(ns marketentry.governor
  "Market-Entry Compliance Governor -- the independent compliance layer
  that earns the MarketEntry-LLM the right to commit. The LLM has no
  notion of Republic of Botswana procurement law, whether a claimed
  engagement fee actually equals base + months x rate, whether the
  engagement's own declared citizen-ownership/arrangement structure is
  ineligible under the Public Procurement Act, 2021's own s.76(1)/
  s.78(1) citizen reservation and preference scale, whether Botswana
  Unified Revenue Service (BURS) Taxpayer Identification Number (TIN)
  registration has been verified for a filing that requires it,
  whether a foreign-owned engagement has actually acknowledged
  Botswana Investment and Trade Centre (BITC) / Botswana One Stop
  Service Centre (BOSSC) facilitation, or when a draft stops being a
  draft and becomes a real-world IPMS tender response, so this MUST be
  a separate system able to *reject* a proposal and fall back to HOLD.

  `:itonami.blueprint/governor` is `:market-entry-compliance-governor`
  (shared family keyword on blueprints).

  This blueprint's own text (docs/business-model.md Trust Controls:
  'any actual portal registration or filing submission requires
  Market-Entry Compliance Governor clearance and always escalates to
  human sign-off'; 'a false or fabricated regulatory-requirement claim
  is a HARD hold') names exactly the checks below.

  Seven checks, in priority order, ALL HARD violations: a human
  approver CANNOT override them. The confidence/actuation gate is
  SOFT: it asks a human to look (low confidence / actuation), and the
  human may approve -- but see `marketentry.phase`: for `:stake
  :actuation/draft-filing`/`:actuation/submit-filing` NO phase ever
  allows auto-commit either. Two independent layers agree that
  actuation is always a human call.

  ⚠️ 'PPRA' below means, exclusively, Botswana's own Public Procurement
  Regulatory Authority -- the identical acronym independently names
  Kenya's and Pakistan's own, completely unrelated, national
  procurement regulators. See `marketentry.facts` namespace docstring
  for the full disambiguation and the `ppra-reference-disambiguated?`
  regression guard.

    1. Spec-basis                  -- did the jurisdiction proposal cite
                                       an OFFICIAL source
                                       (`marketentry.facts`), or invent
                                       one?
    2. Evidence incomplete         -- for `:filing/draft`/
                                       `:filing/submit`, has the
                                       jurisdiction actually been
                                       assessed with a full evidence
                                       checklist on file? (includes CIPA
                                       Certificate of Incorporation and
                                       Contractors' Register listing --
                                       see `marketentry.facts`.)
    3. Reservation ineligible      -- for `:filing/submit`, when the
                                       engagement declares
                                       `:reserved-category? true`,
                                       INDEPENDENTLY recompute whether
                                       the engagement's own declared
                                       citizen-ownership/arrangement
                                       structure classifies as
                                       `:ineligible` under the Public
                                       Procurement Act, 2021's own
                                       s.78(1) descending preference
                                       scale, and HARD-hold if so.
                                       FLAGSHIP check for this
                                       jurisdiction -- an ORDERED
                                       TIER-CLASSIFICATION recompute
                                       (no date arithmetic, no value
                                       threshold, no supplier-registry
                                       read at all), a check SHAPE
                                       genuinely different from every
                                       prior sibling's, and
                                       category/exemption-SCOPE-gated
                                       (only a `:reserved-category?`,
                                       non-`:reservation-exempt?`
                                       engagement has a citizen-
                                       reservation obligation at all --
                                       s.76(1)). See `marketentry.facts`
                                       / `marketentry.registry`.
    4. Engagement fee mismatch     -- for `:filing/submit`,
                                       INDEPENDENTLY recompute whether
                                       the engagement's own `:claimed-
                                       fee` equals `base-fee +
                                       monthly-rate x monitoring-
                                       months` -- honest reapplication
                                       of the ground-truth-recompute
                                       discipline sibling actors use.
    5. TIN registration
       unverified                   -- for `:filing/submit`, when the
                                       engagement declares
                                       `:requires-tin-registration?
                                       true`, INDEPENDENTLY check
                                       `:tin-registered?`. CONDITIONAL
                                       on the engagement's own ground
                                       truth. Grounded in the Income Tax
                                       Act, CAP 52:01's Fifth Schedule
                                       (s.56) employer-registration duty
                                       and BURS's own general Taxpayer
                                       Identification Number (TIN)
                                       registration process (see
                                       `marketentry.facts`).
    6. BITC/BOSSC facilitation
       unacknowledged                -- for `:filing/submit`, when the
                                       engagement declares
                                       `:foreign-owned? true`,
                                       INDEPENDENTLY check
                                       `:bitc-facilitation-acknowledged?`.
                                       CONDITIONAL on the engagement's
                                       own ground truth -- a no-op for a
                                       Motswana-owned engagement.
                                       Grounded in the Botswana
                                       Investment and Trade Centre
                                       (BITC)'s own operational
                                       description of the Botswana One
                                       Stop Service Centre (BOSSC) (see
                                       `marketentry.facts`). BITC/BOSSC
                                       is a FACILITATION gate, never a
                                       substitute for the CIPA
                                       business-registration requirement
                                       check 2 above already enforces
                                       for every engagement, foreign or
                                       domestic -- this check never
                                       treats BITC as the registrar of
                                       record.
    7. Confidence floor / actuation
       gate                          -- LLM confidence below threshold,
                                       OR the op is `:filing/draft`/
                                       `:filing/submit` (REAL acts)
                                       -> escalate.

  Two more guards, double-draft/double-submit prevention, are enforced
  off dedicated `:drafted?`/`:submitted?` facts (never a `:status`
  value)."
  (:require [marketentry.facts :as facts]
            [marketentry.registry :as registry]
            [marketentry.store :as store]))

(def confidence-floor 0.6)

(def high-stakes
  "Stakes grave enough to always require a human, even when clean.
  Drafting a real IPMS tender-response package and submitting it are
  the two real-world actuation events this actor performs."
  #{:actuation/draft-filing :actuation/submit-filing})

;; ----------------------------- checks -----------------------------

(defn- spec-basis-violations
  "A `:jurisdiction/assess` (or `:filing/draft`/`:filing/submit`)
  proposal with no spec-basis citation is a HARD violation -- never
  invent a jurisdiction's market-entry requirements."
  [{:keys [op]} proposal]
  (when (contains? #{:jurisdiction/assess :filing/draft :filing/submit} op)
    (let [value (:value proposal)]
      (when (or (empty? (:cites proposal))
                (and (contains? value :spec-basis) (nil? (:spec-basis value))))
        [{:rule :no-spec-basis
          :detail "公式spec-basisの引用が無い提案は法域要件として扱えない"}]))))

(defn- evidence-incomplete-violations
  "For `:filing/draft`/`:filing/submit`, the jurisdiction's required
  registration evidence must actually be satisfied."
  [{:keys [op subject]} st]
  (when (contains? #{:filing/draft :filing/submit} op)
    (let [e (store/engagement st subject)
          assessment (store/assessment-of st subject)]
      (when-not (and assessment
                     (facts/required-evidence-satisfied?
                      (:jurisdiction e) (:checklist assessment)))
        [{:rule :evidence-incomplete
          :detail "法域の必要書類(CIPA登録/BURS TIN登録/Contractors' Register登録/引用証明等)が充足していない状態での提案"}]))))

(defn- reservation-ineligible-violations
  "For `:filing/submit`, when the engagement declares
  `:reserved-category? true`, INDEPENDENTLY recompute whether its own
  declared citizen-ownership/arrangement structure classifies as
  `:ineligible` under the Public Procurement Act, 2021's own s.78(1)
  descending preference scale -- the flagship check this vertical
  adds. Category/exemption-scope-gated (a no-op for a non-reserved or
  exempt engagement): the Act's own s.76(1) reservation only applies
  'except as otherwise provided under this Act'."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when (registry/reservation-ineligible? e)
        [{:rule :reservation-ineligible
          :detail (str subject " の宣言された市民所有比率/取極め("
                      (:citizen-ownership-pct e) "%, joint-venture?="
                      (:joint-venture? e) ", citizen-subcontractor-association?="
                      (:citizen-subcontractor-association? e)
                      ")はPublic Procurement Act, 2021 s.78(1)の優先順位区分いずれにも該当せず、"
                      "s.76(1)により留保された当該調達区分への参入は進められない")}]))))

(defn- engagement-fee-mismatch-violations
  "For `:filing/submit`, INDEPENDENTLY recompute whether the
  engagement's own claimed fee equals base + months x rate."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when-not (registry/engagement-fee-matches-claim? e)
        [{:rule :engagement-fee-mismatch
          :detail (str subject " の申告手数料(" (:claimed-fee e)
                      ")が独立再計算値(" (registry/compute-engagement-fee e) ")と一致しない")}]))))

(defn- tin-registration-unverified-violations
  "For `:filing/submit`, when the engagement declares
  `:requires-tin-registration? true`, INDEPENDENTLY check
  `:tin-registered?` -- CONDITIONAL on the engagement's own ground
  truth. Grounded in the Income Tax Act, CAP 52:01's Fifth Schedule
  (s.56) employer-registration duty and BURS's own general Taxpayer
  Identification Number (TIN) registration process."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when (and (true? (:requires-tin-registration? e))
                 (not (true? (:tin-registered? e))))
        [{:rule :tin-registration-unverified
          :detail (str subject " はBotswana Unified Revenue Service(Income Tax Act CAP 52:01)へのTIN登録確認を要するが未確認 -- 提出提案は進められない")}]))))

(defn- bitc-facilitation-unacknowledged-violations
  "For `:filing/submit`, when the engagement declares
  `:foreign-owned? true`, INDEPENDENTLY check
  `:bitc-facilitation-acknowledged?` -- CONDITIONAL on the engagement's
  own ground truth, a no-op (never fires) for a domestic (non-foreign-
  owned) engagement. Botswana Investment and Trade Centre (BITC)
  operates the Botswana One Stop Service Centre (BOSSC), which
  FACILITATES (bundles) company/business registration, trade/
  industrial licenses, visas, work/residence permits, tax registration
  and land access for a foreign investor -- BITC does NOT itself
  register companies (the Companies and Intellectual Property
  Authority, CIPA, remains the registrar of record -- see
  `evidence-incomplete-violations` above, which already requires a
  CIPA Certificate of Incorporation for every engagement, foreign or
  domestic). This check only confirms a foreign-owned engagement has
  actually gone through BITC/BOSSC facilitation; it never substitutes
  for, and never relaxes, the CIPA registration requirement."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when (and (true? (:foreign-owned? e))
                 (not (true? (:bitc-facilitation-acknowledged? e))))
        [{:rule :bitc-facilitation-unacknowledged
          :detail (str subject " は外国資本案件のためBotswana Investment and Trade Centre(BITC)のBotswana One Stop Service Centre(BOSSC)投資家向け窓口確認を要するが未確認 -- 提出提案は進められない(CIPA登録要件とは別)")}]))))

(defn- already-drafted-violations
  "For `:filing/draft`, refuses to draft the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/draft)
    (when (store/engagement-already-drafted? st subject)
      [{:rule :already-drafted
        :detail (str subject " は既にドラフト済み")}])))

(defn- already-submitted-violations
  "For `:filing/submit`, refuses to submit the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (when (store/engagement-already-submitted? st subject)
      [{:rule :already-submitted
        :detail (str subject " は既に提出済み")}])))

(defn check
  "Censors a MarketEntry-LLM proposal against the governor rules.
  Returns {:ok? bool :violations [..] :confidence c :escalate? bool
  :high-stakes? bool :hard? bool}."
  [request _context proposal st]
  (let [hard (into []
                   (concat (spec-basis-violations request proposal)
                           (evidence-incomplete-violations request st)
                           (reservation-ineligible-violations request st)
                           (engagement-fee-mismatch-violations request st)
                           (tin-registration-unverified-violations request st)
                           (bitc-facilitation-unacknowledged-violations request st)
                           (already-drafted-violations request st)
                           (already-submitted-violations request st)))
        conf (:confidence proposal 0.0)
        low? (< conf confidence-floor)
        stakes? (boolean (high-stakes (:stake proposal)))
        hard? (boolean (seq hard))]
    {:ok?          (and (not hard?) (not low?) (not stakes?))
     :violations   hard
     :confidence   conf
     :hard?        hard?
     :escalate?    (and (not hard?) (or low? stakes?))
     :high-stakes? stakes?}))

(defn hold-fact
  "The audit fact written when a proposal is rejected (HOLD)."
  [request context verdict]
  {:t          :governor-hold
   :op         (:op request)
   :actor      (:actor-id context)
   :subject    (:subject request)
   :disposition :hold
   :basis      (mapv :rule (:violations verdict))
   :violations (:violations verdict)
   :confidence (:confidence verdict)})

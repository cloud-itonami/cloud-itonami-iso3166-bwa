(ns marketentry.registry
  "Pure-function market-entry filing-draft + filing-submit record
  construction -- an append-only market-entry book-of-record draft.

  Like every sibling actor's registry, there is no single international
  reference-number standard for a public-procurement market-entry
  filing -- every jurisdiction assigns its own format. This namespace
  does NOT invent one; it builds a jurisdiction-scoped sequence number
  and validates the record's required fields, the same honest,
  non-fabricating discipline `marketentry.facts` uses.

  `engagement-fee-matches-claim?` is an HONEST reapplication of the
  SAME ground-truth-recompute DISCIPLINE sibling actors use (verify a
  claimed monetary total against the entity's own recorded quantity x
  unit fields), reapplied to a market-entry engagement fee line.

  `reservation-ineligible?` is THIS vertical's own new ground-truth
  check, grounding BWA's flagship governor check
  (`marketentry.governor/reservation-ineligible-violations`): the
  Public Procurement Act, 2021 (own primary text, see
  `marketentry.facts`) s.76(1) reserves ALL works/services/supplies
  procurement for a citizen or citizen contractor by default, and
  s.78(1) lays out a descending FOUR-TIER preference scale over
  citizen-ownership/arrangement structures. This is a DIFFERENT check
  SHAPE from every prior sibling: not a turnover-scaled formula
  (Bulgaria), not a flat statutory threshold (Albania), not a boolean
  registry-membership read of the SUPPLIER (Azerbaijan/Armenia/
  Bolivia), not a 3-tier CONTRACT-VALUE classification (Antigua and
  Barbuda), not a bid-evaluation price-adjustment recompute (Benin),
  not a struck-off company-registry legal-capacity boolean (Belize),
  not a validity-WINDOW expiry-date recompute (Barbados), not a pure
  date-precedence/ordering check (Brunei), and not a SECTOR
  set-membership gate (Bhutan) -- it is an ORDERED TIER-CLASSIFICATION
  recompute: given the engagement's own declared ownership percentage
  plus two arrangement booleans, independently classify which (if any)
  of the Act's own s.78(1) four descending tiers the engagement
  qualifies for, then gate on whether that tier is :ineligible AND the
  procurement is actually under the default reservation. The classifier
  itself is pure percentage-comparison + boolean logic, no date
  arithmetic, no external registry read, and no monetary formula.

  It is also entity/category-scope-gated, like Bhutan's FDI check: a
  no-op (false) unless the engagement's own `:reserved-category?`
  ground truth is true (the Act's own s.76(1) opens with 'Except as
  otherwise provided under this Act' -- not every procurement activity
  is necessarily brought within scope) and `:reservation-exempt?` is
  not true (an external-obligation/derogation ground under the Act's
  own ss.140-143 on file). The negative/classification INPUTS
  (ownership percentage, joint-venture flag, subcontractor-association
  flag) are never hardcoded here -- they are always the engagement's
  own declared ground truth, the same discipline
  `required-evidence-satisfied?` uses for evidence checklists.

  This namespace is pure data + pure functions -- no I/O, no network
  call to any real IPMS, CIPA, BURS or PPRA system. It builds the
  RECORD an operator would keep, not the act of submitting an IPMS
  tender response itself (that is `marketentry.operation`'s
  `:filing/submit`, always human-gated -- see README Actuation)."
  (:require [clojure.string :as str]))

(defn- unsigned-certificate
  "Every certificate this actor produces is UNSIGNED -- signature is
  the market-entry operator's act, not this actor's."
  [kind subject record-id]
  {"@context" ["https://www.w3.org/ns/credentials/v2"]
   "type" ["VerifiableCredential" kind]
   "credentialSubject" {"id" subject "record" record-id}
   "proof" nil
   "issued_by_registry" false
   "status" "draft-unsigned"})

(defn- zero-pad [n w]
  (let [s (str n)]
    (str (apply str (repeat (max 0 (- w (count s))) "0")) s)))

(defn compute-engagement-fee
  "The ground-truth engagement fee for `engagement`'s own `:base-fee`
  and `:monitoring-months` x `:monthly-rate` -- a single flat
  base + months x rate calculation, not a full pricing engine."
  [{:keys [base-fee monthly-rate monitoring-months]}]
  (+ (double base-fee)
     (* (double monthly-rate) (double monitoring-months))))

(defn engagement-fee-matches-claim?
  "Does `engagement`'s own `:claimed-fee` equal the independently
  recomputed `compute-engagement-fee`?"
  [{:keys [claimed-fee] :as engagement}]
  (== (double claimed-fee) (compute-engagement-fee engagement)))

(defn citizen-preference-tier
  "INDEPENDENTLY recompute the engagement's own Public Procurement Act,
  2021 s.78(1) preference tier from ground-truth ownership/arrangement
  facts -- returns one of `:citizen-joint-venture`,
  `:sole-citizen-contractor`, `:majority-citizen-joint-venture`,
  `:citizen-subcontractor-association` (the Act's own descending order),
  or `:ineligible` when none of the s.78(1) tiers apply. Pure
  percentage-comparison + boolean classification, no I/O, no arithmetic
  beyond a single `> 50` comparison (s.78(1)(c) 'majority shares').

  `:citizen-ownership-pct` is the engagement's own declared citizen
  ownership percentage (0-100). The Act's own s.2 Interpretation defines
  'citizen contractor' as wholly (100%) owned and controlled by
  Botswana citizens -- so 100% ownership alone qualifies for tier (b)
  'sole citizen contractors', and only becomes tier (a) 'joint ventures
  between citizen contractors' when `:joint-venture?` is also true
  (i.e. the engagement is itself a joint venture between two or more
  wholly-citizen-owned parties, not a single company)."
  [{:keys [citizen-ownership-pct joint-venture? citizen-subcontractor-association?]}]
  (cond
    (and joint-venture? (= 100 citizen-ownership-pct))
    :citizen-joint-venture

    (= 100 citizen-ownership-pct)
    :sole-citizen-contractor

    (and joint-venture? citizen-ownership-pct (> citizen-ownership-pct 50))
    :majority-citizen-joint-venture

    citizen-subcontractor-association?
    :citizen-subcontractor-association

    :else
    :ineligible))

(defn reservation-ineligible?
  "Is `engagement` INELIGIBLE under the Public Procurement Act, 2021's
  own default citizen reservation (s.76(1))?

  A no-op (false) unless `:reserved-category?` is true (the Act's own
  s.76(1) opens with 'Except as otherwise provided under this Act' --
  this engagement's own ground truth for whether it falls within the
  reservation) AND `:reservation-exempt?` is not true (an external-
  obligation/derogation ground on file, ss.140-143). Missing
  `:citizen-ownership-pct` or the arrangement booleans, for a reserved,
  non-exempt engagement, is also never treated as ineligible HERE --
  `citizen-preference-tier` already returns `:ineligible` for a nil
  percentage via the same `=`/`>` comparisons failing to match, and
  that in turn only fires the gate when the category/exemption ground
  truth is itself present and reserved -- the same 'missing data still
  routes through the SAME classification, gated on ground truth' shape
  `fdi-sector-restricted?` uses in the Bhutan sibling."
  [{:keys [reserved-category? reservation-exempt?] :as engagement}]
  (boolean
   (and (true? reserved-category?)
        (not (true? reservation-exempt?))
        (= :ineligible (citizen-preference-tier engagement)))))

(defn register-draft
  "Validate + construct the FILING-DRAFT registration DRAFT -- the
  market-entry operator's own act of preparing an IPMS tender response
  package. Pure function -- does not touch any real IPMS, CIPA, BURS or
  PPRA system."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "draft: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "draft: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "draft: sequence must be >= 0" {})))
  (let [draft-number (str (str/upper-case jurisdiction) "-DFT-" (zero-pad sequence 6))
        record {"record_id" draft-number
                "kind" "filing-draft"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "draft_number" draft-number
     "certificate" (unsigned-certificate "FilingDraft" draft-number draft-number)}))

(defn register-submit
  "Validate + construct the FILING-SUBMIT registration DRAFT -- the
  market-entry operator's own act of actually submitting an IPMS tender
  response (always human-gated upstream)."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "submit: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "submit: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "submit: sequence must be >= 0" {})))
  (let [submit-number (str (str/upper-case jurisdiction) "-SUB-" (zero-pad sequence 6))
        record {"record_id" submit-number
                "kind" "filing-submit"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "submit_number" submit-number
     "certificate" (unsigned-certificate "FilingSubmit" submit-number submit-number)}))

(defn append [history result]
  (conj (vec history) (get result "record")))

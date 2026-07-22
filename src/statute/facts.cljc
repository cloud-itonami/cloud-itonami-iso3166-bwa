(ns statute.facts
  "General-law compliance catalog for the Republic of Botswana (BWA) --
  extends this repo's existing `marketentry.facts` (public-procurement
  market-entry only, narrow scope) with a second, orthogonal catalog of
  statutes a company operating in this jurisdiction must generally
  track for compliance. Mirrors cloud-itonami-iso3166-aze/-bih/-jpn/
  -deu/-bgr/-blr/-bol/-blz/-atg/-brb/-brn/-btn/-ben's `statute.facts`
  (ADR-2607141700, cloud-itonami-compliance-fact-federation).

  Every entry below cites an OFFICIAL Botswana government source, and
  every citation reflects PRIMARY TEXT this iteration actually fetched
  (either a direct HTTP 200 fetch, or `curl` + `pdftotext -layout` on a
  PDF downloaded directly from the official domain) and read in full,
  not a secondary summary. Verified 2026-07-21:

  - **The Companies Act, CHAPTER 42:01** -- downloaded in full as a PDF
    directly from the Companies and Intellectual Property Authority's
    (CIPA) own official site (`cipa.co.bw/wp-content/uploads/2026/02/
    Companies-Act-CAP-42-01.pdf`) and read via `pdftotext -layout`
    (366 pages). HIGH confidence, primary text: own s.10 'The Registrar'
    -- 'There shall be a Registrar of Companies who shall be a public
    officer, and who shall, subject to the control of the Minister, be
    responsible for the administration of this Act'; Part II
    (ss.19-24, 'Incorporation -- Essential Requirements') own text
    governs company registration itself (application, registration,
    certificate of incorporation, conclusive evidence, separate legal
    personality); Part XXIV (from s.345) 'External Companies' own
    text requires a company incorporated OUTSIDE Botswana to lodge
    registration with the Registrar within one month after it
    establishes a place of business or commences to carry on business
    in Botswana. This is the statute this repo's `marketentry.facts`
    `business-registration-spec-basis` cites for CIPA's day-to-day
    administration of the Registrar function -- see `marketentry.facts`
    namespace docstring for the honest gap: this iteration could not
    independently verify the Companies and Intellectual Property
    Authority Act, 2011's own primary text (CIPA's OWN 'About Us' page
    claims CIPA itself was established by that separate 2011 Act, a
    claim this iteration did not cross-check against that Act's own
    primary text).
  - **The Public Procurement Act, 2021 (No. 24 of 2021)** -- downloaded
    in full as a PDF directly from the Public Procurement Regulatory
    Authority's (PPRA, formerly PPADB) own official site
    (`www.ppadb.co.bw/Manuals  Acts/Public Procurement Act, 2021.pdf`,
    read via a Wayback Machine capture because the live domain refused
    every connection attempt from this session's network) and read via
    `pdftotext -layout` (53 pages, Botswana Extraordinary Government
    Gazette, 26 November 2021). HIGH confidence, primary text: own s.6
    'Continuation of Authority' (PPRA is a continuation of the former
    PPADB, not a new body); own s.55 establishes the 'National
    eProcurement System' (PPRA's own live implementation is the
    Integrated Procurement Management System, IPMS,
    `ipms.ppadb.co.bw`); own Part XIII (ss.91-103) 'Registration of
    Contractors'; own Part XI (ss.72-81) 'Reservation and Preferential
    Treatment', s.76(1) 'Except as otherwise provided under this Act,
    all works, services and supplies procurement shall be reserved for
    a citizen or citizen contractor', s.78(1) descending preference
    scale. This is the statute this repo's `marketentry.facts`
    `:legal-basis` and `citizen-reservation-spec-basis` cite for the
    entire IPMS procurement framework and this vertical's flagship
    governor check.
  - **The Botswana Unified Revenue Service Act, CHAPTER 53:03** --
    downloaded in full as a PDF directly from BURS's own official site
    (`www.burs.org.bw`) and read via `pdftotext -layout` (13 pages).
    HIGH confidence, primary text: own s.3 'There is hereby established
    a body, to be known as the Botswana Unified Revenue Service, which
    shall be a body corporate with a common seal...'; own s.4(1) 'The
    Revenue Service shall be responsible for the assessment and
    collection of tax on behalf of the Government'. This is the statute
    this repo's `marketentry.facts` `corporate-number-spec-basis` cites
    for BURS's own establishment and mandate.
  - **The Income Tax Act, CHAPTER 52:01** -- downloaded in full as a
    PDF directly from BURS's own official site and read via
    `pdftotext -layout` (130 pages). HIGH confidence, primary text:
    own Fifth Schedule (s.56), paragraph 2 'Registration of employers'
    -- 'Every person who pays or becomes liable to pay remuneration to
    any employee shall register as an employer with the Commissioner
    General'. This iteration did NOT independently locate a general
    'every taxable person must register for a Taxpayer Identification
    Number' provision within the Act's own primary text beyond this
    employer-registration duty -- BURS's own 'Tax Registration'
    webpage (fetched directly) separately states, in its own words,
    that a 2011 amendment made registration compulsory above a P36,000
    annual taxable-income threshold, a claim this iteration cites to
    that webpage rather than to an independently-read section number
    (an honest, explicit gap, see `marketentry.facts` namespace
    docstring).
  - **Employment Act -- deliberately NOT included in this catalog.**
    This iteration searched for Botswana's Employment Act's own primary
    text across every official channel it could reach: the Ministry
    of Labour and Home Affairs' own domain (`www.mlha.gov.bw`) refused
    every connection attempt (and has no Wayback Machine snapshot at
    all); `www.gov.bw`'s own labour-and-employment service-listing page
    returned intermittent 503s and, when it DID load, listed dozens of
    labour-related services (Workers Compensation, Trade Union
    registration, Trade Dispute mediation/arbitration, Labour
    Inspection) without a direct link to the Employment Act itself;
    `elaws.gov.bw` (a guessed legislation-portal domain) refused
    connections; this session's web-search budget was already
    exhausted before this gap was discovered, and the DuckDuckGo HTML
    fallback search triggered an anti-bot 'anomaly' challenge page on
    its first and only attempt (not retried, per this task's own
    instructions). Rather than guess the Act's CAP number or cite a
    section this iteration never actually read, this catalog omits the
    Employment Act entirely -- an honest, explicit gap, not a
    fabricated citation.

  A law not in this table has NO spec-basis, full stop; extend
  `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of statute entries. `:statute/url` + `:statute/law-number`
  are the citation the governor requires before any compliance-fact
  proposal referencing this law can commit."
  {"BWA"
   [{:statute/id "bwa.companies-act-cap-42-01"
     :statute/title "The Companies Act, CHAPTER 42:01"
     :statute/jurisdiction "BWA"
     :statute/kind :law
     :statute/law-number "s.10 'The Registrar' -- 'There shall be a Registrar of Companies who shall be a public officer... responsible for the administration of this Act'; Part II (ss.19-24) governs company registration; Part XXIV (from s.345) governs registration of external companies. Day-to-day administration by the Companies and Intellectual Property Authority (CIPA)"
     :statute/url "https://www.cipa.co.bw/wp-content/uploads/2026/02/Companies-Act-CAP-42-01.pdf"
     :statute/url-provenance :cipa-official-site
     :statute/enacted-date "2007-07-03"
     :statute/retrieved-at "2026-07-21"
     :statute/topic #{:corporate-governance :incorporation}}
    {:statute/id "bwa.public-procurement-act-2021"
     :statute/title "The Public Procurement Act, 2021 (No. 24 of 2021)"
     :statute/jurisdiction "BWA"
     :statute/kind :law
     :statute/law-number "s.6 'Continuation of Authority' (Public Procurement Regulatory Authority, PPRA, continues the former PPADB); s.55 'National eProcurement System'; Part XI (ss.72-81) 'Reservation and Preferential Treatment', s.76(1) default citizen reservation, s.78(1) descending preference scale; Part XIII (ss.91-103) 'Registration of Contractors'. Came into effect 14 April 2022 (own commencement / PPRA's own 'About Us' page)"
     :statute/url "https://web.archive.org/web/20251213070010/http://www.ppadb.co.bw/Manuals%20%20Acts/Public%20Procurement%20Act,%202021.pdf"
     :statute/url-provenance :ppra-official-site-wayback-capture
     :statute/enacted-date "2021-11-26"
     :statute/retrieved-at "2026-07-21"
     :statute/topic #{:public-procurement :citizen-empowerment}}
    {:statute/id "bwa.burs-act-cap-53-03"
     :statute/title "The Botswana Unified Revenue Service Act, CHAPTER 53:03"
     :statute/jurisdiction "BWA"
     :statute/kind :law
     :statute/law-number "s.3 'Establishment of the Revenue Service' -- 'There is hereby established a body, to be known as the Botswana Unified Revenue Service, which shall be a body corporate'; s.4(1) 'The Revenue Service shall be responsible for the assessment and collection of tax on behalf of the Government'"
     :statute/url "https://www.burs.org.bw/index.php/treaties-and-legislation/legislation/revenue-laws?download=15:cap-53-03-botswana-unified-revenue-service-act"
     :statute/url-provenance :burs-official-site
     :statute/enacted-date "2004-08-01"
     :statute/retrieved-at "2026-07-21"
     :statute/topic #{:tax :public-finance}}
    {:statute/id "bwa.income-tax-act-cap-52-01"
     :statute/title "The Income Tax Act, CHAPTER 52:01"
     :statute/jurisdiction "BWA"
     :statute/kind :law
     :statute/law-number "Fifth Schedule (s.56), paragraph 2 'Registration of employers' -- 'Every person who pays or becomes liable to pay remuneration to any employee shall register as an employer with the Commissioner General'. General Taxpayer Identification Number (TIN) registration threshold (P36,000 taxable income/year) per BURS's own 'Tax Registration' webpage, not independently located as a section within this Act's own primary text (honest gap, see namespace docstring)"
     :statute/url "https://www.burs.org.bw/index.php/treaties-and-legislation/legislation/revenue-laws?download=18:cap-52-01-income-tax-act"
     :statute/url-provenance :burs-official-site
     :statute/enacted-date "1995-07-01"
     :statute/retrieved-at "2026-07-21"
     :statute/topic #{:tax}}]})

(defn spec-basis
  "The jurisdiction's statute vector, or nil -- nil means NO spec-basis
  for that jurisdiction yet."
  [iso3]
  (get catalog iso3))

(defn coverage
  "Honest coverage report, same shape/discipline as `marketentry.facts/coverage`:
  never report a missing jurisdiction as covered."
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-bwa statute.facts Wave 0 (ADR-2607141700): "
                 (count (get catalog "BWA")) " BWA statutes seeded with an "
                 "official citation. Extend "
                 "`statute.facts/catalog`, never fabricate a law-id or URL.")})))

(defn by-topic
  "Statutes for `iso3` tagged with `topic` (e.g. :tax, :public-procurement)."
  [iso3 topic]
  (filterv #(contains? (:statute/topic %) topic) (spec-basis iso3)))

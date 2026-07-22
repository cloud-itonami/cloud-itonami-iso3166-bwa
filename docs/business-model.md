# Business Model: Independent Public-Sector Market-Entry & Procurement Compliance Service — Botswana

## Classification

- Repository: `cloud-itonami-iso3166-bwa`
- ISO 3166: `BWA` (Botswana)
- Activity: public-procurement market-entry and ongoing regulatory-
  compliance navigation for an already-incorporated operator
- Social impact: [:motswana-sme-market-access :public-spend-transparency :cross-border-friction-reduction]

## Customer

- an already-incorporated `cloud-itonami-cofog-{code}` /
  `cloud-itonami-isco-{code}` / `cloud-itonami-unspsc-{segment}` /
  `cloud-itonami-{ISIC}` operator wanting to bid on a Botswanan
  public contract
- a foreign SME or civic-tech vendor entering the public sector in
  Botswana for the first time
- a `cloud-itonami-M6910` client that has just completed incorporation and
  now needs public-sector market access

## Offer

- registration walkthrough for the Integrated Procurement Management
  System (IPMS, ipms.ppadb.co.bw), the online supplier-registration
  gateway operated by Botswana's own Public Procurement Regulatory
  Authority (PPRA -- NOT Kenya's or Pakistan's identically-acronymed,
  unrelated regulators of the same name; the SAME legal body as the
  former PPADB, simply renamed under the Public Procurement Act, 2021,
  No. 24 of 2021, s.6 "Continuation of Authority", commenced 14 April
  2022 -- not a newly-created organization)
- business/tax registration checklist: registration with the Companies
  and Intellectual Property Authority (CIPA) plus a Taxpayer
  Identification Number (TIN) from the Botswana Unified Revenue
  Service (BURS)
- foreign-investor facilitation walkthrough: Botswana Investment and
  Trade Centre (BITC)'s Botswana One Stop Service Centre (BOSSC), which
  bundles access to company/business registration, trade/industrial
  licenses, visas, work/residence permits, tax registration and land
  access for a foreign-owned engagement -- BITC FACILITATES; CIPA
  remains the registrar of record
- local-content / preferential-procurement navigation: Botswanan
  public-procurement citizen-reservation and preference provisions
  (Public Procurement Act, 2021 s.76(1)/s.78(1)) for local suppliers on
  qualifying tenders
- ongoing regulatory-change monitoring subscription
- compliance-audit export package for the client's own records

## Revenue

- per-engagement market-entry fee (one-time registration + checklist
  completion)
- recurring regulatory-change monitoring subscription
- compliance-audit export package

## Trust Controls

- any actual portal registration or filing submission requires
  Market-Entry Compliance Governor clearance and always escalates to
  human sign-off (`:filing/submit` is never automated at any phase)
- a false or fabricated regulatory-requirement claim is a HARD hold that
  cannot be overridden by human approval alone — it must be corrected
  against a cited official source first
- this service does **not** provide legal or tax advice; characterization
  and filing on the client's behalf beyond checklist/draft assistance
  routes to Botswanan-licensed counsel or a registered agent
- every requirement cites the official portal or regulation, never
  invented
- **PPRA disambiguation**: any regulatory-requirement claim naming
  "PPRA" must be Botswana-qualified -- the identical acronym
  independently names Kenya's and Pakistan's own, unrelated, national
  procurement regulators, and citing a bare "PPRA" as if it were
  self-evidently Botswana's own is treated as the same class of
  fabrication risk as inventing a requirement outright
- BITC/Botswana One Stop Service Centre (BOSSC) is modeled strictly as
  a foreign-investor facilitation gate, never as the registrar of
  record -- CIPA registration is required for every engagement
  regardless of whether BITC facilitation was used

## Boundary with adjacent actors (read before forking)

- **`com-etzhayyim-ooyake`** (etzhayyim/root): read-only civic-wayfinding
  mirror of government structure, non-commercial, barred from acting as
  or for the government (G3 impersonation ban). This blueprint is
  commercial and never claims to be an official channel.
- **`matsurigoto`** (etzhayyim/root): sovereign e-government statecraft —
  literally the government, for etzhayyim's own covenant or an adopting
  nation-state. This blueprint is an independent operator the government
  contracts with or that bids into its procurement — never the
  government.
- **`com-etzhayyim-toritsugi`** (etzhayyim/root): guides a consenting
  INDIVIDUAL citizen through their OWN procedure, non-profit,
  donation-only. This blueprint's client is a business operator, not an
  individual citizen, and it is commercial.
- **`legal-entity.etzhayyim.com`**: read-only aggregated company-registry
  data, no execution. This blueprint executes (gated) registrations.
- **`cloud-itonami-M6910`**: helps a client BECOME a legal entity
  (incorporation, ISIC 6910) — a prior, different regulatory phase
  (company law). This blueprint assumes incorporation is already done and
  handles public-procurement market entry (a different regulatory domain).
- **`cloud-itonami-cofog-{code}`**: a jurisdiction-agnostic operator
  template for ONE public function. This blueprint is the orthogonal
  jurisdiction-specific axis — the two compose (fork a COFOG-function
  blueprint AND this one to operate in Botswana).

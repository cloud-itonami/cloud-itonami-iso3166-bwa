# cloud-itonami-iso3166-bwa

Open ISO 3166 Blueprint for **BWA**: Republic of Botswana --
**`:implemented`**.

This repository designs **and implements** a forkable OSS business for
an independent public-sector market-entry consultant: an already-
incorporated operator (e.g. a `cloud-itonami-cofog-{code}`,
`cloud-itonami-isco-{code}`, `cloud-itonami-unspsc-{segment}` or
`cloud-itonami-{ISIC}` blueprint fork) gets a **MarketEntry-LLM**
advisor + independent **Market-Entry Compliance Governor** to navigate
Botswana's public-procurement registration, business/tax registration
and citizen-reservation/preference rules, so the operator can win and
service a government contract without hiring a full in-house
compliance department.

Built on this workspace's
[`langgraph`](https://github.com/kotoba-lang/langgraph) StateGraph
runtime (portable `.cljc`, supervised superstep loop, interrupts,
Datomic/in-mem checkpoints) -- the same actor pattern as every prior
actor in this fleet, e.g. `cloud-itonami-iso3166-ago` /
`cloud-itonami-iso3166-btn` -- here it is **MarketEntry-LLM ⊣
Market-Entry Compliance Governor**.

## ⚠️ PPRA disambiguation (read this first)

**"PPRA" is NOT unique to Botswana.** The identical acronym
independently names Kenya's and Pakistan's own, completely separate,
national procurement regulators. Every "PPRA" in this repository --
in `marketentry.facts`, `marketentry.governor`, this README, and
`docs/*` -- means **Botswana's own** Public Procurement Regulatory
Authority, the s.6 "Continuation of Authority" successor to the former
Public Procurement and Asset Disposal Board (PPADB) under the Public
Procurement Act, 2021 (No. 24 of 2021, commenced 14 April 2022) --
never Kenya's, never Pakistan's. Provenance is `ipms.ppadb.co.bw` (the
live Integrated Procurement Management System portal) and
`botswanalaws.com` / Wayback-archived `ppadb.co.bw` captures -- **never
`ppra.co.bw`**, which was unreachable (connection refused) at the time
this catalog was verified and was never independently corroborated as
Botswana's own domain.
`marketentry.facts/ppra-reference-disambiguated?` and
`ppra-references-in-catalog-disambiguated?` are executable regression
guards against this exact fabrication trap; see
`test/marketentry/facts_test.clj`'s
`bwa-ppra-references-are-disambiguated-from-kenya-and-pakistan`.

## Official surface

- **Business/company registration**: Companies and Intellectual
  Property Authority (CIPA, `cipa.co.bw`) -- a parastatal established
  by an Act of Parliament in 2011, under Botswana's Ministry of Trade
  and Industry. Administers the Registrar of Companies function under
  the Companies Act (CAP 42:01) for incorporation, and the
  Registration of Business Names Act (CAP 42:05) for business-name
  registration. Online Business Registration System (OBRS) launched
  June 2019, `cipa.co.bw/security/ui/start/logon`.
- **Tax registration**: Botswana Unified Revenue Service (BURS,
  `burs.org.bw`) issues Taxpayer Identification Numbers (TIN).
  Registration channel is not modeled as online-only by this catalog
  (form BURS 1 / BURS 1A; both in-person and online-adjacent channels
  are plausible and this repo does not overclaim which).
- **Public procurement**: Botswana's own Public Procurement Regulatory
  Authority (PPRA -- see disambiguation above), established under the
  Public Procurement Act, 2021 (No. 24 of 2021), the SAME legal body
  as the former PPADB simply renamed (Act s.6, not a new entity).
  E-procurement channel is the Integrated Procurement Management
  System (IPMS, `ipms.ppadb.co.bw`) -- modules: Contractor
  Registration, ITT Vetting, E-bidding, Adjudication, Dispute
  Resolution. Bidder/contractor registration via the Contractors'
  Register (Act Part XIII, ss.91-103): licensed/incorporated under the
  Laws of Botswana and domiciled in Botswana (s.92(1)); valid five
  years (s.92(4)). Citizen-reservation/preference scheme under Part XI
  (ss.72-81): s.76(1) reserves procurement for a citizen or citizen
  contractor by default, s.78(1) lays out a descending four-tier
  preference scale.
- **Foreign-investor facilitation**: Botswana Investment and Trade
  Centre (BITC, `bitc.co.bw`) operates the Botswana One Stop Service
  Centre (BOSSC), which **facilitates** (bundles) company/business
  registration, trade/industrial licenses, visas, work/residence
  permits, tax registration and land access for a foreign investor.
  **CIPA remains the registrar of record; BITC does NOT itself
  register companies.** This blueprint models BITC/BOSSC as a
  facilitation/awareness gate for foreign-owned engagements
  specifically, distinct from (and never a substitute for) the
  domestic CIPA registration requirement every engagement must
  satisfy.

See `src/marketentry/facts.cljc`'s own namespace docstring for the
full per-citation provenance trail (which sources were directly
fetched vs. read via Wayback Machine capture because the live domain
refused connections) and the honest gaps it explicitly declines to
paper over (e.g. `rep-spec-basis` is deliberately `nil` for BWA -- the
only representative-shaped mechanism found, Companies Act s.345(e), is
scoped narrowly to an external company's local place-of-business
agent, not a general market-entry representative regime).

## Checks

Seven checks, in priority order, evaluated by `marketentry.governor`
on every `MarketEntry-LLM` proposal. The first six are HARD violations
a human approver cannot override; double-actuation guards are counted
separately. The confidence/actuation gate (item 7) is SOFT -- but see
Actuation below, `:filing/draft`/`:filing/submit` never auto-commit
regardless.

| # | Check | Grounds | Source |
|---|---|---|---|
| 1 | **Spec-basis** -- a `:jurisdiction/assess`/`:filing/draft`/`:filing/submit` proposal must cite an official source, never an invented one | `marketentry.facts/spec-basis` | Public Procurement Act, 2021; Companies Act CAP 42:01; BURS Act CAP 53:03 (see Official surface above) |
| 2 | **Evidence incomplete** -- for draft/submit, the jurisdiction's full required-evidence checklist must be on file | CIPA Certificate of Incorporation, BURS TIN registration confirmation, Contractors' Register listing, citizen-ownership certification where claimed | `marketentry.facts/required-evidence-satisfied?` |
| 3 | **Reservation ineligible** (flagship) -- for submit, when `:reserved-category? true`, independently recompute whether the engagement's own declared citizen-ownership/arrangement structure classifies as `:ineligible` under the Act's own s.78(1) descending preference scale; category/exemption-scope-gated | `marketentry.registry/reservation-ineligible?` | Public Procurement Act, 2021 s.76(1) + s.78(1) |
| 4 | **Engagement fee mismatch** -- for submit, independently recompute `claimed-fee = base-fee + monthly-rate x monitoring-months` | `marketentry.registry/engagement-fee-matches-claim?` | ground-truth recompute (fleet-standard discipline) |
| 5 | **TIN registration unverified** -- for submit, when `:requires-tin-registration? true`, independently check `:tin-registered?` | `marketentry.governor/tin-registration-unverified-violations` | BURS, Income Tax Act CAP 52:01 Fifth Schedule s.56 |
| 6 | **BITC/BOSSC facilitation unacknowledged** -- for submit, when `:foreign-owned? true`, independently check `:bitc-facilitation-acknowledged?`; entity-scope-gated to a foreign-owned engagement (a no-op for a Motswana-owned one) | `marketentry.governor/bitc-facilitation-unacknowledged-violations` | BITC (`bitc.co.bw`), BOSSC's own operational description -- facilitation only, never a substitute for check 2's CIPA requirement |
| 7 | **Confidence floor / actuation gate** (SOFT) -- LLM confidence below 0.6, or the op is `:filing/draft`/`:filing/submit` -> escalate to human | `marketentry.governor/check` | this vertical's own Trust Controls (`docs/business-model.md`) |

Two further double-actuation guards (`already-drafted`,
`already-submitted`) refuse to draft or submit the SAME engagement
twice, enforced off dedicated `:drafted?`/`:submitted?` booleans, never
a `:status` value.

The flagship check (3) is a genuinely different SHAPE from prior
siblings: an ORDERED TIER-CLASSIFICATION recompute over the
engagement's own declared ownership percentage plus two arrangement
booleans against the Act's own four descending tiers -- no date
arithmetic, no value threshold, no supplier-registry read -- see
`marketentry.registry`'s namespace docstring for the full shape
comparison against sibling B-country checks.

## Actuation

**Drafting a real IPMS tender-response package and submitting a real
filing are never autonomous, at any phase, by construction.** Two
independent layers enforce this:

- `marketentry.governor`'s `high-stakes` set
  (`#{:actuation/draft-filing :actuation/submit-filing}`) always
  escalates, regardless of confidence.
- `marketentry.phase`'s phase table (`phase 0` through `phase 3`)
  never puts `:filing/draft` or `:filing/submit` in any phase's
  `:auto` set -- see `marketentry.phase`'s own docstring and
  `test/marketentry/phase_test.clj`'s `filing-submit-never-auto`.

The actor may intake an engagement, assess a jurisdiction and draft a
recommendation; a human market-entry operator is always the one who
actually files a draft or a submission. Grounded directly in this
blueprint's own [`docs/business-model.md`](docs/business-model.md) and
`marketentry.governor`'s own namespace docstring: "any actual portal
registration or filing submission requires Market-Entry Compliance
Governor clearance and always escalates to human sign-off"; "a false
or fabricated regulatory-requirement claim is a HARD hold". `:filing/
draft` and `:filing/submit` apply SEQUENTIALLY to the SAME engagement
record (draft first, submit later) -- matching every sibling
`market-entry-compliance-governor` actor's own sequential shape.

## Core Contract

```text
engagement intake + jurisdiction facts (marketentry.facts, spec-cited)
        |
        v
   ┌────────────────────┐   proposal      ┌──────────────────────────────┐
   │ MarketEntry-LLM     │ ─────────────▶ │ Market-Entry Compliance       │
   │ (sealed)            │  + citations    │ Governor (independent system) │
   └────────────────────┘                  │ spec-basis · evidence-        │
          │                commit ◀────────┤ incomplete · reservation-     │
          │                                │ ineligible (FLAGSHIP) · fee-  │
    record + ledger        escalate ◀──────┤ mismatch · TIN-unverified ·   │
          │            (ALWAYS for         │ BITC/BOSSC unacknowledged ·   │
          │             draft/submit)      │ already-drafted/submitted     │
          ▼                                └──────────────────────────────┘
      human approval
```

No automated proposal can submit a portal registration or filing the
governor refuses, suppress a compliance record, or claim a legal/tax
conclusion the governor has not cleared. `:filing/submit` is never in
any phase's `:auto` set -- it always requires human sign-off (mirrors
`cloud-itonami-M6910`'s `filing-submit-never-auto-at-any-phase`
invariant).

## What this is NOT

- **Not the government of Botswana, and not BITC/CIPA/BURS/PPRA
  themselves.** See
  [`docs/business-model.md`](docs/business-model.md) for the boundary with
  `com-etzhayyim-ooyake` (read-only civic mirror), `matsurigoto` (sovereign
  statecraft), `com-etzhayyim-toritsugi` (individual citizen concierge),
  `legal-entity.etzhayyim.com` (read-only data aggregation), and
  `cloud-itonami-M6910` (company incorporation — a different regulatory
  phase this blueprint assumes is already complete).
- **Not legal or tax advice.** Every regulatory claim must cite the
  official source and route final filings to Botswanan-licensed counsel
  or a registered agent where the law requires licensed representation.
- **Not a claim that BITC registers companies.** BITC/BOSSC
  facilitates; CIPA is, and remains, the registrar of record for
  every engagement, foreign or domestic (see PPRA disambiguation /
  Official surface above).

## Run

```bash
clojure -M:dev:run     # walk a clean intake -> assess -> draft -> submit lifecycle, plus HARD-hold scenarios
clojure -M:dev:test    # governor contract · phase invariants · store parity · registry conformance · facts coverage
clojure -M:lint        # clj-kondo (errors fail; CI mirrors this)
```

## Layout

| File | Role |
|---|---|
| `src/marketentry/store.cljc` | **Store** protocol -- `MemStore` ‖ `DatomicStore` (`langchain.db` + `kotoba-lang/langchain-store`, no hand-rolled EDN-blob codec) + append-only audit ledger + draft AND submit history (dual history) |
| `src/marketentry/registry.cljc` | Filing-draft/filing-submit record construction, `engagement-fee-matches-claim?` ground-truth recompute, `reservation-ineligible?` flagship ORDERED TIER-CLASSIFICATION check |
| `src/marketentry/facts.cljc` | Per-jurisdiction market-entry regulatory catalog with an official spec-basis citation per entry, the PPRA/Kenya/Pakistan disambiguation guard, honest coverage reporting |
| `src/marketentry/marketentryllm.cljc` | **MarketEntry-LLM** -- `mock-advisor`; intake/jurisdiction-assessment/draft/submit proposals |
| `src/marketentry/governor.cljc` | **Market-Entry Compliance Governor** -- 6 HARD checks + 2 double-actuation guards + 1 soft (confidence/actuation gate), see Checks above |
| `src/marketentry/phase.cljc` | **Phase 0→3** -- read-only → assisted intake → assisted assess → supervised (draft/submit always human) |
| `src/marketentry/operation.cljc` | **OperationActor** -- langgraph StateGraph |
| `src/marketentry/sim.cljc` | demo driver |
| `test/marketentry/*_test.clj` | governor contract (incl. PPRA-disambiguation + BITC-gate tests) · phase invariants · store parity · registry conformance · facts coverage |

## No robotics premise — digital/data service exemption

Market-entry and procurement-compliance navigation is a pure data/software
service with no physical-domain work (portal registration, document
checklists, regulatory-change monitoring) — the same exemption class as
`cloud-itonami-6310` (HR SaaS replacement) and `cloud-itonami-gtin-*`.
`blueprint.edn` sets `:itonami.blueprint/robotics false` and
`:required-technologies` lists only real capabilities (`:identity`,
`:forms`, `:dmn`, `:bpmn`, `:audit-ledger`), no `:robotics`.

## Capability layer

Resolves via [`kotoba-lang/iso3166`](https://github.com/kotoba-lang/iso3166)
(ISO 3166 `BWA`). Required capabilities:

- :identity
- :forms
- :dmn
- :bpmn
- :audit-ledger

See [`docs/business-model.md`](docs/business-model.md) and
[`docs/operator-guide.md`](docs/operator-guide.md).

## Jurisdiction coverage (honest)

`marketentry.facts/coverage` reports how many requested jurisdictions
actually have an official spec-basis in `marketentry.facts/catalog` --
currently BWA, USA and DEU are seeded. This is a starting catalog to
prove the governor contract end-to-end, not a claim of global
coverage. Adding a jurisdiction is additive: one map entry citing a
real official source -- never fabricate a jurisdiction's requirements
to make coverage look bigger, and never add a "PPRA" entry for another
country without re-verifying which country's PPRA it actually is.

## Maturity

`:implemented` -- see `blueprint.edn`'s
`:itonami.blueprint/implemented-slice` for the full promotion note.

## License

AGPL-3.0-or-later.

## Culture catalog

This repo carries a **country-level regional-culture catalog**
(ADR-2607171400 addendum 2, `cloud-itonami-municipality-culture-catalog`
Wave 1, in `com-junkawasaki/root`) — national dishes, protected products,
beverages, crafts, festivals and heritage sites for Botswana:

- `src/culture/facts.cljc` — the catalog, source of truth (keyed by
  uppercase ISO3, mirroring `statute.facts`).
- `schema/culture.edn` — DataScript schema.
- `data/culture-tx.edn` — derived DataScript tx-data (regenerated from
  the catalog, never hand-edited).

City-level counterparts live in the `cloud-itonami-municipality-*` repos.
Same provenance discipline as the compliance catalogs: every entry cites a
source URL that was actually fetched and read on `:culture/retrieved-at`;
summaries state only what the cited source confirms. An item not in
`culture.facts/catalog` has no spec-basis — never fabricate one.

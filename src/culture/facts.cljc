(ns culture.facts
  "Country-level regional-culture catalog for Botswana (BWA) -- national
  dishes, protected products, beverages, crafts, festivals and heritage
  sites, per ADR-2607171400 addendum 2 (cloud-itonami-municipality-
  culture-catalog Wave 1, in com-junkawasaki/root). Sibling namespace to
  `marketentry.facts` / `statute.facts` (ADR-2607141700); city-level
  counterparts live in the cloud-itonami-municipality-* repos.

  Catalog is keyed by UPPERCASE ISO3 (mirrors `statute.facts`); entries
  carry no :culture/municipality (that attribute is city-level only).

  Every entry cites a source URL that was actually fetched and read on
  :culture/retrieved-at -- never fabricated. Summaries state only what the
  cited source confirms. An item not in this table has NO spec-basis, full
  stop; extend `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of culture entries."
  {"BWA"
   [{:culture/id "bwa.dish.seswaa"
     :culture/name "Seswaa"
     :culture/country "BWA"
     :culture/kind :dish
     :culture/summary "Traditional stewed meat dish of Botswana made from beef or goat, normally prepared for ceremonies such as funerals, weddings and independence celebrations."
     :culture/url "https://en.wikipedia.org/wiki/Seswaa"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "bwa.dish.bogobe-jwa-lerotse"
     :culture/name "Bogobe jwa lerotse"
     :culture/country "BWA"
     :culture/kind :dish
     :culture/summary "Traditional porridge of sorghum meal and lerotse melon, described as a national dish of Botswana; the lerotse melon is indigenous to the country."
     :culture/url "https://en.wikipedia.org/wiki/Bogobe_jwa_lerotse"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "bwa.dish.dikgobe"
     :culture/name "Dikgobe"
     :culture/country "BWA"
     :culture/kind :dish
     :culture/summary "Dish of samp and beans cooked together, with its place of origin given as Botswana, South Africa and Lesotho; a customary starch at Setswana celebrations such as marriages and funerals."
     :culture/url "https://en.wikipedia.org/wiki/Dikgobe"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "bwa.dish.morogo"
     :culture/name "Morogo"
     :culture/country "BWA"
     :culture/kind :dish
     :culture/summary "Dark green leafy vegetables (African spinach) found throughout Southern Africa, including Botswana, forming an important part of the staple diet in rural communities."
     :culture/url "https://en.wikipedia.org/wiki/Morogo"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "bwa.product.mopane-worm"
     :culture/name "Mopane worm"
     :culture/country "BWA"
     :culture/kind :product
     :culture/summary "Edible caterpillar of the emperor moth Gonimbrasia belina, an established food and important protein source in southern Africa; Botswana is a principal producer, earning roughly $8 million annually."
     :culture/url "https://en.wikipedia.org/wiki/Mopane_worm"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "bwa.beverage.chibuku-shake-shake"
     :culture/name "Chibuku Shake Shake"
     :culture/country "BWA"
     :culture/kind :beverage
     :culture/summary "Commercial sorghum beer based on traditional African umqombothi beers; in Botswana it is brewed by Botswana Breweries, a subsidiary of Kgalagadi Breweries Limited."
     :culture/url "https://en.wikipedia.org/wiki/Chibuku_Shake_Shake"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "bwa.festival.dithubaruba"
     :culture/name "Dithubaruba Cultural Festival"
     :culture/country "BWA"
     :culture/kind :festival
     :culture/summary "Annual national cultural festival held near Molepolole since 2007, celebrating the heritage of the Bakwena people and marked in the Botswana Calendar of Events."
     :culture/url "https://en.wikipedia.org/wiki/Dithubaruba_Cultural_Festival"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "bwa.heritage.tsodilo"
     :culture/name "Tsodilo"
     :culture/country "BWA"
     :culture/kind :heritage
     :culture/summary "Hills in Ngamiland District, Botswana, with over 4,500 rock paintings; inscribed as a UNESCO World Heritage Site in 2001 for their religious and spiritual significance and record of human settlement."
     :culture/url "https://en.wikipedia.org/wiki/Tsodilo"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "bwa.heritage.okavango-delta"
     :culture/name "Okavango Delta"
     :culture/country "BWA"
     :culture/kind :heritage
     :culture/summary "Vast inland delta in Botswana formed by the Okavango River, inscribed on 22 June 2014 as the 1000th UNESCO World Heritage Site."
     :culture/url "https://en.wikipedia.org/wiki/Okavango_Delta"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}]})

(defn spec-basis [iso3] (get catalog iso3))

(defn coverage
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-bwa culture catalog "
                 "(ADR-2607171400 addendum 2, Wave 1): " (count (get catalog "BWA"))
                 " BWA entries, each with a fetched-and-read citation. "
                 "Extend `culture.facts/catalog`, never fabricate an id/url.")})))

(defn by-kind [iso3 kind]
  (filterv #(= (:culture/kind %) kind) (spec-basis iso3)))

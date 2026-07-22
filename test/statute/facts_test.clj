(ns statute.facts-test
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is]]
            [statute.facts :as facts]))

(deftest bwa-has-spec-basis
  (let [sb (facts/spec-basis "BWA")]
    (is (= 4 (count sb)))
    (is (every? #(str/starts-with? (:statute/url %) "https://") sb))
    (is (every? :statute/law-number sb))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ"))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["BWA" "JPN" "ATL"])]
    (is (= 3 (:requested c)))
    (is (= 1 (:covered c)))
    (is (= ["ATL" "JPN"] (:missing-jurisdictions c)))))

(deftest by-topic-filters
  (is (= ["bwa.burs-act-cap-53-03" "bwa.income-tax-act-cap-52-01"]
         (mapv :statute/id (facts/by-topic "BWA" :tax))))
  (is (= ["bwa.public-procurement-act-2021"]
         (mapv :statute/id (facts/by-topic "BWA" :public-procurement))))
  (is (= ["bwa.burs-act-cap-53-03"]
         (mapv :statute/id (facts/by-topic "BWA" :public-finance))))
  (is (empty? (facts/by-topic "BWA" :environment)))
  (is (empty? (facts/by-topic "ATL" :tax))))

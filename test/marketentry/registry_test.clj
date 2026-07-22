(ns marketentry.registry-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.registry :as registry]))

(deftest engagement-fee-recompute
  (let [e {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12 :claimed-fee 860000.0}]
    (is (== 860000.0 (registry/compute-engagement-fee e)))
    (is (true? (registry/engagement-fee-matches-claim? e))))
  (let [bad {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12 :claimed-fee 999000.0}]
    (is (false? (registry/engagement-fee-matches-claim? bad)))))

(deftest register-draft-and-submit
  (let [d (registry/register-draft "eng-1" "BWA" 0)
        s (registry/register-submit "eng-1" "BWA" 0)]
    (is (= "BWA-DFT-000000" (get d "draft_number")))
    (is (= "BWA-SUB-000000" (get s "submit_number")))
    (is (nil? (get-in d ["certificate" "proof"])))
    (is (= "draft-unsigned" (get-in s ["certificate" "status"])))))

(deftest register-requires-ids
  (is (thrown? Exception (registry/register-draft "" "BWA" 0)))
  (is (thrown? Exception (registry/register-submit "eng-1" "" 0))))

(deftest citizen-preference-tier-recompute
  (testing "Public Procurement Act, 2021 s.78(1) descending order -- ordered tier classification, no arithmetic beyond a single > 50 comparison"
    (is (= :citizen-joint-venture
           (registry/citizen-preference-tier
            {:citizen-ownership-pct 100 :joint-venture? true})))
    (is (= :sole-citizen-contractor
           (registry/citizen-preference-tier
            {:citizen-ownership-pct 100 :joint-venture? false})))
    (is (= :majority-citizen-joint-venture
           (registry/citizen-preference-tier
            {:citizen-ownership-pct 60 :joint-venture? true})))
    (is (= :citizen-subcontractor-association
           (registry/citizen-preference-tier
            {:citizen-ownership-pct 0 :joint-venture? false :citizen-subcontractor-association? true})))
    (is (= :ineligible
           (registry/citizen-preference-tier
            {:citizen-ownership-pct 0 :joint-venture? false :citizen-subcontractor-association? false})))
    (testing "a JV with only 50% (not STRICTLY majority) citizen shares does not qualify for the majority-JV tier"
      (is (= :ineligible
             (registry/citizen-preference-tier
              {:citizen-ownership-pct 50 :joint-venture? true}))))
    (testing "missing citizen-ownership-pct never throws and classifies as ineligible"
      (is (= :ineligible (registry/citizen-preference-tier {:joint-venture? true}))))))

(deftest reservation-ineligible-recompute
  (testing "0% citizen ownership, no JV, no association, on a reserved category -> ineligible"
    (is (true? (registry/reservation-ineligible?
                {:reserved-category? true :reservation-exempt? false
                 :citizen-ownership-pct 0 :joint-venture? false
                 :citizen-subcontractor-association? false}))))
  (testing "100% citizen ownership on a reserved category -> eligible (not ineligible)"
    (is (false? (registry/reservation-ineligible?
                 {:reserved-category? true :reservation-exempt? false
                  :citizen-ownership-pct 100 :joint-venture? false}))))
  (testing "category/exemption-scope-gated: a no-op (false) unless :reserved-category? is true"
    (is (false? (registry/reservation-ineligible?
                 {:reserved-category? false :reservation-exempt? false
                  :citizen-ownership-pct 0 :joint-venture? false
                  :citizen-subcontractor-association? false}))
        "not a reserved category -- the Act's own s.76(1) reservation never applies"))
  (testing "an on-file exemption (ss.140-143 external obligation/derogation) is also a no-op"
    (is (false? (registry/reservation-ineligible?
                 {:reserved-category? true :reservation-exempt? true
                  :citizen-ownership-pct 0 :joint-venture? false
                  :citizen-subcontractor-association? false})))))

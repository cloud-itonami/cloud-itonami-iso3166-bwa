(ns marketentry.facts-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.facts :as facts]))

(deftest bwa-has-spec-basis
  (let [sb (facts/spec-basis "BWA")]
    (is (some? sb))
    (is (string? (:provenance sb)))
    (is (seq (:required-evidence sb)))
    (is (some? (facts/corporate-number-spec-basis "BWA")))
    (is (some? (facts/business-registration-spec-basis "BWA")))
    (is (some? (facts/citizen-reservation-spec-basis "BWA")))))

(deftest bwa-rep-spec-basis-is-honest-nil
  (testing "BWA's rep-spec-basis is nil -- this iteration found a real external-company local-agent duty (Companies Act s.345(e)) but it is scoped narrower than a general market-entry representative regime"
    (is (nil? (facts/rep-spec-basis "BWA")))))

(deftest bwa-business-registration-is-a-different-body-from-tax-and-procurement
  (testing "business/company registration (CIPA) and tax registration (BURS) are administered by different authorities -- see namespace docstring"
    (let [reg (facts/business-registration-spec-basis "BWA")
          tax (facts/corporate-number-spec-basis "BWA")]
      (is (some? reg))
      (is (some? tax))
      (is (not= (:business-registration-owner-authority reg)
                (:corporate-number-owner-authority tax))))))

(deftest bwa-citizen-reservation-is-the-flagship-spec-basis
  (testing "the Public Procurement Act, 2021 s.78(1) descending preference scale is a real, government-published, four-tier scale -- not fabricated"
    (let [cr (facts/citizen-reservation-spec-basis "BWA")]
      (is (some? cr))
      (is (= 4 (count (:citizen-reservation-preference-scale cr))))
      (is (= :citizen-joint-venture (first (:citizen-reservation-preference-scale cr))))
      (is (= :sole-citizen-contractor (second (:citizen-reservation-preference-scale cr)))))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ")))
  (is (nil? (facts/business-registration-spec-basis "ATL")))
  (is (nil? (facts/citizen-reservation-spec-basis "ATL"))))

(deftest required-evidence-satisfied
  (let [sb (facts/spec-basis "BWA")
        all (:required-evidence sb)]
    (is (true? (facts/required-evidence-satisfied? "BWA" all)))
    (is (not (facts/required-evidence-satisfied? "BWA" (take 1 all))))
    (is (nil? (facts/required-evidence-satisfied? "ATL" all)))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["BWA" "USA" "ATL"])]
    (is (= 3 (:requested c)))
    (is (= 2 (:covered c)))
    (is (= ["ATL"] (:missing-jurisdictions c)))))

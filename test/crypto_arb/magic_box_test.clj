(ns crypto-arb.magic-box-test
  (:require [clojure.test :refer :all])
  (:require [crypto-arb.magic-box :refer :all]
            [manifold.stream :as s]))

(deftest test-pdr-pp
  (testing "pretty print a pdr object"
    (let [pdr {:pdr (/ 1.05 1.00)
               :low {:price 1.00
                     :ex :bitstamp
                     :pair :btc-eur}
               :high {:price 1.05
                      :ex :gdax
                      :pair :btc-eur}}
          expected-result "btc-eur 1.05 1.0@bitstamp 1.05@gdax"]
      (is (= (pp-pdr pdr :btc-eur) expected-result)))))

(deftest test-calculate-pdr
  (testing "calculation of greatest price difference ratio given a ticker event"
    (let [ticker-event {:low {:price 9.00
                              :ex :bitstamp
                              :pair :btc-eur}
                        :high {:price 11.00
                               :ex :gdax
                               :pair :btc-eur}}
          expected-result (merge ticker-event
                                 {:pdr  (/ 11.00 9.00)})]
      (is (= (calculate-pdr ticker-event) expected-result)))))

(deftest test-update-state
  (testing "update state should add pair to state if it does not already exist"
    (let [tick {:price 12980.5, :ex :gdax, :pair :btc-eur}
          expected-state {:btc-eur {:low tick :high tick}}]
      (is (= (update-state {} tick) expected-state))))
  (testing "update state should not update existing pair if it is not lower or higher than current"
    (let [existing-low {:price 1, :ex :gdax, :pair :btc-eur}
          existing-high {:price 3, :ex :gdax, :pair :btc-eur}
          tick {:price 2, :ex :bitstamp, :pair :btc-eur}
          existing-state {:btc-eur {:low existing-low :high existing-high}}]
      (is (= (update-state existing-state tick) existing-state))))
  (testing "update state should update existing pair if it is higher than the current high"
    (let [existing-low {:price 1, :ex :gdax, :pair :btc-eur}
          existing-high {:price 2, :ex :gdax, :pair :btc-eur}
          tick {:price 3, :ex :bitstamp, :pair :btc-eur}
          existing-state {:btc-eur {:low existing-low :high existing-high}}
          expected-state {:btc-eur {:low existing-low :high tick :pdr 3}}]
      (is (= (update-state existing-state tick) expected-state))))
  (testing "update state should update existing pair if it is lower than the current low"
    (let [existing-low {:price 2, :ex :gdax, :pair :btc-eur}
          existing-high {:price 3, :ex :gdax, :pair :btc-eur}
          tick {:price 1, :ex :bitstamp, :pair :btc-eur}
          existing-state {:btc-eur {:low existing-low :high existing-high}}
          expected-state {:btc-eur {:low tick :high existing-high :pdr 3}}]
      (is (= (update-state existing-state tick) expected-state)))))

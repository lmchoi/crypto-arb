(ns crypto-arb.magic-box-test
  (:require [clojure.test :refer :all])
  (:require [crypto-arb.magic-box :refer :all]))

(deftest test-pdr-pp
  (testing "pretty print a pdr object"
    (let [pdr {:pdr        (/ 1.05 1.00)
               :buy-price  1.00
               :buy-at     :bitstamp
               :sell-price 1.05
               :sell-at    :gdax}
          expected-result "1.05 1.0@bitstamp 1.05@gdax"]
      (is (= (pdr-pp pdr) expected-result)))))

(deftest test-calculate-pdr
  (testing "calculation of greatest price difference ratio given a ticker event"
    (let [ticker-event '({:price 10.00, :ex :some-other} {:price 9.00, :ex :bitstamp} {:price 11.00, :ex :gdax})
          expected-result {:pdr (/ 11.00 9.00)
                           :buy-price 9.00
                           :buy-at :bitstamp
                           :sell-price 11.00
                           :sell-at :gdax}]
      (is (= (calculate-pdr ticker-event) expected-result)))))

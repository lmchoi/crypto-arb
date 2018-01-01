(ns crypto-arb.exchange-client-test
  (:require [clojure.test :refer :all]
            [crypto-arb.exchanges.common.client :as client]))

(deftest return-current-bid-bitstamp
  (testing "return current bid"
    (let [actual-response (client/get-ticker-bitstamp)]
      (is (number? actual-response)))))

(deftest return-current-bid-bittrex
  (testing "return current bid from bittrex"
    (let [actual-response (client/get-ticker-bittrex)]
      (is (number? actual-response)))))

(deftest return-current-bid-kraken
  (testing "return current bid from kraken"
    (let [actual-response (client/get-ticker-kraken)]
      (is (number? actual-response)))))

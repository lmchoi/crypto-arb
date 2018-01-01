(ns crypto-arb.exchanges.common.client
  (:require [org.httpkit.client :as http]
            [cheshire.core :refer :all]))

;max
;600 requests per 10 minutes
(defn get-ticker-bitstamp []
  (let [response @(http/get "https://www.bitstamp.net/api/v2/ticker/xrpeur/")
        body (parse-string (:body response) true)]
    (parse-string (:bid body))))

; https://www.kraken.com/en-us/help/api#general-usage
; note that this API can retrieve more than one pair per call
(defn get-ticker-kraken []
  (let [response @(http/get "https://api.kraken.com/0/public/Ticker?pair=XXRPZEUR")
        body (parse-string (:body response) true)]
    (parse-string (get-in body [:result :XXRPZEUR :b 0]))))

; NOTE this is in USD!
(defn get-ticker-bittrex []
  (let [response @(http/get "https://bittrex.com/api/v1.1/public/getticker?market=USDT-BTC")
        body (parse-string (:body response) true)]
    (get-in body [:result :Bid])))



(ns crypto-arb.exchanges.binance
  (:require [aleph.http :as http]
            [manifold.stream :as s]))

(def exchange-id :binance)
(def exchange-url "wss://stream.binance.com:9443")
(def exchange-fees {:per-txn 0.001})

(def products-definition {"ETHBTC" :eth-btc})

(defn- ticker-response? [{:keys [data]}]
  (= (:e data) "aggTrade"))

(defn- parse-price [data]
  (read-string (:p data)))

(defn product-lookup [data]
  (get products-definition (:s data) :unknown))

(defn- parse-ticker-msg [{:keys [data] :as msg}]
  {:price (parse-price data)
   :ex    exchange-id
   :pair  (product-lookup data)})

(def exchange
  {:id                  exchange-id
   :url                 (str exchange-url "/stream?streams=" "ethbtc" "@aggTrade")
   :ticker-request      nil
   :ticker-response?-fn ticker-response?
   :parse-ticker-msg-fn parse-ticker-msg
   :fees                exchange-fees})

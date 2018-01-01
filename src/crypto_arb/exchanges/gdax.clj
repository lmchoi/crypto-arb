(ns crypto-arb.exchanges.gdax
  (:require [aleph.http :as http]
            [manifold.stream :as s]))

(def exchange-id :gdax)
(def exchange-url "wss://ws-feed.gdax.com")

(def default-ticker-request
  {:type "subscribe",
   :product_ids ["BTC-EUR"],
   :channels [{:name "ticker"}]})

(defn- create-ticker-request []
  (cheshire.core/generate-string default-ticker-request))

(defn- ticker-response? [{:keys [type]}]
  (= type "ticker"))

(defn- parse-price [msg]
  (read-string (:price msg)))

(defn- parse-ticker-msg [msg]
  {:price (parse-price msg)
   :ex    exchange-id})

(def exchange
  {:id                exchange-id
   :url               exchange-url
   :ticker-request-fn create-ticker-request
   :ticker-response?-fn ticker-response?
   :parse-ticker-msg-fn parse-ticker-msg})

; TODO
; 1. check for errors
; 2. pass in product ids

;
;{:type "subscribe",
; :product_ids ["ETH-USD" "ETH-EUR"], // applies to all channels
; :channels ["level2"
;            "heartbeat"
;            {:name "ticker", :product_ids ["ETH-BTC" "ETH-USD"]}] // products for this channel only
; }

; example response
;"gdax" {"product_id" "BTC-EUR", "trade_id" 8799761, "side" "sell", "open_24h" "10844.00000000", "volume_24h" "3431.58812769",
;        "sequence" 3161010782, "best_ask" "12310.56", "time" "2017-12-31T17:00:41.111000Z", "low_24h" "12310.55000000",
;        "type" "ticker", "high_24h" "12475.00000000", "price" "12310.55000000", "best_bid" "12310.55",
;        "last_size" "0.10934637", "volume_30d" "177809.56174837"}


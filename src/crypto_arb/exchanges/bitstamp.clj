(ns crypto-arb.exchanges.bitstamp
  (:require [aleph.http :as http]
            [manifold.stream :as s]))

(def exchange-id :bitstamp)
(def exchange-url "wss://ws.pusherapp.com:443/app/de504dc5763aeef9ff52?protocol=5")

(def default-ticker-request
  {:event "pusher:subscribe"
   :data {:channel "live_trades_btceur"
          :event "trade"}})

(defn- create-ticker-request []
  (cheshire.core/generate-string default-ticker-request))

(defn- ticker-response? [{:keys [event]}]
  (= event "trade"))

(defn- parse-price [{:keys [data]}]
  (:price (cheshire.core/parse-string data true)))

(defn- parse-ticker-msg [msg]
  {:price (parse-price msg)
   :ex    exchange-id})

(def exchange
  {:id                exchange-id
   :url               exchange-url
   :ticker-request-fn create-ticker-request
   :ticker-response?-fn ticker-response?
   :parse-ticker-msg-fn parse-ticker-msg})

; NOTE: this is Pusher format: https://pusher.com/docs/pusher_protocol

;"bitstamp" {"event" "trade", "data" "{\"amount\": 0.01290669,
;\"buy_order_id\": 693486286, \"sell_order_id\": 693486047,
;\"amount_str\": \"0.01290669\", \"price_str\": \"11788.11\",
;\"timestamp\": \"1514739640\", \"price\": 11788.110000000001,
;\"type\": 0, \"id\": 40358824}", "channel" "live_trades_btceur"}

(ns crypto-arb.exchanges.bitstamp
  (:require [aleph.http :as http]
            [manifold.stream :as s]))

(def exchange-id :bitstamp)
(def exchange-url "wss://ws.pusherapp.com:443/app/de504dc5763aeef9ff52?protocol=5")
(def exchange-fees {:per-txn 0.0025})

(def products-definition {"live_trades_btceur" :btc-eur
                          "live_trades_etheur" :eth-eur
                          "live_trades_xrpeur" :xrp-eur
                          "live_trades_ethbtc" :eth-btc
                          "live_trades_xrpbtc" :xrp-btc})

(def default-ticker-request
  {:event "pusher:subscribe"
   :data {:channel "live_trades_btceur"
          :event "trade"}})

(defn- create-ticker-request [channel]
  (-> default-ticker-request
      (assoc-in [:data :channel] channel)
      (cheshire.core/generate-string)))

(defn- ticker-response? [{:keys [event]}]
  (= event "trade"))

(defn- parse-price [data]
  (:price data))

(defn product-lookup [msg]
  (get products-definition (:channel msg) :unknown))

(defn- parse-ticker-msg [{:keys [data] :as msg}]
  (let [parsed-data (cheshire.core/parse-string data true)]
    {:price (parse-price parsed-data)
     :ex    exchange-id
     :pair  (product-lookup msg)}))

(def exchange
  {:id                  exchange-id
   :url                 exchange-url
   :ticker-request      [(create-ticker-request "live_trades_btceur")
                         (create-ticker-request "live_trades_etheur")
                         (create-ticker-request "live_trades_ethbtc")]
   :ticker-response?-fn ticker-response?
   :parse-ticker-msg-fn parse-ticker-msg
   :fees                exchange-fees})

; NOTE: this is Pusher format: https://pusher.com/docs/pusher_protocol

;"bitstamp" {"event" "trade", "data" "{\"amount\": 0.01290669,
;\"buy_order_id\": 693486286, \"sell_order_id\": 693486047,
;\"amount_str\": \"0.01290669\", \"price_str\": \"11788.11\",
;\"timestamp\": \"1514739640\", \"price\": 11788.110000000001,
;\"type\": 0, \"id\": 40358824}", "channel" "live_trades_btceur"}

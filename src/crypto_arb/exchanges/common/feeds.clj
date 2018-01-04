(ns crypto-arb.exchanges.common.feeds
  (:require [crypto-arb.exchanges.gdax :as gdax]
            [crypto-arb.exchanges.bitstamp :as bitstamp]
            [crypto-arb.exchanges.binance :as binance]
            [manifold.stream :as s]
            [aleph.http :as http]))

(defn- parse-msg [msg {:keys [ticker-response?-fn parse-ticker-msg-fn id]}]
  (let [parsed-msg (cheshire.core/parse-string msg true)]
    (if (ticker-response?-fn parsed-msg)
      (let [parsed-ticker-msg (parse-ticker-msg-fn parsed-msg)]
        (prn parsed-ticker-msg)
        parsed-ticker-msg)
      (prn (name id) parsed-msg))))

(defn connect [{:keys [url ticker-request] :as exchange}]
  (let [conn @(http/websocket-client url)]
    ; sends request to exchange
    (s/put-all! conn ticker-request)
    ; connect response stream to ticker stream
    (->> conn
         (s/map #(parse-msg % exchange))
         (s/filter (complement nil?)))))

(defn ticker-streams []
  {:gdax-stream     (connect gdax/exchange)
   :bitstamp-stream (connect bitstamp/exchange)
   :binance-stream  (connect binance/exchange)})

(defn one-big-ticker-stream []
  (let [ticker-stream (s/stream)]
    (s/connect (connect gdax/exchange) ticker-stream)
    (s/connect (connect bitstamp/exchange) ticker-stream)
    (s/connect (connect binance/exchange) ticker-stream)
    ticker-stream))
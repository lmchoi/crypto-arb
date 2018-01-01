(ns crypto-arb.exchanges.common.feeds
  (:require [crypto-arb.exchanges.gdax :as gdax]
            [crypto-arb.exchanges.bitstamp :as bitstamp]
            [manifold.stream :as s]
            [aleph.http :as http]))

(defn- parse-msg [msg {:keys [ticker-response?-fn parse-ticker-msg-fn id]}]
  (let [parsed-msg (cheshire.core/parse-string msg true)]
    (if (ticker-response?-fn parsed-msg)
      (parse-ticker-msg-fn parsed-msg)
      (prn (name id) parsed-msg))))

(defn connect [{:keys [url ticker-request-fn] :as exchange} ticker-stream]
  (let [conn @(http/websocket-client url)]
    ; sends request to exchange
    (s/put-all! conn
                [(ticker-request-fn)])
    ; connect response stream to ticker stream
    (s/connect (->> conn
                    (s/map #(parse-msg % exchange))
                    (s/filter (complement nil?)))
               ticker-stream)))

(defn ticker-feeds []
  (let [ticker-stream (s/stream)]
    (connect gdax/exchange ticker-stream)
    (connect bitstamp/exchange ticker-stream)
    (s/consume prn ticker-stream)))

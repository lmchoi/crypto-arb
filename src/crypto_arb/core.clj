(ns crypto-arb.core
  (:require [clj-http.client :as client])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println (client/get "https://www.bitstamp.net/api/v2/ticker/xrpeur/")))

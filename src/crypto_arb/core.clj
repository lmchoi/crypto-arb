(ns crypto-arb.core
  (:require [crypto-arb.exchanges.common.feeds :refer :all])
  (:gen-class))

(defn cleanup []
  (println "SHUTDOWN"))

(defn -main
  [& args]
  (ticker-feeds)

  ; cleanup
  (.addShutdownHook (Runtime/getRuntime)
                    (Thread. #(cleanup)))

  ; TODO a more elegant way to keep the application running?
  (loop []
    (Thread/sleep 1000)
    (recur)))

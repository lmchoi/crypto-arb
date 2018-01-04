(ns crypto-arb.core
  (:require [crypto-arb.exchanges.common.feeds :refer :all]
            [crypto-arb.magic-box :refer :all])
  (:gen-class))

(defn cleanup []
  (println "SHUTDOWN"))

(defn -main
  [& args]
  ; start streaming ticker events from exchange and do magic
  (-> (one-big-ticker-stream)
      (do-magic))

  ; cleanup
  (.addShutdownHook (Runtime/getRuntime)
                    (Thread. #(cleanup)))

  ; TODO a more elegant way to keep the application running?
  (loop []
    (Thread/sleep 1000)
    (recur)))

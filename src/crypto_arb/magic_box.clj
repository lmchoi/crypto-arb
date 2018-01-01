(ns crypto-arb.magic-box
  (:require [manifold.stream :as s]))

(defn- with-buy-info [pdr {:keys [price ex]}]
  (-> pdr
      (assoc :buy-price price)
      (assoc :buy-at ex)))

(defn- with-sell-info [pdr {:keys [price ex]}]
  (-> pdr
      (assoc :sell-price price)
      (assoc :sell-at ex)))

(defn calculate-pdr [ticker-event]
  (let [[cheapest most-expensive] ((juxt first last) (sort-by :price ticker-event))]
    (-> {:pdr (/ (:price most-expensive) (:price cheapest))}
        (with-buy-info cheapest)
        (with-sell-info most-expensive))))

(defn pdr-pp [{:keys [pdr buy-price buy-at sell-price sell-at]}]
  (let [formatted-str (str pdr " " buy-price "@" (name buy-at) " " sell-price "@" (name sell-at))]
    (println formatted-str)
    formatted-str))

(defn do-magic [ticker-stream]
  (s/consume #(pdr-pp (calculate-pdr %))
             ticker-stream))

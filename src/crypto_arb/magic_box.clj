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

(defn pdr-pp [{:keys [pdr buy-price buy-at sell-price sell-at]} pair]
  (let [formatted-str (str (name pair) " " pdr " " buy-price "@" (name buy-at) " " sell-price "@" (name sell-at))]
    (println formatted-str)
    formatted-str))

(defn handle-pair [{:keys [gdax-stream bitstamp-stream]} pair]
  (s/consume #(pdr-pp (calculate-pdr %) pair)
             (s/zip (s/filter #(= (:pair %) pair) gdax-stream)
                    (s/filter #(= (:pair %) pair) bitstamp-stream))))

(defn do-magic [ticker-streams]
  (handle-pair ticker-streams :btc-eur)
  (handle-pair ticker-streams :eth-eur)
  (handle-pair ticker-streams :eth-btc))

(ns crypto-arb.magic-box
  (:require [manifold.stream :as s]))

(defn pp-pdr [{:keys [pdr low high]} pair]
  (let [buy-price (:price low)
        buy-at (:ex low)
        sell-price (:price high)
        sell-at (:ex high)
        formatted-str (str (name pair) " " pdr " "
                           buy-price "@" (name buy-at) " "
                           sell-price "@" (name sell-at))]
    (println formatted-str)
    formatted-str))

(defn calculate-pdr [{:keys [low high] :as pair-state}]
  (let [pdr (/ (:price high) (:price low))
        updated-pair-state (assoc pair-state :pdr pdr)]
    (pp-pdr updated-pair-state (:pair low))
    updated-pair-state))

(defn new-low? [pair-state tick]
  (> (get-in pair-state [:low :price]) (:price tick)))

(defn new-high? [pair-state tick]
  (< (get-in pair-state [:high :price] ) (:price tick)))

(defn update-existing-pair [pair-state tick]
  (cond
    (nil? pair-state)
    {:low tick :high tick}

    (new-low? pair-state tick)
    (-> pair-state
        (assoc :low tick)
        (calculate-pdr))

    (new-high? pair-state tick)
    (-> pair-state
        (assoc :high tick)
        (calculate-pdr))

    :default
    pair-state))

(defn update-state [state {:keys [pair price] :as tick}]
  (update state pair update-existing-pair tick))

(defn do-magic [ticker-stream]
  (s/reduce update-state
            {}
            ticker-stream))

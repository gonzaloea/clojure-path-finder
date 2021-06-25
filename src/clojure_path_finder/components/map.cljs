(ns clojure-path-finder.components.map)

(defn map-component [state]
  [:div.map
   (for [n @state]
     [:div.node {:style {:grid-row-start (:y n) :grid-row-end (:y n) :grid-column-start (:x n) :grid-column-end (:x n) }}  (:id n)])])
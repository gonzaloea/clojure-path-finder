(ns clojure-path-finder.core
  (:require
   [reagent.dom :as d]
   [clojure-path-finder.components.header :as h]
   [clojure-path-finder.components.map :as m]
   [clojure-path-finder.state :as s]
   ))

;; -------------------------
;; State


;; -------------------------
;; Behaviour


;; -------------------------
;; Views

(defn home-page []
  [:div
   [h/header-component]
  ;;  [m/map-component s/state]
   ])

;; Drawing library

(defn setup []
  (js/createCanvas 640 480)
  (js/smooth))

(def graph-view
  {:model (:data s/graph) :size 80 :scale 0.7})


(defn graph-view-get-vertices [graph]
  (keys (:model graph)))

(defn point-in-circle? [x y c_x c_y r]
  (let [dx (- x c_x)
        dy (- y c_y)
        dx2 (* dx dx)
        dy2 (* dy dy)
        d2 (+ dx2 dy2)]
    (< d2 (* r r)))
)


(defn graph-view-draw-vertices [graph]
  (doseq [node (graph-view-get-vertices graph)]
    (let [x (* (:x node) (:size graph))
          y (* (:y node) (:size graph))
          size (* (:size graph) (:scale graph))]
      (if (point-in-circle? js/mouseX js/mouseY x y (/ size 2))
        (js/strokeWeight 3)
        (js/strokeWeight 1))
      (js/ellipse x y size size))
    )
  )

(defn graph-view-draw-edges [graph]
  (doseq [[node adjacents] (:model graph)]
    (doseq [adjacent adjacents]
      (js/line (* (:x node) (:size graph)) 
               (* (:y node) (:size graph)) 
               (* (:x adjacent) (:size graph)) 
               (* (:y adjacent) (:size graph))))))

(defn draw []
  (js/background 51)
  (js/strokeWeight 2)
  (graph-view-draw-edges graph-view)
  (graph-view-draw-vertices graph-view)
)

;; -------------------------
;; Initialize app


(set! (.-setup js/window) setup)
(set! (.-draw js/window) draw)

(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))

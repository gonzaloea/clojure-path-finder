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
   [m/map-component s/state]
   ])

;; Drawing library

(defn setup []
  (js/createCanvas 640 480)
  (js/smooth))

(def graph-view
  @s/newstate)


(defn graph-view-get-vertices [graph]
  (keys graph))

(defn graph-view-draw-vertices [graph]
  (doseq [node (graph-view-get-vertices graph)] 
    (js/ellipse (* (:x node) 80) (* (:y node) 80) 60 60)
  ))

(defn graph-view-draw-edges [graph]
  (doseq [[node adjacents] graph]
    (doseq [adjacent adjacents]
      (js/line (* (:x node) 80) (* (:y node) 80) (* (:x adjacent) 80) (* (:y adjacent) 80)))))

(defn draw []
  (js/background 51)
  (js/strokeWeight 4)
  (graph-view-draw-edges graph-view)
  (js/strokeWeight 1)
  (graph-view-draw-vertices graph-view)
)

;; -------------------------
;; Initialize app


(set! (.-setup js/window) setup)
(set! (.-draw js/window) draw)

(defn mount-root []
  (println "testtest")
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))

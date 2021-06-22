(ns clojure-path-finder.core
    (:require
      [reagent.dom :as d]
      [clojure-path-finder.components.header :as h ]
      [clojure-path-finder.components.map :as m]
      [clojure-path-finder.state :as s]
      [loom.graph :as l]
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
   [m/map-component]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (println "testtest")
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
 
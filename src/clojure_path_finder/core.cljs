(ns clojure-path-finder.core
    (:require
      [reagent.core :as r]
      [reagent.dom :as d]
      [clojure-path-finder.components.header :as h ]
      [clojure-path-finder.components.map :as m]))

;; -------------------------
;; Views

(defn home-page []
  [:div
   [h/header-component]
   [:div [:h2 "Welcome to Reagent"]]
   [m/map-component]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
 
(ns clojure-path-finder.components.header)

(defn header-component []
  [:div.header {:on-click (fn [] (js/console.log "Hello!!"))}
   [:h1.title "Clojure Path Finder"]
   ])
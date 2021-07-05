(ns clojure-path-finder.components.header)

(defn header-component [start-action]
  [:div.header 
   [:h1.title "Clojure Path Finder"]
   [:button.start {:on-click start-action } 
    "► Iniciar"]
   [:div.command-explanation
    [:div.command
     "LClick"]
    [:div.explanation
     "Alternar visibilidad"]]
   [:div.command-explanation
    [:div.command
     "Shift + LClick"]
    [:div.explanation
     "Seleccionar Origen"]]
   [:div.command-explanation
    [:div.command
     "Ctrl + LClick"]
    [:div.explanation
     "Seleccionar Destino"]]])
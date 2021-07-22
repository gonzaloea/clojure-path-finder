(ns clojure-path-finder.components.header)

(defn header-component [start-action reset-action state algorithms]
  [:div.header 
   [:h1.title "Clojure Path Finder"]
   [:div.algorithm-selection-container
    [:div.algorithm-name
     "Algoritmo"]
   
   [:select.algorithm-selection 
    {:on-change 
      (fn [event] 
          [(let [selected-algorithm (.-value (.-target event))]
           (swap! state assoc :algorithm (get algorithms selected-algorithm)))])}
    
    (for [algorithm (keys algorithms)]
      [:option {:value algorithm} algorithm])]
    ]
   
   [:button.start {:on-click start-action } 
    "► Iniciar"]
   
   [:button.reset {:on-click reset-action }
       "↺ Reniciar"]
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

(ns clojure-path-finder.components.header)

(defn header-component [start-action reset-action read-action download-map-action state algorithms]
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
      [:option {:key algorithm :value algorithm} algorithm])]
    ]
   
   [:button.start.button {:on-click start-action } 
    "► Iniciar"]
   
   [:button.reset.button {:on-click reset-action }
       "↺ Reniciar"]
   
   [:label#upload-map-lbl.button.start 
    {:for "upload-map" 
     :role "img" 
     :aria-label "Subir un mapa"} "⭱"]
   
   [:input#upload-map 
    {:type "file" 
     :accept ".json"
     :on-change read-action } ]
   
   [:button#export-map.start.button
    {:role "img"
     :aria-label "Exportar mapa"
     :on-click download-map-action} "⤓"]
   
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

(ns clojure-path-finder.core
  (:require
   [reagent.dom :as d]
   [clojure-path-finder.components.header :as h]
   [clojure-path-finder.components.map :as m]
   [clojure-path-finder.state :as s]))

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
  (js/createCanvas js/windowWidth js/windowHeight)
  (js/smooth))

(def graph-view
  (atom {:model s/graph  :size 30 :scale 0.7}))


(defn graph-view-get-vertices []
  (keys (:data (:model @graph-view))))

(defn mouse-in-rect? [x y c_x c_y r]
  (let [dx (- x c_x)
        dy (- y c_y)
        dx2 (* dx dx)
        dy2 (* dy dy)
        d2 (+ dx2 dy2)]
    (< d2 (* r r))))


(defn graph-view-draw-vertices []
  (doseq [node (graph-view-get-vertices)]
    (let [x (* (:x node) (:size @graph-view))
          y (* (:y node) (:size @graph-view))
          size (* (:size @graph-view) (:scale @graph-view))]
      ;;Si el puntero esta en el casillero, muestro el borde grueso, sino normal.
      (if (mouse-in-rect? js/mouseX js/mouseY x y (/ size 2))
        (js/strokeWeight 3)
        (js/strokeWeight 1))
      ;;Si el puntero esta en el casillero y el mouseClick esta activo, pinto el fondo.
      (if  (not (:visible node))
        (js/fill "#000000")
        (js/fill "#ffffff"))
      ;;Dibujo el rectangulo del casillero
      (js/rect (- x (/ size 2)) (- y (/ size 2)) size size))))

(defn graph-view-draw-edges []
  (doseq [[node adjacents] (:data (:model @graph-view))]
    (doseq [adjacent adjacents]
      (js/line (* (:x node) (:size @graph-view))
               (* (:y node) (:size @graph-view))
               (* (:x adjacent) (:size @graph-view))
               (* (:y adjacent) (:size @graph-view))))))

(defn draw []
  (js/background "#ffffff")
  (js/strokeWeight 2)
  (graph-view-draw-edges)
  (graph-view-draw-vertices))

(defn mouse-clicked []
  (js/console.log "clicked")
  (doseq [node (graph-view-get-vertices)]
    (let [x (* (:x node) (:size @graph-view))
          y (* (:y node) (:size @graph-view))
          size (* (:size @graph-view) (:scale @graph-view))]
      (if (mouse-in-rect? js/mouseX js/mouseY x y (/ size 2))
        [(js/console.log (str "Previous: " @graph-view))
         (swap! graph-view assoc :model (s/set-visibility (:model @graph-view) node (not (:visible node))))
         (js/console.log (str "Post: "@graph-view))]))))

;; -------------------------
;; Initialize app



(set! (.-setup js/window) setup)
(set! (.-draw js/window) draw)
(set! (.-mouseClicked js/window) mouse-clicked)

(js/console.log (str @graph-view))
(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))

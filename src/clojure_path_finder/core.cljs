(ns clojure-path-finder.core
  (:require
   [reagent.dom :as d]
   [clojure-path-finder.components.header :as h]
   [clojure-path-finder.state :as s]))


;; -------------------------
;; Views



;; Drawing library

(defn setup []
  (js/createCanvas js/windowWidth js/windowHeight)
  (js/smooth))

(def graph-view
  (atom {:model s/table-graph :source (s/new-vertex 1 1) :destination (s/new-vertex 2 3) :path #{} :size 20 :scale 0.8}))




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
        (js/strokeWeight 2)
        (js/strokeWeight 1))
      ;;Si el puntero esta en el casillero y el mouseClick esta activo, pinto el fondo.
      (if  (not (s/has-adjacents (:model @graph-view) node))
        (js/fill "#767c79")
        (js/fill "#ffffff"))
      
             (if (contains? (:path @graph-view) node)
               (js/fill "#cfc627"))
      (if (= (:source @graph-view) node)
        (js/fill "#2163e8"))

      (if (= (:destination @graph-view) node)
         (js/fill "#f81919"))
      
      ;;Dibujo el rectangulo del casillero
      (js/rect (- x (/ size 2)) (- y (/ size 2)) size size)

      (if (= (:source @graph-view) node)
        [(js/fill "#000000")
         (js/text "O" (- x 4.7) (+ y 4))])

      (if (= (:destination @graph-view) node)
        [(js/fill "#000000")
         (js/text "D" (- x 4) (+ y 4))]))))

(defn graph-view-draw-edges []
  (doseq [[node adjacents] (:data (:model @graph-view))]
    (doseq [adjacent adjacents]
      (js/line (* (:x node) (:size @graph-view))
               (* (:y node) (:size @graph-view))
               (* (:x adjacent) (:size @graph-view))
               (* (:y adjacent) (:size @graph-view))))))

(defn draw []
  (js/stroke "#000000")
  (js/background "#ffffff")
  (graph-view-draw-edges)
  (graph-view-draw-vertices))

(defn mouse-clicked []
  (doseq [node (graph-view-get-vertices)]
    (let [x (* (:x node) (:size @graph-view))
          y (* (:y node) (:size @graph-view))
          is-start (= (:source @graph-view) node)
          is-end (= (:destination @graph-view) node)
          has-adjacents (s/has-adjacents (:model @graph-view) node)
          size (* (:size @graph-view) (:scale @graph-view))
          shift-pressed? (= 16 (when (js/keyIsDown 16) js/keyCode))
          ctrl-pressed? (= 17 (when (js/keyIsDown 17) js/keyCode))]

      (if (mouse-in-rect? js/mouseX js/mouseY x y (/ size 2))
        [;;(js/console.log (str "Previous: " @graph-view))
         ;;(swap! graph-view assoc :model (s/set-visibility (:model @graph-view) node (not (:visible node))))
         (if (and (not is-start) (not is-end) (not shift-pressed?) (not ctrl-pressed?))
           (if has-adjacents
             (swap! graph-view assoc :model (s/hide-vertex (:model @graph-view) node))
             (swap! graph-view assoc :model (s/unhide-vertex (:model @graph-view) node))))
         (if (and has-adjacents shift-pressed? (not is-end))
           (swap! graph-view assoc :source node))
         (if (and has-adjacents ctrl-pressed? (not is-start))
           (swap! graph-view assoc :destination node))]))))

(defn start-button-action []
  (let [path-dfs (s/dfs (:model @graph-view) (:source @graph-view) (:destination @graph-view))]
       (js/console.log (str "camino dfs: " path-dfs))
       (swap! graph-view assoc :path path-dfs)
       )
  )

(defn home-page []
  [:div
   [h/header-component start-button-action]])


;; -------------------------
;; Initialize app


;;Bindings javascript - clojure
(set! (.-setup js/window) setup)
(set! (.-draw js/window) draw)
(set! (.-mouseClicked js/window) mouse-clicked)

(js/console.log (str @graph-view))

(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
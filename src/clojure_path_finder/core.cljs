(ns clojure-path-finder.core
  (:require
   [reagent.dom :as d]
   [clojure-path-finder.components.header :as h]
   [clojure-path-finder.state :as s]
   [clojure.string :as str]
  ;;  [cheshire.core :as json]
   ))


;; -------------------------
;; Views


;; Drawing library

(defn setup []
  (js/createCanvas (* js/windowWidth 0.9) js/windowHeight)
  (js/smooth))

(def graph-view
  (atom {:model s/table-graph
         :source (s/new-vertex 1 1)
         :destination (s/new-vertex 2 3)
         :path-queue []
         :path {}
         :visited-queue []
         :visited {}
         :algorithm s/dfs
         :size 60
         :scale 0.8}))


(defn graph-view-get-vertices []
  (keys (:data (:model @graph-view))))

(defn mouse-in-rect? [x y c_x c_y r]
  (let [dx (- x c_x)
        dy (- y c_y)
        dx2 (* dx dx)
        dy2 (* dy dy)
        d2 (+ dx2 dy2)]
    (< d2 (* r r))))

(js/window.setInterval
 (fn []
   (let [first-path-node    (first (:path-queue @graph-view))
         new-path-queue     (drop 1 (:path-queue @graph-view))
         new-path           (assoc (:path @graph-view) first-path-node true)
         first-visited-node (first (:visited-queue @graph-view))
         new-visited-queue  (drop 1 (:visited-queue @graph-view))
         new-visited        (assoc (:visited @graph-view) first-visited-node true)]
     (if (empty? new-visited-queue)
       [(swap! graph-view assoc :path-queue new-path-queue)
        (swap! graph-view assoc :path new-path)]
       [(swap! graph-view assoc :visited-queue new-visited-queue)
        (swap! graph-view assoc :visited new-visited)])))
 50)


(defn reset-graph []
  (swap! graph-view assoc :visited-queue [])
  (swap! graph-view assoc :path-queue [])
  (swap! graph-view assoc :path {})
  (swap! graph-view assoc :visited {}))

(defn hard-reset-graph []
  (reset-graph)
  (swap! graph-view assoc :model s/table-graph)
  (swap! graph-view assoc :source (s/new-vertex 1 1))
  (swap! graph-view assoc :destination (s/new-vertex 2 3)))


(defn read-file-web [onload-cb onerror-cb]
  (let [input-file (js/document.getElementById "upload-map")
        file-list  (.-files input-file)
        file       (first file-list)
        file-reader (js/FileReader.)]
    (.readAsText file-reader file "UTF-8")
    (set! (.-onload file-reader) onload-cb)
    (set! (.-onerror file-reader) onerror-cb)))

(defn- vertex-from-dict [d]
  (s/new-vertex (get d "x") (get d "y")))
(defn json-to-map [raw]
  (let [parsed (js->clj (.parse js/JSON raw))
        data   (get (get parsed "model") "data")
        temp-keys (map
                   (fn [d] (map
                            #(js/parseInt (last (str/split % #" ")))
                            (str/split (subs d 1 (- (count d) 1)) #", ")))
                   (keys data))
        keys    (map (fn [[x y]] (s/new-vertex x y)) temp-keys)
        values  (map (fn [l] (map vertex-from-dict l)) (vals data))
        model   (zipmap keys values)]
    (swap! graph-view assoc :model (s/create-graph model))
    (swap! graph-view assoc :source (vertex-from-dict (get parsed "source")))
    (swap! graph-view assoc :destination (vertex-from-dict (get parsed "destination")))
    (swap! graph-view assoc :path-queue (get parsed "path-queue"))
    (swap! graph-view assoc :path (get parsed "path"))
    (swap! graph-view assoc :visited-queue (get parsed "visited-queue"))
    (swap! graph-view assoc :visited (get parsed "visited"))
    (swap! graph-view assoc :size (get parsed "size"))
    (swap! graph-view assoc :scale (get parsed "scale"))))

(defn map-to-json [map]
  (.stringify js/JSON (clj->js map) nil "\t"))

(defn read-loaded-file [event]
  (let [raw    (.-result (.-target event))]
    (reset! graph-view (json-to-map raw))))

(defn handle-error-file [_]
  (js/alert "No se pudo cargar el archivo"))

(def read-file-web-p (partial read-file-web read-loaded-file handle-error-file))

(defn download-map [] 
  ;; var element = document.createElement('a');
  ;; element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
  ;; element.setAttribute('download', filename);

  ;; element.style.display = 'none';
  ;; document.body.appendChild(element);

  ;; element.click();

  ;; document.body.removeChild(element);
  (let [output (.getElementById js/document "saved-map")]
    (set! (.-innerText output) (str (map-to-json @graph-view)))
    (.select output)
    (.execCommand js/document "copy")
    (js/alert "Se copió la información del mapa al portapapeles")))


(defn graph-view-draw-vertices []
  (doseq [node (graph-view-get-vertices)]
    (let [x (* (:x node) (:size @graph-view))
          y (* (:y node) (:size @graph-view))
          size (* (:size @graph-view) (:scale @graph-view))]
      ;;Si el puntero esta en el casillero, muestro el borde grueso, sino normal.
      (if (mouse-in-rect? js/mouseX js/mouseY x y (/ size 2))
        [(js/strokeWeight 2)
         (js/cursor js/HAND)]
        (js/strokeWeight 1))
      (js/fill "#ffffff")
      ;;Si el puntero esta en el casillero y el mouseClick esta activo, pinto el fondo.
      (when (get (:visited @graph-view) node)
        (js/fill "#57a1e6"))

      (when (get (:path @graph-view) node)
        (js/fill "#cfc627"))

      (when (not (s/has-adjacents (:model @graph-view) node))
        (js/fill "#767c79"))

      (when (= (:source @graph-view) node)
        (js/fill "#2163e8"))

      (when (= (:destination @graph-view) node)
        (js/fill "#f81919"))

      ;;Dibujo el rectangulo del casillero
      (js/rect (- x (/ size 2))
               (- y (/ size 2))
               size
               size)

      (when (= (:source @graph-view) node)
        [(js/fill "#000000")
         (js/text "O" (- x 4.7) (+ y 4))])

      (when (= (:destination @graph-view) node)
        [(js/fill "#000000")
         (js/text "D" (- x 4) (+ y 4))]))))

(defn graph-view-draw-edges []
  (let [graph-model (:data (:model @graph-view))
        edges (apply concat
                     (map (fn [[node neighbors]]
                            (map
                             (fn [neighbor] [node neighbor])
                             neighbors))
                          graph-model))
        unique-edges (into #{} edges)]

    (doseq [[a b] unique-edges]
      (js/stroke "#000000")
      (js/strokeWeight 1)
      (js/line (* (:x a) (:size @graph-view))
               (* (:y a) (:size @graph-view))
               (* (:x b) (:size @graph-view))
               (* (:y b) (:size @graph-view))))))


(defn draw []
  (js/stroke "#000000")
  (js/strokeWeight 1)
  (js/background "#ffffff")
  (js/cursor js/ARROW)
  (graph-view-draw-edges)
  (graph-view-draw-vertices))

(defn mouse-clicked []
  (doseq [node (graph-view-get-vertices)]
    (let [x (* (:x node) (:size @graph-view))
          y (* (:y node) (:size @graph-view))
          is-start? (= (:source @graph-view) node)
          is-end? (= (:destination @graph-view) node)
          has-adjacents (s/has-adjacents (:model @graph-view) node)
          size (* (:size @graph-view) (:scale @graph-view))
          is-shift-pressed? (js/keyIsDown js/SHIFT)
          is-ctrl-pressed?  (js/keyIsDown js/CONTROL)]

      (when (mouse-in-rect? js/mouseX js/mouseY x y (/ size 2))
        [(when (and (not is-start?)
                    (not is-end?)
                    (not is-shift-pressed?)
                    (not is-ctrl-pressed?))
           (if has-adjacents
             (swap! graph-view assoc :model (s/hide-vertex (:model @graph-view) node))
             (swap! graph-view assoc :model (s/unhide-vertex (:model @graph-view) node))))
         (when (and has-adjacents is-shift-pressed? (not is-end?))
           (swap! graph-view assoc :source node))
         (when (and has-adjacents is-ctrl-pressed? (not is-start?))
           (swap! graph-view assoc :destination node))]))))


(defn start-button-action []
  (reset-graph)

  (try
    (let [path-solution ((:algorithm @graph-view)
                         (:model @graph-view)
                         (:source @graph-view)
                         (:destination @graph-view))]
    ;; (js/console.log (str "camino solution: " path-solution))
      (swap! graph-view assoc :visited-queue (:visited path-solution))
      (swap! graph-view assoc :path-queue (:path path-solution)))

    (catch js/Error e
      (js/alert (str e)))))


(defn home-page []
  [:div
   [h/header-component start-button-action
    hard-reset-graph
    read-file-web-p
    download-map
    graph-view
    {"DFS" s/dfs
     "BFS" s/bfs
     "Sh-DFS" s/shuffle-dfs}]
   [:textarea#saved-map {:readOnly ""}]])


;; -------------------------
;; Initialize app


;;Bindings javascript - clojure
(set! (.-setup js/window) setup)
(set! (.-draw js/window) draw)
(set! (.-mouseClicked js/window) mouse-clicked)



(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
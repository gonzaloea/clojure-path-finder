(ns clojure-path-finder.state)

(defrecord Graph [data])

(defn create-graph
  ([] (Graph. {}))
  ([data] (Graph. data)))


(defn add-vertex [self vertex]
  (if (contains? (:data self) vertex)
    self
    (create-graph (assoc (:data self) vertex []))))

(defn get-adjacents [self vertex]
  (get (:data self) vertex))


(defn add-adjacent [self vertex adjacent]
  (let [old-adjacent (get-adjacents self vertex)
        new-adjacent (conj old-adjacent adjacent)
        new-data (assoc (:data self) vertex new-adjacent)]
    (create-graph new-data)))

(defn new-vertex
  ([x y] (into {} (map (fn [[k v]] [k v]) {:x x :y y}))))


(defn has-adjacents [self vertex]
  (seq (get-adjacents self vertex)))


(defn hide-vertex [self vertex]
  (let [adjacents (get-adjacents self vertex)
        new-data (assoc (dissoc (:data self) vertex) vertex [])]
    (create-graph (reduce (fn [g a] (let [new-adjacents (set (filter (fn [b]
                                                                       (not= b vertex))
                                                                     (get g a #{})))]
                                      (assoc (dissoc g a)
                                             a new-adjacents))) new-data adjacents))))




(defn add-edge [self u v]
  (let [one-way-graph (add-adjacent self u v)]
    ;; (add-adjacent one-way-graph v u)))
    one-way-graph))

(defn table-edges [self vertex]
  (let [x (:x vertex)
        y (:y vertex)
        left-vertex (new-vertex (- x 1) y)
        right-vertex (new-vertex (+ x 1) y)
        bottom-vertex (new-vertex x (+ y 1))
        top-vertex (new-vertex x (- y 1))]
    (reduce (fn [g [u v]] (add-edge g u v)) self (filter
                                                  (fn [[u v]] (contains? (:data self) v))
                                                  [[vertex right-vertex]
                                                   [vertex bottom-vertex]
                                                   [vertex left-vertex]
                                                   [vertex top-vertex]]))))

(defn table-edges-avoid-empty [self vertex]
  (let [x (:x vertex)
        y (:y vertex)
        left-vertex (new-vertex (- x 1) y)
        right-vertex (new-vertex (+ x 1) y)
        top-vertex (new-vertex x (+ y 1))
        bottom-vertex (new-vertex x (- y 1))]
    (reduce (fn [g [u v]] (add-edge g u v)) self (filter
                                                  (fn [[u v]] (and (contains? (:data self) v) (has-adjacents self v)))
                                                  [[vertex right-vertex]
                                                   [vertex bottom-vertex]
                                                   [vertex left-vertex]
                                                   [vertex top-vertex]]))))

(defn unhide-vertex [self vertex]
  (table-edges-avoid-empty self vertex))

(def edgeless-graph
  (reduce add-vertex
          (create-graph)
          (into []
                (for [x (range 1 24) y (range 1 11)]
                  (new-vertex x y)))))

(def table-graph
  (reduce table-edges edgeless-graph (keys (:data edgeless-graph))))


(defn bfs
  [graph source destination]
  (loop [paths #queue []
         visited [source]
         s [source]]

    (let [neighbors (into [] (filter (fn [v] (not (some #(= % v) visited))) (get-adjacents graph (last s))))
          new-paths (into #queue [] (concat paths (into #queue [] (concat (map (fn [n] (into [] (conj s n))) neighbors)))))
          actual-path (into [] (peek new-paths))]

      (js/console.log "last: " (str (last actual-path)))

      (if (= (last actual-path) destination)
        {:path actual-path :visited visited}
        (recur
         (pop new-paths)
         (into [] (if (some #(= % (last actual-path)) visited) visited (concat visited [(last actual-path)])))
         actual-path)))))

(defn dfs
  [graph source destination]
  (loop [paths []
         visited [source]
         s [source]]
    (let [neighbors (into [] (filter (fn [v] (not (some #(= % v) visited))) (get-adjacents graph (last s))))
          new-paths (into [] (concat paths (into  [] (concat (map (fn [n] (into [] (conj s n))) neighbors)))))
          actual-path (into [] (last new-paths))]
      (if (= (last actual-path) destination)
        {:path actual-path :visited (into [] (if (some #(= % (last actual-path)) visited) visited (concat visited [(last actual-path)])))}
        (recur
         (pop new-paths)
         (into [] (if (some #(= % (last actual-path)) visited) visited (concat visited [(last actual-path)])))
         actual-path)))))


;; -----------algoritmo A*-------------------------------

(defn manhattan-distance [[x1 y1] [x2 y2]]
  (+ (Math/abs ^Integer (- x2 x1)) (Math/abs ^Integer (- y2 y1))))

(defn costo [curr start end]
  (let [g (manhattan-distance [(:x start) (:y start)] [(:x curr) (:y curr)])
        h (manhattan-distance [(:x curr) (:y curr)] [(:x end) (:y end)])
        f (+ g h)]
    ;; [f g h]))
    [f]))

;; no funca, creo que estoy metiendo la pata en reduce porque no calcula el costo
;; después lo sigo
(defn a*
  [graph source destination]
  ;;  (js/console.log (str "#############"))
  (js/console.log (str "destination: " destination))
  (loop [paths #queue []
         visited [source]
         s [source]]
    (let [neighbors (into [] (filter (fn [v] (not (some #(= % v) visited))) (get-adjacents graph (last s))))
          ;;  nextNode (reduce (fn [x, y] (if (> costo x source destination costo y source destination) y x ) ) neighbors)
          ;;  new-paths (into #queue [] (concat paths (into #queue [] (concat (reduce (fn [x, y] (if (<= (costo x source destination) cost y source destination x y))) neighbors)))))
          new-paths (into #queue [] (concat paths (into #queue [] (concat (reduce (fn [x, y] (if (<= costo x source destination costo y source destination) x y)) neighbors)))))
          ;;  new-paths (into #queue [] (concat paths (into #queue [] (concat (reduce (fn costo [x, y] (if (<= (x source destination) (y source destination)) x y)) neighbors)))))
          actual-path (into [] (peek new-paths))]
      ;;  (js/console.log "neighbors: " neighbors)
       ;; (js/console.log "new-paths: " (str new-paths))
      (js/console.log (str "actual-path: " actual-path))
      (if (= (last actual-path) destination)
        {:path actual-path :visited visited}
        (recur
         (pop new-paths)
         (into [] (if (some #(= % (last actual-path)) visited) visited (concat visited [(last actual-path)])))
         actual-path)))))
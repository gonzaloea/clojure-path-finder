(ns clojure-path-finder.state
  (:require [reagent.core :as r]))

(defrecord Graph [data])

(defn create-graph
  ([] (Graph. {}))
  ([data] (Graph. data)))

(defn node-compare [n1 n2] 
  (< (:x n1) (:x n2)))

(defn add-vertex [self vertex]
  (if (contains? (:data self) vertex)
    self
    (create-graph (assoc (:data self) vertex (sorted-set-by node-compare )))))

(defn get-adjacents [self vertex]
  (get (:data self) vertex))


(defn add-adjacent [self vertex adjacent]
  (let [old-adjacent (get-adjacents self vertex)
        new-adjacent (conj old-adjacent adjacent)
        new-data (assoc (:data self) vertex new-adjacent)]
    (if (contains? old-adjacent adjacent)
      self
      (create-graph new-data))))

(defn new-vertex
  ([x y] (into {} (map (fn [[k v]] [k v]) {:x x :y y }))))


(defn has-adjacents [self vertex]
  (not (empty? (get-adjacents self vertex)))
  )


(defn hide-vertex [self vertex]
  (let [adjacents (get-adjacents self vertex)
        new-data (assoc (dissoc (:data self) vertex) vertex (sorted-set-by node-compare))]
    (create-graph (reduce (fn [g a] (let [new-adjacents (set (filter (fn [b]
                                                                       (not= b vertex))
                                                                     (get g a #{})))]
                                      (assoc (dissoc g a)
                                             a new-adjacents))) new-data adjacents))))




(defn add-edge [self u v]
  (let [one-way-graph (add-adjacent self u v)]
    (add-adjacent one-way-graph v u)))

(defn table-edges [self vertex]
  (let [x (:x vertex)
        y (:y vertex)
        left-vertex (new-vertex (- x 1) y)
        right-vertex (new-vertex (+ x 1) y)
        top-vertex (new-vertex x (+ y 1))
        bottom-vertex (new-vertex x (- y 1))]
    (reduce (fn [g [u v]] (add-edge g u v)) self (filter
                                                  (fn [[u v]] (contains? (:data self) v))
                                                  [[vertex left-vertex]
                                                   [vertex right-vertex]
                                                   [vertex top-vertex]
                                                   [vertex bottom-vertex]]))))

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
                                                   [vertex top-vertex]
                                                   ]))))

(defn unhide-vertex [self vertex]
  (table-edges-avoid-empty self vertex))

(def edgeless-graph
  (reduce add-vertex
          (create-graph)
          (into []
                (for [x (range 1 60) y (range 1 30)]
                  (new-vertex x y)))))



(def edges
  [[{:x 1 :y 1 } {:x 2 :y 1 }]
   [{:x 1 :y 1 } {:x 1 :y 2 }]
   [{:x 2 :y 1 } {:x 1 :y 2 }]
   [{:x 2 :y 1 } {:x 3 :y 1 }]
   [{:x 2 :y 1 } {:x 2 :y 2 }]
   [{:x 3 :y 1 } {:x 4 :y 1 }]
   [{:x 3 :y 1 } {:x 3 :y 2 }]
   [{:x 4 :y 1 } {:x 4 :y 2 }]
   [{:x 1 :y 2 } {:x 2 :y 2 }]
   [{:x 2 :y 2 } {:x 3 :y 2 }]
   [{:x 3 :y 2 } {:x 4 :y 2 }]])

(def graph (reduce (fn [g [u v]] (add-edge g u v))
                   edgeless-graph edges))



(def table-graph
  (reduce table-edges edgeless-graph (keys (:data edgeless-graph))))

;; (defn dfs
;;   ([graph source destination] (dfs graph source destination #{} ))
;;   ([graph source destination visited]

;;    (let [new-visited (conj visited source)]
;;     (js/console.log "visited: " (str visited))
;;      (if (or (= source destination) (contains? visited source))
;;        (if (= source destination) 
;;          (conj new-visited destination)
;;          new-visited )
;;        (reduce (fn  [adjacents v]
;;                  (dfs graph v destination new-visited)) new-visited (get-adjacents graph source))))))


(defn dfs [g s d]
  (loop [vertices [] explored #{s} frontier [s]]
    (if (or (empty? frontier) (= (last explored) d)) 
      explored
      (let [v (peek frontier)
            neighbors (get-adjacents g v)]
        (recur
         (conj vertices v)
         (into explored neighbors)
         (into (pop frontier) (remove explored neighbors)))))))
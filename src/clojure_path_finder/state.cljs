(ns clojure-path-finder.state
  (:require [reagent.core :as r]))

(defrecord Graph [data])

(defn create-graph
  ([] (Graph. {}))
  ([data] (Graph. data)))

(defn add-vertex [self vertex]
  (if (contains? (:data self) vertex)
    self
    (create-graph (assoc (:data self) vertex []))))

(defn get-adjacents [self vertex]
  (get (:data self) vertex []))


(defn add-adjacent [self vertex adjacent]
  (let [old-adjacent (get-adjacents self vertex)
        new-adjacent (conj old-adjacent adjacent)
        new-data (assoc (:data self) vertex new-adjacent)]
    (create-graph new-data)))

(defn new-vertex
  ([x y] (into {} (map (fn [[k v]] [k v]) {:x x :y y :visible true})))
  ([x y visible] (into {} (map (fn [[k v]] [k v]) {:x x :y y :visible visible}))))


(defn set-visibility [self vertex visibility]
  (let [new-v (new-vertex (:x vertex) (:y vertex) visibility)
        adjacent (get-adjacents self vertex)
        new-data (assoc (dissoc (:data self) vertex) new-v adjacent)]
    (create-graph new-data)))

(defn add-edge [self u v]
  (let [one-way-graph (add-adjacent self u v)]
    (add-adjacent one-way-graph v u)))

(def edgeless-graph
  (reduce add-vertex
          (create-graph)
          (into []
                (for [x (range 1 41) y (range 1 21)]
                  (new-vertex x y true)))))



(def edges
  [[{:x 1 :y 1 :visible true} {:x 2 :y 1 :visible true}]
   [{:x 1 :y 1 :visible true} {:x 1 :y 2 :visible true}]
   [{:x 2 :y 1 :visible true} {:x 1 :y 2 :visible true}]
   [{:x 2 :y 1 :visible true} {:x 3 :y 1 :visible true}]
   [{:x 2 :y 1 :visible true} {:x 2 :y 2 :visible true}]
   [{:x 3 :y 1 :visible true} {:x 4 :y 1 :visible true}]
   [{:x 3 :y 1 :visible true} {:x 3 :y 2 :visible true}]
   [{:x 4 :y 1 :visible true} {:x 4 :y 2 :visible true}]
   [{:x 1 :y 2 :visible true} {:x 2 :y 2 :visible true}]
   [{:x 2 :y 2 :visible true} {:x 3 :y 2 :visible true}]
   [{:x 3 :y 2 :visible true} {:x 4 :y 2 :visible true}]])

 (def graph (reduce (fn [g [u v]] (add-edge g u v))
                    edgeless-graph edges))

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




(def table-graph
  (reduce table-edges edgeless-graph (keys (:data edgeless-graph))))

(defn dfs
  ([graph source destination] (dfs graph source destination #{} {}))
  ([graph source destination visited path]
 
   (let [new-visited (conj visited source)]
     (if (or (= source destination) (contains? visited source))
       new-visited
       (reduce (fn  [adjacents v]
                 (js/console.log (str adjacents))
                 (dfs graph v destination adjacents (assoc path v source))) new-visited (get-adjacents graph source))))))




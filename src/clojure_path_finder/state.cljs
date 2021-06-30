(ns clojure-path-finder.state
  (:require [reagent.core :as r]))

(def state (r/atom [{:id 1 :x 1 :y 1 :adjacents [2 5]}
                    {:id 2 :x 2 :y 1 :adjacents [1 3 6]}
                    {:id 3 :x 3 :y 1 :adjacents [2 4 7]}
                    {:id 4 :x 4 :y 1 :adjacents [3 8]}
                    {:id 5 :x 1 :y 2 :adjacents [1 6]}
                    {:id 6 :x 2 :y 2 :adjacents [5 2 7]}
                    {:id 7 :x 3 :y 2 :adjacents [3 5 8]}
                    {:id 8 :x 4 :y 2 :adjacents [4 7]}]))

;; TODO: Enacapsular el nodo {x:... y:...}
;; (def newstate {{:x 1 :y 1} [{:x 2 :y 1} {:x 1 :y 2}]
;;                 {:x 2 :y 1} [{:x 2 :y 1} {:x 3 :y 1} {:x 2 :y 2}]
;;                 {:x 3 :y 1} [{:x 2 :y 1} {:x 4 :y 1} {:x 3 :y 2}]
;;                 {:x 4 :y 1} [{:x 3 :y 1} {:x 4 :y 2}]
;;                 {:x 1 :y 2} [{:x 2 :y 1} {:x 2 :y 2}]
;;                 {:x 2 :y 2} [{:x 1 :y 2} {:x 2 :y 1} {:x 3 :y 2}]
;;                 {:x 3 :y 2} [{:x 3 :y 1} {:x 1 :y 2} {:x 4 :y 2}]
;;                 {:x 4 :y 2} [{:x 4 :y 1} {:x 3 :y 2}]})

(defrecord Graph [data])

(defn create-graph
  ([] (Graph. {}))
  ([data] (Graph. data)))

(defn add-vertex [self vertex]
  (if (contains? (:data self) vertex)
    self
    (create-graph (assoc (:data self) vertex []))))

(defn get-adjacents [self vertex]
  ((:data self) vertex))

(defn add-adjacent [self vertex adjacent]
  (let [old-adjacent (get-adjacents self vertex)
        new-adjacent (conj old-adjacent adjacent)
        new-data (assoc (:data self) vertex new-adjacent)]
    (create-graph new-data)))

(defn add-edge [self u v]
  (let [one-way-graph (add-adjacent self u v)]
    (add-adjacent one-way-graph v u)))

(def edgeless-graph
  (reduce add-vertex
          (create-graph)
          [{:x 1 :y 1}
           {:x 2 :y 1}
           {:x 3 :y 1}
           {:x 4 :y 1}
           {:x 1 :y 2}
           {:x 2 :y 2}
           {:x 3 :y 2}
           {:x 4 :y 2}]))

(def edges [[{:x 1 :y 1} {:x 2 :y 1}]
            [{:x 1 :y 1} {:x 1 :y 2}]
            [{:x 2 :y 1} {:x 1 :y 2}]
            [{:x 2 :y 1} {:x 3 :y 1}]
            [{:x 2 :y 1} {:x 2 :y 2}]
            [{:x 3 :y 1} {:x 4 :y 1}]
            [{:x 3 :y 1} {:x 3 :y 2}]
            [{:x 4 :y 1} {:x 4 :y 2}]
            [{:x 1 :y 2} {:x 2 :y 2}]
            [{:x 2 :y 2} {:x 3 :y 2}]
            [{:x 3 :y 2} {:x 4 :y 2}]])

(def graph (reduce (fn [g [u v]] (add-edge g u v))
                   edgeless-graph edges))

(js/console.log (str (:data graph)))

(defn dfs
  ([graph source destination] (dfs graph source destination #{} {}))
  ([graph source destination visited path]

  (let [new-visited (conj visited source)]
   (if (or (= source destination) (contains? visited source))
     new-visited
     (reduce (fn  [adjacents v]
               (js/console.log (str adjacents))
               (dfs graph v destination adjacents (assoc path v source))) new-visited (get-adjacents graph source))))))


;; (js/console.log (str (dfs graph {:x 3 :y 1} {:x 4 :y 2})))

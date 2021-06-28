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
(def newstate (r/atom {{:x 1 :y 1} [{:x 2 :y 1} {:x 1 :y 2}]
                       {:x 2 :y 1} [{:x 2 :y 1} {:x 3 :y 1} {:x 2 :y 2}]
                       {:x 3 :y 1} [{:x 2 :y 1} {:x 4 :y 1} {:x 3 :y 2}]
                       {:x 4 :y 1} [{:x 3 :y 1} {:x 4 :y 2}]
                       {:x 1 :y 2} [{:x 2 :y 1} {:x 2 :y 2}]
                       {:x 2 :y 2} [{:x 1 :y 2} {:x 2 :y 1} {:x 3 :y 2}]
                       {:x 3 :y 2} [{:x 3 :y 1} {:x 1 :y 2} {:x 4 :y 2}]
                       {:x 4 :y 2} [{:x 4 :y 1} {:x 3 :y 2}]}))
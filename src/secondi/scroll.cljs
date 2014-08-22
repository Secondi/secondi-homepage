(ns secondi.scroll
  (:require [secondi.reactive :refer [listen by-id]]
            [cljs.core.async :refer [<!]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn when-scrolling [f]
  (let [scroll-c (listen js/window :scroll)]
  (go (while true
        (let [result (<! scroll-c)
              y-position (.. result -currentTarget -pageYOffset)]
          (f y-position))))))

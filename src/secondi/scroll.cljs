(ns secondi.scroll
  (:require [secondi.reactive :refer [listen by-id]]
            [cljs.core.async :refer [<!]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn when-scrolling [f]
  (let [scroll-c (listen js/window :scroll)]
  (go (while true
        (<! scroll-c)
        (f)))))

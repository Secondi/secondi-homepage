(ns secondi.transition
  (:require [secondi.reactive :refer [listen by-id]]
            [cljs.core.async :refer [<!]])
  (:import [goog.fx.css3 Transition])
  (:require-macros [cljs.core.async.macros :refer [go]]))

;; protocol
;; ----------------------------------------------------------------------------

(defprotocol ICssTransition
  (play [this]))

;; records
;; ----------------------------------------------------------------------------

(defrecord CssTransition [transition]
  ICssTransition
  (play [this]
        (.play transition)))

(defn css-transition [el duration {:keys [initial final transitions]}]
  (->CssTransition (Transition. el duration (clj->js initial) (clj->js final) (clj->js transitions))))

(def transitions {:rotateRoomLeft {:in {:initial {:opacity 0.3
                                               :-webkit-transform-origin "0% 50%"
                                               :transform-origin "0% 50%"
                                               :-webkit-transform "translateX(100%) rotateY(-90deg)"
                                               :transform "translateX(100%) rotateY(-90deg)"}
                                        :final {:opacity 1
                                             :-webkit-transform "translateX(0%) rotateY(0deg)"
                                             :transform "translateX(0%) rotateY(0deg)"}
                                        :transitions [{:property "opacity"
                                             :duration 0.8
                                             :timing "ease"
                                             :delay 0
                                             :fill-mode "both"}
                                            {:property "transform"
                                             :duration 0.8
                                             :timing "ease"
                                             :delay 0
                                             :fill-mode "both"}
                                            {:property "-webkit-transform"
                                             :duration 0.8
                                             :timing "ease"
                                             :delay 0
                                             :fill-mode "both"}]}
                                   :out {:initial {:opacity 1
                                                   :-webkit-transform-origin "100% 50%"
                                                   :transform-origin "100% 50%"
                                                   :-webkit-transform "translateX(0%) rotateY(0deg)"
                                                   :transform "translateX(0%) rotateY(0deg)"}
                                         :final {:opacity 0.3
                                                 :-webkit-transform "translateX(-100%) rotateY(90deg)"
                                                 :transform "translateX(-100%) rotateY(90deg)"}
                                         :transitions [{:property "opacity"
                                             :duration 0.8
                                             :timing "ease"
                                             :delay 0
                                             :fill-mode "both"}
                                            {:property "transform"
                                             :duration 0.8
                                             :timing "ease"
                                             :delay 0
                                             :fill-mode "both"}
                                            {:property "-webkit-transform"
                                             :duration 0.8
                                             :timing "ease"
                                             :delay 0
                                             :fill-mode "both"}]}}})

(defn transition-in [el style]
  (css-transition el 0.8 (:in (style transitions))))

(defn transition-out [el style]
  (css-transition el 0.8 (:out (style transitions))))

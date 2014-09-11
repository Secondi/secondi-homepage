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

(def effects {:moveFromBottom {:initial {:-webkit-transform "translateY(100%)"
                                         :transform "translateY(100%)"}
                               :final {:-webkit-transform "translateY(0%)"
                                       :transform "translateY(0%)"}
                               :transitions [{:property "transform"
                                              :duration 0.6
                                              :timing "ease"
                                              :delay 0
                                              :fill-mode "both"}
                                             {:property "-webkit-transform"
                                              :duration 0.6
                                              :timing "ease"
                                              :delay 0
                                              :fill-mode "both"}]}
              :scaleDown {:initial {:opacity 1
                                    :-webkit-transform "scale(1)"
                                    :transform "scale(1)"}
                          :final {:opacity 0
                                  :-webkit-transform "scale(0.8)"
                                  :transform "scale(0.8)"}
                          :transitions [{:property "opacity"
                                         :duration 0.7
                                         :timing "ease"
                                         :delay 0
                                         :fill-mode "both"}
                                        {:property "transform"
                                         :duration 0.7
                                         :timing "ease"
                                         :delay 0
                                         :fill-mode "both"}
                                        {:property "-webkit-transform"
                                         :duration 0.7
                                         :timing "ease"
                                         :delay 0
                                         :fill-mode "both"}]}
              :rotateRoomLeftIn {:initial {:opacity 0.3
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
              :rotateRoomLeftOut {:initial {:opacity 1
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
                                             :fill-mode "both"}]}})

(def transitions {:rotateRoomLeft {:in :rotateRoomLeftIn
                                   :out :rotateRoomLeftOut}
                  :scaleDownBottom {:in :moveFromBottom
                                    :out :scaleDown}})

(defn get-effect [effect]
  (let [{:keys [in out]} (effect transitions)]
    {:in (in effects)
      :out (out effects)}))

(defn get-css-transition [el effect]
  (css-transition el 0.8 effect))

(defn create-transition [el effect direction]
  (let [e (direction (get-effect effect))]
    (get-css-transition el e)))

(defn create-transitions [[el-in el-out] effect]
  (let [{:keys [in out]} (get-effect effect)]
    {:in (get-css-transition el-in in)
     :out (get-css-transition el-out out)}))

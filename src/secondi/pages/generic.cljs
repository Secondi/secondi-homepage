(ns secondi.pages.generic
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [goog.dom :as gdom]
            [secondi.reactive :refer [listen]]
            [secondi.transition :as transition]
            [cljs.core.async :refer [<!]]
            [clojure.string :as string])
  (:require-macros [cljs.core.async.macros :refer [go]]))

;; protocol
;; ----------------------------------------------------------------------------
(defprotocol IPageNavigation
  (create-slug [this]))

(defprotocol IPageState
  (handle-frontpage [this])
  (handle-isolation [this]))

(defprotocol IGenericPage)

(defprotocol ICustomPage
  (custom-page [this]))

;; records
;; ----------------------------------------------------------------------------

(defn make-slug [text]
  (-> text
      (string/lower-case)
      (string/replace " " "-")))

(defrecord GenericPage [name body-description])
(defrecord NavigatePage [page]
  IPageNavigation
  (create-slug [this]
               (make-slug (get-in this [:page :name]))))

;; primitive extension of page
;; ----------------------------------------------------------------------------


(defn generic-page [name body-description]
  (->GenericPage name body-description))

(defn navigate-page [name body-description]
  (->NavigatePage (generic-page name body-description)))


;; om component
;; ----------------------------------------------------------------------------

(def transition-in "pt-page-current")
(def transition-mid "pt-page-ontop pt-page-current")
(def transition-out "")

;; generic page view
(defn page-view [page owner]
  (reify
    om/IInitState
    (init-state [_]
                {})
    om/IWillUpdate
    (will-update [this next-props next-state]
                (when (not= (get-in next-props [:page :name]) (get-in page [:page :name]))
                  (om/set-state! owner :prev-page page)))
    om/IDidUpdate
    (did-update [this prev-props prev-state]
                (when (not= (get-in prev-props [:page :name]) (get-in page [:page :name]))
                  (let [pages (gdom/getChildren (om/get-node owner))
                        new-page (aget pages 0)
                        old-page (aget pages 1)
                        trans-new (transition/transition-in new-page :rotateRoomLeft)
                        trans-old (transition/transition-out old-page :rotateRoomLeft)
                        el-new (get-in trans-new [:transition])
                        el-old (get-in trans-old [:transition])]
                    (listen el-new :transition-play #(om/set-state! owner :transition transition-in))
                    (listen el-old :transition-end #(om/set-state! owner :prev-page nil))
                    (transition/play trans-new)
                    (transition/play trans-old))))
    om/IDidMount
    (did-mount [_]
               (let [trans (transition/transition-in (aget (gdom/getChildren (om/get-node owner)) 0) :rotateRoomLeft)
                     el (get-in trans [:transition])]
                 (listen el :transition-play #(om/set-state! owner :transition transition-in))
                 (transition/play trans)))
    om/IWillUnmount
    (will-unmount [_]
               (let [trans (transition/transition-out (om/get-node owner) :rotateRoomLeft)
                     el (get-in trans [:transition])
                     c (listen el :transition-end #(om/set-state! owner :transition transition-out))]
                 (go (let [v (<! c)]
                         (js/console.log "end")))
                 (listen el :transition-play #(om/set-state! owner :transition transition-mid))
                 (transition/play trans)))

    om/IRenderState
    (render-state [this state]
                  (dom/div nil
                           (dom/div #js {:className (str "sectionWrapper general-page pt-page " (:transition state)) :id (get-in page [:page :name])}
                                    (dom/div #js {:className "content"} (get-in page [:page :body-description] "boo")))
                           (when (:prev-page state)
                             (let [prev-page (:prev-page state)]
                               (dom/div #js {:className "sectionWrapper general-page pt-page pt-page-ontop pt-page-current" :id (get-in prev-page [:page :name])}
                                        (dom/div #js {:className "content"} (get-in prev-page [:page :body-description] "boo")))))))))

(ns secondi.pages.generic
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [secondi.reactive :refer [listen]]
            [secondi.transition :as transition]
            [cljs.core.async :refer [alts!]]
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
    om/IWillMount
    (will-mount [_]
                {})
    om/IDidMount
    (did-mount [_]
               (let [trans (transition/transition-in (om/get-node owner) :rotateRoomLeft)
                     el (get-in trans [:transition])]
                 (js/console.log "mount")
                 (listen el :transition-play #(om/set-state! owner :transition transition-in))
                 (transition/play trans)))
    om/IWillUnmount
    (will-unmount [_]
               (let [trans (transition/transition-out (om/get-node owner) :rotateRoomLeft)
                     el (get-in trans [:transition])]
                 (js/console.log "unmount")
                 (listen el :transition-play #(om/set-state! owner :transition transition-mid))
                 (listen el :transition-end #(om/set-state! owner :transition transition-out))
                 (transition/play trans)))
    om/IRenderState
    (render-state [this state]
                  (dom/div #js {:className (str "sectionWrapper general-page pt-page " (:transition state)) :id (get-in page [:page :name])}
                           (dom/div #js {:className "content"} (get-in page [:page :body-description] "boo"))))))

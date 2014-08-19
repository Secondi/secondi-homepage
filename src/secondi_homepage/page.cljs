(ns secondi.page
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(defprotocol IPageNavigation
  (navigate-to [this])
  (transition-to [this])
  (transition-away [this]))

(defprotocol IPageState
  (handle-frontpage [this])
  (handle-isolation [this]))

(defrecord GenericPage [name body-description])
(defn generic-page [name body-description]
  (->GenericPage name body-description))


;; om component
;; ----------------------------------------------------------------------------

(defn section-header [name]
  (dom/h2 nil name))

(defn section-banner [page]
  (dom/div #js {:className "panel"}
           (section-header (:name page))))

(defn page-view [page owner]
  (reify
    om/IInitState
    (init-state [_]
                {})
    om/IWillMount
    (will-mount [_]
                (js/console.log (om/get-state owner :hello)))
    om/IRenderState
    (render-state [this state]
                  (dom/div #js {:className "sectionWrapper"}
                           (section-banner page)
                           (dom/div #js {:className "content"} (:body-description page))))))

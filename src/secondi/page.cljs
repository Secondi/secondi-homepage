(ns secondi.page
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [clojure.string :as string]))

(defprotocol IPageNavigation
  (navigate-to [this])
  (transition-to [this])
  (transition-away [this]))

(defprotocol IPageState
  (handle-frontpage [this])
  (handle-isolation [this]))

(defprotocol IGenericPage)

(defrecord GenericPage [name body-description])
(defrecord NavigatePage [page slug])

(defn generic-page [name body-description]
  (->GenericPage name body-description))

(defn slug [name]
  (-> name
      (string/lower-case)
      (string/replace " " "-")))

(defn navigate-page [name body-description]
  (->NavigatePage (generic-page name body-description) (slug name)))

;; om component
;; ----------------------------------------------------------------------------

(defn section-header [name]
  (dom/h2 nil name))

(defn section-banner [page]
  (dom/div #js {:className "panel"}
           (section-header (get-in page [:page :name]))))

(defn page-view [page owner]
  (reify
    om/IInitState
    (init-state [_]
                {})
    om/IWillMount
    (will-mount [_]
                {})
    om/IRenderState
    (render-state [this state]
                  (dom/div #js {:className "sectionWrapper general-page"}
                           (dom/div #js {:className "content"} (get-in page [:page :body-description] "boo"))))))

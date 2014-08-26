(ns secondi.pages.generic
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [clojure.string :as string]))

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

;; generic page view
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

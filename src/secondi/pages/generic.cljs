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

(def transition-in "pt-page-current pt-page-ontop")
(def transition-mid "pt-page-current")
(def transition-out "")
(def trans-effect :scaleDownBottom)

(defn get-transition-el [transition]
  (get-in transition [:transition]))

(defn get-page-name [page]
  (get-in page [:page :name]))

(defn same-page? [page1 page2]
  (= (get-page-name page1) (get-page-name page2)))

;; generic page view
(defn page-view [page owner]
  (reify
    om/IInitState
    (init-state [_]
                {})
    om/IWillUpdate
    (will-update [this next-props next-state]
                (when (not (same-page? next-props page))
                  (om/set-state! owner :prev-page page)))
    om/IDidUpdate
    (did-update [this prev-props prev-state]
                (when (not (same-page? prev-props page))
                  (let [pages (gdom/getChildren (om/get-node owner))
                        {:keys [in out]} (transition/create-transitions [(aget pages 0) (aget pages 1)] trans-effect)
                        el-in (get-transition-el in)
                        el-out (get-transition-el out)]
                    (listen el-in :transition-play #(om/set-state! owner :transition transition-in))
                    (listen el-out :transition-end #(om/set-state! owner :prev-page nil))
                    (transition/play in)
                    (transition/play out))))
    om/IDidMount
    (did-mount [_]
               (let [trans (transition/create-transition (aget (gdom/getChildren (om/get-node owner)) 0) trans-effect :in)
                     el (get-in trans [:transition])]
                 (listen el :transition-play #(om/set-state! owner :transition transition-in))
                 (transition/play trans)))
    om/IWillUnmount
    (will-unmount [_]
               (let [trans (transition/create-transition (om/get-node owner) trans-effect :out)
                     el (get-in trans [:transition])
                     c (listen el :transition-end #(om/set-state! owner :transition transition-out))]
                 (listen el :transition-play #(om/set-state! owner :transition transition-mid))
                 (transition/play trans)))

    om/IRenderState
    (render-state [this state]
                  (dom/div nil
                           (dom/div #js {:className (str "sectionWrapper general-page pt-page " (:transition state)) :id (get-page-name page)}
                                    (dom/div #js {:className "content"} (get-in page [:page :body-description] "boo")))
                           (when (:prev-page state)
                             (let [prev-page (:prev-page state)]
                               (dom/div #js {:className "sectionWrapper general-page pt-page pt-page-current" :id (get-page-name prev-page)}
                                        (dom/div #js {:className "content"} (get-in prev-page [:page :body-description] "boo")))))))))

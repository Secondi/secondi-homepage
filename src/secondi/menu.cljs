(ns secondi.menu
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))


;; om component
;; ----------------------------------------------------------------------------

(defn menu-square []
  (dom/div #js {:className "nav-square"} nil))

(defn menu-item-view [item owner]
  (om/component
   (dom/li nil
           (dom/a #js {:className "link" :href (:slug item)}
                  (get-in item [:page :name])
                  (menu-square)))))

(defn menu-view [app owner]
  (reify
    om/IInitState
    (init-state [_]
                {})
    om/IWillMount
    (will-mount [_]
                (js/console.log (om/get-state owner :hello)))
    om/IRenderState
    (render-state [this state]
                  (dom/div #js {:className "menuWrapper"}
                           (apply dom/ul #js {:className "menu"}
                                  (om/build-all menu-item-view (:areas app)))))))

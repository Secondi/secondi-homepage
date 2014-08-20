(ns secondi.menu
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [secondi.page :as page]
            [clojure.string :as string]))

(defn slug [{:keys [name]}]
  (-> name
      (string/lower-case)
      (string/replace " " "-")))

(defn menu-square []
  (dom/div #js {:className "nav-square"} nil))

(defn menu-item-view [item owner]
  (om/component
   (dom/li nil
           (dom/a #js {:href (slug item)}
                  (str (:name item))
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

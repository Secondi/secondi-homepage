(ns secondi.pages.video
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [secondi.pages.generic :as generic]
            [clojure.string :as string]
            [cljs.core.async :refer [<! >! put! chan]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))


;; film types
;; ----------------------------------------------------------------------------

(defprotocol IFilm
  (prepare-film [this])
  (stream-url [this]))

(defprotocol IPlayer
  (play-player [this])
  (stop-player [this])
  (current-position [this])
  (set-position [this]))

(defrecord VimeoFilm [name uri thumbnail]
  IFilm
  (prepare-film [this] nil)
  (stream-url [this]
              (str "//player.vimeo.com" uri)))

(defn vimeo-film [name uri thumbnail _]
  (->VimeoFilm name uri thumbnail))

(defprotocol IPlaylist
  (play-next [this] [this next-film]))

(defrecord Playlist [name film-collection]
  IPlaylist
  (play-next [this] nil))

(defn playlist [name film-collection]
  (->Playlist name film-collection))

;; primitive test data
;; ----------------------------------------------------------------------------

(def temp-playlists (playlist "Secondi Vimeo" [(vimeo-film "Secondi - Red Release" "/video/82328663" "https://i.vimeocdn.com/video/458707406_200x150.jpg" 1)
                                               (vimeo-film "Secondi - Sometimes the destination is unknown (Promo #3)" "/video/82049870" "https://i.vimeocdn.com/video/458306576_200x150.jpg" 1)
                                               (vimeo-film "i'm" "/video/82328663" "https://i.vimeocdn.com/video/458707406_200x150.jpg" 1)
                                               (vimeo-film "dummy" "/video/82328663" "https://i.vimeocdn.com/video/458707406_200x150.jpg" 1)
                                               (vimeo-film "data" "/video/82328663" "https://i.vimeocdn.com/video/458707406_200x150.jpg" 1)]))

;; page extension
;; ----------------------------------------------------------------------------

(defrecord VideoPage [navigation-page]
  generic/IPageNavigation
  (create-slug [this]
               (generic/make-slug (get-in this [:navigation-page :page :name]))))

(defn video-page [name body-description]
  (->VideoPage (generic/navigate-page name body-description)))

;; playlist view component

(def playlist-corners
  (dom/div nil
           (dom/div #js {:id "corner-topleft"} nil)
           (dom/div #js {:id "corner-topright"} nil)
           (dom/div #js {:id "corner-bottomleft"} nil)
           (dom/div #js {:id "corner-bottomright"} nil)))

(def play-symbol "►")
(def pause-symbol "||")

(defn play-button [play-state]
  (condp = play-state
    :play pause-symbol
    :pause play-symbol
    :stop play-symbol))

(defn track-view [track owner]
  (reify
    om/IInitState
    (init-state [_]
                {:playing :stop})
    om/IRenderState
    (render-state [_ state]
                  (dom/div #js {:className "track"
                                :onClick #(js/console.log (str "you've clicked: " (:name track)))}
                           (dom/div #js {:className "play-button"}
                                    (dom/span nil (play-button (:playing state))))
                           (dom/span #js {:className "track-text"} (str "Track: " (:name track)))))))

(defn playlist-view [playlist owner]
  (reify
    om/IRenderState
    (render-state [_ state]
                  (dom/div #js {:id "playlist-wrapper"}
                           playlist-corners
                           (when playlist
                             (dom/div #js {:id "playlist"}
                                      (dom/h2 nil (-> playlist :name string/upper-case))
                                      (apply dom/div nil
                                             (om/build-all track-view (:track-collection playlist)))))))))

;; player view component
;; ----------------------------------------------------------------------------

(defn player-view [video owner]
  (reify
    om/IInitState
    (init-state [_]
                {})
    om/IRenderState
    (render-state [_ state]
                  (dom/div nil
                           (when-not (nil? video)
                             (dom/iframe #js {:src (stream-url video)
                                              :width "600"
                                              :height "480"
                                              :frameborder 0
                                              :webkitallowfullscreen true
                                              :mozallowfullscreen true
                                              :allowfullscreen true}))))))
; <iframe src="//player.vimeo.com/video/VIDEO_ID"
;  width="WIDTH" height="HEIGHT" frameborder="0" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>

;; video view component
;; ----------------------------------------------------------------------------

(defn set-active! [video state]
  (put! (:video-chan state) video))

(defn is-active? [video state]
  (= video (:current-video state)))

(defn video-view [video owner]
  (reify
    om/IInitState
    (init-state [_]
                {:playing :stop})
    om/IRenderState
    (render-state [_ state]
                  (dom/div #js {:className "video"
                                :onClick #(set-active! (.-value video) state)}
                           (dom/img #js {:src (:thumbnail video)})))))


(defn select-video! [video owner]
  (om/set-state! owner :current-video video))

(defn videolist-view [video-list owner]
  (reify
    om/IInitState
    (init-state [_]
                {:video-chan (chan)
                 :current-video nil})
    om/IWillMount
    (will-mount [_]
                (let [video-chan (om/get-state owner :video-chan)]
                  (go (while true
                        (select-video! (<! video-chan) owner)))))
    om/IRenderState
    (render-state [_ state]
                  (dom/div nil
                           (om/build player-view (:current-video state))
                           (dom/hr nil)
                           (apply dom/div #js {:id "video-list"}
                                  (om/build-all video-view (:film-collection video-list)
                                                {:init-state {:video-chan (:video-chan state)}
                                                  :state {:current-video (:current-video state)}}))))))

;; video page wrapper
;; ----------------------------------------------------------------------------

(extend-type VideoPage
  generic/ICustomPage
  (custom-page
   [this]
   (fn [app-state owner]
     (reify
       om/IInitState
       (init-state [_]
                   {})
       om/IRenderState
       (render-state [_ state]
                     (dom/div #js {:className "sectionWrapper video-page"}
                              (dom/div #js {:className "content"}
                                       (om/build videolist-view (get-in app-state [:app-state :video])))))))))


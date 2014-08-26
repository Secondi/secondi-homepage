(ns secondi.pages.music
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [secondi.pages.generic :as generic]))

;; sound types
;; ----------------------------------------------------------------------------

(defprotocol ITrack
  (prepare-track [this])
  (play-track [this])
  (stop-track [this])
  (current-position [this])
  (set-position [this]))

(defrecord SoundcloudTrack [name soundmanager]
  ITrack
  (prepare-track [this] nil)
  (play-track [this] nil)
  (stop-track [this] nil)
  (current-position [this] nil)
  (set-position [this] nil))

(defn music-track [name _]
  (->MusicTrack name nil))

(defprotocol IPlaylist
  (play-next [this] [this next-track]))

(defrecord Playlist [name album-cover track-collection]
  IPlaylist
  (play-next [this] nil))

(defn playlist [name album-cover track-collection]
  (->Playlist name album-cover track-collection))

;; page extension
;; ----------------------------------------------------------------------------


(defrecord MusicPage [navigation-page]
  generic/IPageNavigation
  (create-slug [this]
               (generic/make-slug (get-in this [:navigation-page :page :name]))))

(defn music-page [name body-description]
  (->MusicPage (generic/navigate-page name body-description)))


;; om component
;; ----------------------------------------------------------------------------

(extend-type MusicPage
  generic/ICustomPage
  (custom-page
   [this]
   (fn [app-state owner]
     (reify
       om/IInitState
       (init-state [_]
                   {:sections []})
       om/IRender
       (render [_]
               (dom/div #js {:className "sectionWrapper general-page"}
                        (dom/div #js {:className "content"} "THIS IS THE MUSIC PAGE")))))))

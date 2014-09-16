(ns secondi.components.bandcamp
  (:require [clojure.string :as string]))


;; sound types
;; ----------------------------------------------------------------------------

(defprotocol ITrack
  (prepare-track [this])
  (play-track [this])
  (stop-track [this])
  (current-position [this])
  (set-position [this]))

(defrecord BandcampTrack [name soundmanager]
  ITrack
  (prepare-track [this] nil)
  (play-track [this] nil)
  (stop-track [this] nil)
  (current-position [this] nil)
  (set-position [this] nil))

(defn music-track [name _]
  (->BandcampTrack name nil))

(defprotocol IPlaylist
  (play-next [this] [this next-track]))

(defrecord Playlist [name album-cover track-collection]
  IPlaylist
  (play-next [this] nil))

(defn playlist [name album-cover track-collection]
  (->Playlist name album-cover track-collection))

;; sound types
;; ----------------------------------------------------------------------------


(def secondi-id "1269523251")
(def dev-key "vatnajokull")
(def api-url "http://api.bandcamp.com/api/")
(def band-url (str api-url "band/3/"))

(defn url-kv [k v]
  (str k "=" v))

(defn url-query [v]
  (string/join "&" (map (fn [item]
                          (url-kv (-> item (get 0) name)
                                  (item 1)))(seq v))))

(defn discography [api-key band-id]
  (str band-url "?" (url-query {:key api-key
                                :band_id band-id})))

(discography dev-key secondi-id)

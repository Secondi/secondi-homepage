(ns secondi.components.bandcamp)


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

(ns build
  (:require
    [clojure.spec.test.alpha :as spec-test]
    [fr.jeremyschoffen.mbt.alpha.core :as mbt-core]
    [fr.jeremyschoffen.mbt.alpha.default :as mbt-defaults]))

(spec-test/instrument)


(def specific-conf (sorted-map
                     :maven/group-id 'fr.jeremyschoffen
                     :versioning/scheme mbt-defaults/simple-scheme))


(def conf (mbt-defaults/make-conf specific-conf))


(comment
  (mbt-defaults/bump-tag! conf)
  (mbt-defaults/build-jar! conf)
  (mbt-defaults/install! conf)
  (mbt-core/clean! conf)

  (-> conf
      (assoc :project/version "0")
      mbt-defaults/build-jar!)

  (-> conf
      (assoc :project/version "0")
      mbt-defaults/install!))
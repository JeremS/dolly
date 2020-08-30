(ns build
  (:require
    [clojure.spec.test.alpha :as spec-test]
    [fr.jeremyschoffen.mbt.alpha.core :as mbt-core]
    [fr.jeremyschoffen.mbt.alpha.default :as mbt-defaults]
    [fr.jeremyschoffen.mbt.alpha.utils :as u]

    [docs.core :as docs]))

(u/pseudo-nss
  maven
  project
  project.license
  versioning)



(spec-test/instrument `[mbt-core/clean!
                        mbt-defaults/versioning-tag-new-version!
                        mbt-defaults/build-jar!
                        mbt-defaults/maven-install!])


(def specific-conf {::maven/group-id 'fr.jeremyschoffen
                    ::versioning/scheme mbt-defaults/git-distance-scheme

                    ::project/licenses [{::project.license/name "Eclipse Public License - v 2.0"
                                         ::project.license/url "https://www.eclipse.org/legal/epl-v20.html"
                                         ::project.license/distribution :repo
                                         ::project.license/file (u/safer-path "LICENSE")}]})


(def conf (mbt-defaults/config-make specific-conf))


(defn make-docs! [conf]
  (-> conf
      (u/assoc-computed ::project/maven-coords mbt-core/deps-make-coord)
      (u/side-effect! docs/make-readme!)))


(defn new-milestone [conf]
  (-> conf
      (u/assoc-computed ::versioning/version mbt-defaults/versioning-next-version
                        ::project/version (comp str ::versioning/version))

      (mbt-defaults/build-before-bump! (u/do-side-effect! make-docs!))
      (u/do-side-effect! mbt-defaults/versioning-tag-new-version!)
      (u/do-side-effect! mbt-defaults/build-jar!)
      (u/do-side-effect! mbt-defaults/maven-install!)))



(comment
  (-> conf
      (u/assoc-computed ::versioning/version mbt-defaults/versioning-next-version
                        ::project/version (comp str ::versioning/version))

      (u/do-side-effect! make-docs!))

  (new-milestone conf)
  (mbt-core/clean! conf)

  (-> conf
      (assoc :project/version "0")
      mbt-defaults/build-jar!)

  (-> conf
      (assoc :project/version "0")
      mbt-defaults/install!))
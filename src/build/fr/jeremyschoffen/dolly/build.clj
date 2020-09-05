(ns fr.jeremyschoffen.dolly.build
  (:require
    [clojure.spec.test.alpha :as spec-test]
    [fr.jeremyschoffen.mbt.alpha.core :as mbt-core]
    [fr.jeremyschoffen.mbt.alpha.default :as mbt-defaults]
    [fr.jeremyschoffen.mbt.alpha.utils :as u]

    [fr.jeremyschoffen.dolly.docs.core :as docs]
    [build]))

(u/pseudo-nss
  git
  maven
  maven.scm
  maven.credentials
  project
  project.license
  versioning)


(def conf (-> {::maven/group-id 'fr.jeremyschoffen
               ::versioning/scheme mbt-defaults/git-distance-scheme

               ::maven/scm {::maven.scm/url "https://github.com/JeremS/dolly"}

               ::maven/server mbt-defaults/clojars

               ::maven/credentials {::maven.credentials/user-name "jeremys"
                                    ::maven.credentials/password build/token}

               ::project/licenses [{::project.license/name "Eclipse Public License - v 2.0"
                                    ::project.license/url "https://www.eclipse.org/legal/epl-v20.html"
                                    ::project.license/distribution :repo
                                    ::project.license/file (u/safer-path "LICENSE")}]}
              mbt-defaults/config))


(defn generate-docs! [conf]
  (-> conf
      (u/assoc-computed ::project/maven-coords mbt-core/deps-make-coord)
      (u/do-side-effect! docs/make-readme!)))

(u/spec-op generate-docs!
           :deps [mbt-core/deps-make-coord]
           :param {:req [::maven/artefact-name
                         ::maven/group-id
                         ::project/version]
                   :opt [::maven/classifier]})


(defn next-version [conf]
  (let [next-v (mbt-defaults/versioning-next-version conf)]
    (if (= next-v (mbt-defaults/versioning-initial-version conf))
      next-v
      (update next-v :number inc))))


(defn new-milestone! [conf]
  (-> conf
      (mbt-defaults/build-before-bump! (u/do-side-effect! generate-docs!))
      (u/do-side-effect! mbt-defaults/versioning-tag-new-version!)))

(u/spec-op new-milestone!
           :deps [generate-docs!
                  mbt-defaults/versioning-tag-new-version!]
           :param {:req [::git/repo
                         ::maven/artefact-name
                         ::maven/group-id
                         ::project/version
                         ::project/working-dir
                         ::versioning/tag-base-name
                         ::versioning/version]
                   :opt [::maven/classifier]})


(spec-test/instrument `[generate-docs!
                        new-milestone!
                        mbt-core/clean!
                        mbt-defaults/versioning-tag-new-version!
                        mbt-defaults/maven-make-github-scm
                        mbt-defaults/build-jar!
                        mbt-defaults/maven-install!])


(defn release! []
  (-> conf
      (u/assoc-computed ::versioning/version next-version
                        ::project/version mbt-defaults/versioning-project-version)
      (u/do-side-effect! new-milestone!)
      (u/assoc-computed ::maven/scm mbt-defaults/maven-make-github-scm)
      (u/do-side-effect! mbt-defaults/build-jar!)
      (u/do-side-effect! mbt-defaults/maven-install!)
      u/record-build))


(defn deploy! []
  (-> conf
      (u/assoc-computed ::versioning/version mbt-defaults/versioning-current-version
                        ::project/version mbt-defaults/versioning-project-version)
      (u/assoc-computed ::maven/scm mbt-defaults/maven-make-github-scm)
      (u/do-side-effect! mbt-defaults/maven-deploy!)))


(comment
  (release!)
  (deploy!)
  (mbt-core/clean! conf))


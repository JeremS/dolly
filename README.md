

# Dolly

Utilities for cloning vars in Clojure and Clojurescript.


## Installation
```clojure
{io.github.jerems/dolly {:git/tag "v27", :git/sha "835e15c649"}}
```

## Usage

```Clojure
(defn foo
  "Some foo doc"
  [])

;; We can clone foo in the same ns or not
(def-clone bar foo)

;; The docstring (and other var metadata) is cloned
(clojure.repl/doc foo)
;=> "Some foo doc"


;; We can clone macros
(defmacro baz
  "Some doc for baz."
  [])

;; And change some metadata for the clone.
(def-clone ^{:doc "Some other doc"}cloned-baz baz)

(clojure.repl/doc cloned-baz)
;=> "Some other doc"
```

## Inspirations
This project is inspired by [https://github.com/aleph-io/potemkin](potemkin's') import var system.
However dolly's `def-clone` doesn't link aliased vars to propagate change when the original is redefined.
I don't think that's possible in Clojurescript and this projects aims to work with it.

## License

Copyright © 2020-2022 Jeremy Schoffen.

Distributed under the Eclipse Public License v 2.0.

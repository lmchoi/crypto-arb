(defproject crypto-arb "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [cheshire "5.8.0"]
                 [http-kit "2.2.0"]
                 [aleph "0.4.4"]
                 ;[com.lorddoig/pusher-clj "0.1.0-alpha2"]
                 ]
  :main ^:skip-aot crypto-arb.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})

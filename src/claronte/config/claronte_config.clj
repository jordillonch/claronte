(ns claronte.config.claronte-config)

(def fetcher-redis-server-connection-parameters {:pool {} :spec {:host "127.0.0.1" :port 6379}})

(def sender-rabbitmq-server-connection-parameters {:host "127.0.0.1" :port 5672 :username "guest" :password "guest" :vhost "/"})
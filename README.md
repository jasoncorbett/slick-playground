Slick Playground
================

It's not perfect, but it explores combining ionic 2 and vertx (and hopefully soon mongo) into an awesome scalable
powerful stack of tech.

How to Run
----------
What you will need:

  * Maven
  * Nodejs with npm
  * typescript compiler (npm install -g typescript)
  * ionic (npm install -g ionic)

Run:

    mvn clean package
    java -jar target/slickqaweb-5.0-SNAPSHOT-fat.jar

In another terminal run:

    cd web
    ionic serve

Then look at localhost:8100.
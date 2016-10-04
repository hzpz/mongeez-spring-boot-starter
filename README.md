![Maven Central](https://img.shields.io/maven-central/v/io.github.hzpz.spring.boot/mongeez-spring-boot-starter.svg)
[![Build Status](https://travis-ci.org/hzpz/mongeez-spring-boot-starter.svg?branch=master)](https://travis-ci.org/hzpz/mongeez-spring-boot-starter)
[![Codacy Badge](https://api.codacy.com/project/badge/grade/e518c6a5031143fda2a6348fb90bfe55)](https://www.codacy.com/app/mailantimo/mongeez-spring-boot-starter)

# Spring Boot Starter for Mongeez
[Mongeez][mongeez] is a change management tool for Mongo databases. 
This project is a [starter][spring-boot-starter] for Spring Boot that can automatically configure Mongeez and run migration scripts.
The auto-configuration makes sure that the migrations happen before any Spring Data Mongo beans are initialized.

## Usage
 * Make sure Spring Data Mongo is configured properly. 
 * Add dependency to the Mongeez Spring Boot starter:

<!-- -->
    <dependency>
        <groupId>io.github.hzpz.spring.boot</groupId>
        <artifactId>mongeez-spring-boot-starter</artifactId>
        <version>1.3.0</version>
    </dependency>

 * [Create a mongeez.xml file that includes all change logs][mongeez.xml] at `db/mongeez.xml`.
 
## Configuration
Mongeez can be configured by setting properties with the prefix `mongeez` or by using the `MongeezProperties` bean directly.

### Disable auto-configuration
To disable the auto-configuration of Mongeez completely, set the property `mongeez.enabled` to `false`.

### Location of migration script
The default expected location of the migration script (see [Create a mongeez.xml file that includes all change logs][mongeez.xml])
is `db/mongeez.xml`. The simplest way to override the location is by setting the property `mongeez.location`.

### Database
By default, the auto-configuration will configure Mongeez to use the same database that is configured for Spring Data Mongo
(either by setting the property `spring.data.mongodb.database` or by using the `MongoProperties` bean directly). Override 
the database by setting the property `mongeez.database`.

### Authentication
If the database requires authentication you need to set username and password twice, for Spring Data Mongo and for Mongeez
(either by setting the properties `mongeez.username` and `mongeez.password` or by using the `MongeezProperties` bean directly).
This is because Spring Data Mongo clears the password from `MongoProperties` after using it.

[mongeez]: https://github.com/mongeez/mongeez
[mongeez.xml]: https://github.com/mongeez/mongeez/wiki/How-to-use-mongeez#create-a-mongeezxml-file-that-include-all-change-logs
[spring-boot-starter]: http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#using-boot-starter-poms

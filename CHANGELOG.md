# Version 3.0.0 (2017-11-30)

* [new] Implements automatic translation of business specifications to Morphia queries. 

# Version 2.0.0 (2017-01-13)

* [brk] Update to new configuration system.

# Version 1.1.0 (2016-04-26)

* [new] Automatically build indexes
* [new] Add Bean Validation support to Morphia (at pre-persist) when SeedStack validation add-on is present in the classpath.
* [new] Add `exists()` and `count()` in `BaseMorphiaRepository` according to change in business framework.
* [chg] Update for SeedStack 16.4.
* [brk] Remove `do*()` methods in `BaseMorphiaRepository` according to change in business framework.
* [fix] Correctly take MongoDb database aliases into account when injecting Morphia data stores.

# Version 1.0.1 (2016-02-09)

* [fix] Flawed release process made this add-on unusable by clients.

# Version 1.0.0 (2015-07-30)

* [new] Initial Open-Source release.

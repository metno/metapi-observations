Observations access service for met-api
=======================================

This implements a service for reading observations data, currently
from a kdvh database.


Setup
-----

This application only almost works out of the box. To make this work
for testing, you must create a file, conf/development.conf. This file
must at least contain the following keys:

  * db.kdvh.url
  * db.kdvh.user
  * db.kdvh.password

See https://dokit.met.no/prosjekter/bora/kdvh-test for details on how
to set these options.

You can find a template file under conf, to make the creation a bit
easier.

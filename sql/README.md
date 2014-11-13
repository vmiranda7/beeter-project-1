Beeter - SQL scripts
This folder contains the scripts to create the user associated to the beeter database (user: beeter, default password beeter) and the schema of the database. It also contains script to configure the Tomcat realm.

Installation

Connect as root to mysql, execute script beeterdb-user.sql and realmdb-user.sql, then exit.
Connect as realm (password: realm) to mysql, execute script beeterdb-schema.sql, then exit.
Connect as beeter (password: beeter) to mysql, execute script beeterdb-schema.sql, then exit.
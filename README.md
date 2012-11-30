XenForge
========

Plugin for Minecraft Forge to integrate forums (not just XenForo) with a Forge server.

This plugin is intended only for Forge servers, if you run this on a client, it's
doubtful it will work as expected.

Installation:

 1. Drop the XenForge jar into the mods/ folder.

 2. Install your favourite JDBC driver into the lib/ folder.

 3. Run your Forge server, then shut it down, to generate the configuration.

 4. Edit config/XenForge.cfg, and modify the following:-

    Set this to the driver name for your driver:-

        S:driver=com.mysql.jdbc.Driver

    This is the filename of the driver jar:-

        S:driverfile=mysql-connector-java-5.1.22-bin.jar
 
    Name of your database:

        S:database=xenforo_db

    Host your database is on:

        S:host=localhost

    This part should be obvious:

        S:username=xenforodb
        S:password=password
        S:port=3306
        S:type=mysql
    }

 5. You can customise the SQL to make it work with other databases.

    You probably need to change the user_group_id's used in the groupquery. They
    work for me, but your groups may be different.

    if you want to know what groups you have and what their IDs are, use your sql
    client and look at xf_user_group:

mysql> select * from xf_user_group;
+---------------+----------------------------+------------------------+--------------+----------------------+
| user_group_id | title                      | display_style_priority | username_css | user_title           |
+---------------+----------------------------+------------------------+--------------+----------------------+
|             1 | Unregistered / Unconfirmed |                      0 |              | Guest                |
|             2 | Player                     |                      0 |              | Player               |
|             3 | Admin                      |                   1000 |              | Administrator        |
|             4 | Mod                        |                    900 |              | Moderator            |
+---------------+----------------------------+------------------------+--------------+----------------------+

 6. You can customise the messages that users received if they are rejected.

 7. run your server and test. :)

Questions? forum post:


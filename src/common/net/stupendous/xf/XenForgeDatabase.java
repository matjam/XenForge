package net.stupendous.xf;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * User: chrome
 * Date: 7/10/12
 * Time: 11:38 AM
 */
public class XenForgeDatabase {
    XenForgeLogger log = null;
    XenForgePlugin plugin = null;
    Connection dbc = null;

    PreparedStatement getForumIdForMinecraftName = null;
    PreparedStatement getForumRanksForForumID = null;
    PreparedStatement getIsBannedForForumID = null;

    XenForgeDatabase(XenForgePlugin plugin) {
    	this.plugin = plugin; 
        this.log = XenForgePlugin.getLogger();
    }

    public boolean connect() {
        String driver = plugin.getConfig().get("db", "driver", "com.mysql.jdbc.Driver").value;
        String driverFile = plugin.getConfig().get("db", "driverfile", "mysql-connector-java-5.1.22-bin.jar").value;
        String type = plugin.getConfig().get("db", "type", "mysql").value;
        String host = plugin.getConfig().get("db", "host", "localhost").value;
        String port = plugin.getConfig().get("db", "port", "3306").value;
        String database = plugin.getConfig().get("db", "database", "xenforo_db").value;
        String username = plugin.getConfig().get("db", "username", "xenforo").value;
        String password = plugin.getConfig().get("db", "password", "xenforo").value;

        String testquery = plugin.getConfig().get("sql", "testquery", "select version_id from xf_upgrade_log order by completion_date desc limit 1").value;
        String userquery = plugin.getConfig().get("sql", "userquery", "select user_id from xf_user_field_value where field_id='minecraft' and field_value=?").value;
        String groupquery = plugin.getConfig().get("sql", "groupquery", "select user_group_id from xf_user_group_relation where user_id=? and (user_group_id=3 or user_group_id=5)").value;
        String banquery = plugin.getConfig().get("sql", "banquery", "select is_banned from xf_user where user_id=?").value;
        
        String connectionString = String.format("jdbc:%s://%s:%s/%s", type, host, port, database);

        log.info("Connecting to database: %s", connectionString);

        try {
        	String path = "jar:file:" + System.getProperty("user.dir") + "/lib/" + driverFile + "!/";
        	
    		URL u = new URL(path);
    		URLClassLoader ucl = new URLClassLoader(new URL[] { u });
    		Driver d = (Driver)Class.forName(driver, true, ucl).newInstance();
    		DriverManager.registerDriver(new XenForgeDriverShim(d));
        } catch (ClassNotFoundException e) {
            log.severe("Unable to load database driver (%s): ClassNotFoundException", driver);
            return false;
        } catch (MalformedURLException e) {
            log.severe("Unable to load database driver (%s): MalformedURLException", driver);
            return false;
		} catch (InstantiationException e) {
            log.severe("Unable to load database driver (%s): InstantiationException", driver);
            return false;
		} catch (IllegalAccessException e) {
            log.severe("Unable to load database driver (%s): IllegalAccessException", driver);
            return false;
		} catch (SQLException e) {
            log.severe("Unable to load database driver (%s): SQLException", driver);
            return false;
		}

        try {
            dbc = DriverManager.getConnection(connectionString, username, password);
        } catch (SQLException e) {
            log.severe("Unable to connect to database (%s): %s", connectionString, e.getMessage());
            return false;
        }

        if (dbc == null) {
            log.severe("Unknown error occurred; unable to connect to database.");
            return false;
        }

        try {
            // Test the database so we can be sure that we can query it.

        	Statement statement = dbc.createStatement();
            ResultSet result = statement.executeQuery(testquery);

            if (!result.next()) {
                log.severe("Is xenforo installed? No entries found in xf_upgrade_log table.");
            }

            int version_id = result.getInt(1);

            log.info("XenForo version %d found.", version_id);

            // Prepare all of our SQL

            getForumIdForMinecraftName =
                    dbc.prepareStatement(userquery);

            getForumRanksForForumID =
                    dbc.prepareStatement(groupquery);

            getIsBannedForForumID =
                    dbc.prepareStatement(banquery);

        } catch (SQLException e) {
            log.severe("SQL Error: %s", e.getMessage());
            return false;
        } catch (NullPointerException e) {
            log.severe("SQL Error: %s", e.getMessage());
        }

        return true;
    }

    public void close() {
        try {
            getForumIdForMinecraftName.close();
            getForumRanksForForumID.close();
            dbc.close();
        } catch (SQLException e) {
            log.severe("SQL Error: %s", e.getMessage());
        }

        log.info("Disconnected from database.");
    }

    public int getForumId(String minecraftName) {
        try {
            if (!dbc.isValid(1)) {
                connect();
            }
        } catch (SQLException e) {
            log.info("Database Connection invalid; reconnecting.");
        }

        try {
            getForumIdForMinecraftName.setString(1, minecraftName);

            ResultSet result = getForumIdForMinecraftName.executeQuery();

            if (!result.next()) {
                log.info("UserID for %s not found.", minecraftName);
                result.close();
                return -1;
            }

            int userID = result.getInt(1);
            result.close();

            return userID;
        } catch (SQLException e) {
            log.severe("Unable to perform SQL Query: %s", e.getMessage());
            return -1;
        }
    }

    public boolean isAllowed(int forumId) {
        try {
            if (!dbc.isValid(1)) {
                connect();
            }
        } catch (SQLException e) {
            log.info("Database Connection invalid; reconnecting.");
        }

        try {
            getForumRanksForForumID.setInt(1, forumId);

            ResultSet result = getForumRanksForForumID.executeQuery();

            if (!result.next()) {
                log.info("UserID %d not in allowed group.", forumId);
            }
            while (result.next()) {
                int rank = result.getInt(1);
                result.close();
                return false;
            }
            result.close();

            return true;
        } catch (SQLException e) {
            log.severe("Unable to perform SQL Query: %s", e.getMessage());
            return false;
        }
    }

    public boolean isBanned(int forumID) {
        try {
            if (!dbc.isValid(1)) {
                connect();
            }
        } catch (SQLException e) {
            log.info("Database Connection invalid; reconnecting.");
        }

        try {
            getIsBannedForForumID.setInt(1, forumID);

            ResultSet result = getIsBannedForForumID.executeQuery();

            if (!result.next()) {
                log.severe("SQL Error: user id %d does not exist.", forumID);
                return false;
            }

            boolean is_banned = result.getBoolean("is_banned");
            result.close();
            return is_banned;
        } catch (SQLException e) {
            log.severe("Unable to perform SQL Query: %s", e.getMessage());
            return false;
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.upgrade;

import java.io.File;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.Statement;

public class HSQL17To18PreUpgradeCleaner {
    public static void main(String[] args) {
        String DRIVER_NAME = "org.hsqldb.jdbcDriver";
        try {
            File dbProperties = new File("./database/confluencedb.properties");
            if (!dbProperties.exists()) {
                System.out.println("Could not find ./database/confluencedb.properties. This program must be run from your confluence.home directory.");
                System.exit(1);
            }
            Class<?> driverClass = Class.forName(DRIVER_NAME);
            DriverManager.registerDriver((Driver)driverClass.newInstance());
            Connection c = DriverManager.getConnection("jdbc:hsqldb:./database/confluencedb;hsqldb.tx=MVCC", "sa", "");
            Statement s = c.createStatement();
            s.execute("SHUTDOWN");
            s.close();
            c.close();
            System.out.println("Cleaning complete.");
        }
        catch (ClassNotFoundException cnfe) {
            throw new RuntimeException("Driver class not found", cnfe);
        }
        catch (Exception e) {
            throw new RuntimeException("Error", e);
        }
    }
}


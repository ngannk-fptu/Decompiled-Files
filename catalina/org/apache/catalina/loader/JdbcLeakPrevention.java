/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.loader;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;

public class JdbcLeakPrevention {
    public List<String> clearJdbcDriverRegistrations() throws SQLException {
        ArrayList<String> driverNames = new ArrayList<String>();
        HashSet<Driver> originalDrivers = new HashSet<Driver>();
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            originalDrivers.add(drivers.nextElement());
        }
        drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            if (driver.getClass().getClassLoader() != this.getClass().getClassLoader()) continue;
            if (originalDrivers.contains(driver)) {
                driverNames.add(driver.getClass().getCanonicalName());
            }
            DriverManager.deregisterDriver(driver);
        }
        return driverNames;
    }
}


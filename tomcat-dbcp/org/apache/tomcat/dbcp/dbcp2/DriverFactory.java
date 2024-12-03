/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

final class DriverFactory {
    DriverFactory() {
    }

    static Driver createDriver(BasicDataSource basicDataSource) throws SQLException {
        Driver driverToUse = basicDataSource.getDriver();
        String driverClassName = basicDataSource.getDriverClassName();
        ClassLoader driverClassLoader = basicDataSource.getDriverClassLoader();
        String url = basicDataSource.getUrl();
        if (driverToUse == null) {
            Class<?> driverFromCCL;
            block12: {
                driverFromCCL = null;
                if (driverClassName != null) {
                    try {
                        try {
                            if (driverClassLoader == null) {
                                driverFromCCL = Class.forName(driverClassName);
                                break block12;
                            }
                            driverFromCCL = Class.forName(driverClassName, true, driverClassLoader);
                        }
                        catch (ClassNotFoundException cnfe) {
                            driverFromCCL = Thread.currentThread().getContextClassLoader().loadClass(driverClassName);
                        }
                    }
                    catch (Exception t) {
                        String message = "Cannot load JDBC driver class '" + driverClassName + "'";
                        basicDataSource.log(message, t);
                        throw new SQLException(message, t);
                    }
                }
            }
            try {
                if (driverFromCCL == null) {
                    driverToUse = DriverManager.getDriver(url);
                } else {
                    driverToUse = (Driver)driverFromCCL.getConstructor(new Class[0]).newInstance(new Object[0]);
                    if (!driverToUse.acceptsURL(url)) {
                        throw new SQLException("No suitable driver", "08001");
                    }
                }
            }
            catch (Exception t) {
                String message = "Cannot create JDBC driver of class '" + (driverClassName != null ? driverClassName : "") + "' for connect URL '" + url + "'";
                basicDataSource.log(message, t);
                throw new SQLException(message, t);
            }
        }
        return driverToUse;
    }
}


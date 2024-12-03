/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.c3p0;

import com.mchange.v2.c3p0.DriverManagerDataSource;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;

public final class DriverManagerDataSourceFactory {
    public static DataSource create(String driverClass, String jdbcUrl, String dfltUser, String dfltPassword, String refFactoryLoc) throws SQLException {
        DriverManagerDataSource out = new DriverManagerDataSource();
        out.setDriverClass(driverClass);
        out.setJdbcUrl(jdbcUrl);
        out.setUser(dfltUser);
        out.setPassword(dfltPassword);
        out.setFactoryClassLocation(refFactoryLoc);
        return out;
    }

    public static DataSource create(String driverClass, String jdbcUrl, Properties props, String refFactoryLoc) throws SQLException {
        DriverManagerDataSource out = new DriverManagerDataSource();
        out.setDriverClass(driverClass);
        out.setJdbcUrl(jdbcUrl);
        out.setProperties(props);
        out.setFactoryClassLocation(refFactoryLoc);
        return out;
    }

    public static DataSource create(String driverClass, String jdbcUrl, String dfltUser, String dfltPassword) throws SQLException {
        return DriverManagerDataSourceFactory.create(driverClass, jdbcUrl, dfltUser, dfltPassword, null);
    }

    public static DataSource create(String driverClass, String jdbcUrl) throws SQLException {
        return DriverManagerDataSourceFactory.create(driverClass, jdbcUrl, (String)null, null);
    }

    public static DataSource create(String jdbcUrl, String dfltUser, String dfltPassword) throws SQLException {
        return DriverManagerDataSourceFactory.create(null, jdbcUrl, dfltUser, dfltPassword);
    }

    public static DataSource create(String jdbcUrl) throws SQLException {
        return DriverManagerDataSourceFactory.create(null, jdbcUrl, (String)null, null);
    }

    private DriverManagerDataSourceFactory() {
    }
}


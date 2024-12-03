/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.tomcat.dbcp.dbcp2.ConnectionFactory;
import org.apache.tomcat.dbcp.dbcp2.Utils;

public class DriverConnectionFactory
implements ConnectionFactory {
    private final String connectionString;
    private final Driver driver;
    private final Properties properties;

    public DriverConnectionFactory(Driver driver, String connectString, Properties properties) {
        this.driver = driver;
        this.connectionString = connectString;
        this.properties = properties;
    }

    @Override
    public Connection createConnection() throws SQLException {
        return this.driver.connect(this.connectionString, this.properties);
    }

    public String getConnectionString() {
        return this.connectionString;
    }

    public Driver getDriver() {
        return this.driver;
    }

    public Properties getProperties() {
        return this.properties;
    }

    public String toString() {
        return this.getClass().getName() + " [" + this.driver + ";" + this.connectionString + ";" + Utils.cloneWithoutCredentials(this.properties) + "]";
    }
}


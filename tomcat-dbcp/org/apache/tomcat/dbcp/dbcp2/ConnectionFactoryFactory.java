/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.apache.tomcat.dbcp.dbcp2.ConnectionFactory;
import org.apache.tomcat.dbcp.dbcp2.DriverConnectionFactory;

final class ConnectionFactoryFactory {
    ConnectionFactoryFactory() {
    }

    static ConnectionFactory createConnectionFactory(BasicDataSource basicDataSource, Driver driver) throws SQLException {
        Properties connectionProperties = basicDataSource.getConnectionProperties();
        String url = basicDataSource.getUrl();
        String user = basicDataSource.getUsername();
        if (user != null) {
            connectionProperties.put("user", user);
        } else {
            basicDataSource.log(String.format("DBCP DataSource configured without a '%s'", "user"));
        }
        String pwd = basicDataSource.getPassword();
        if (pwd != null) {
            connectionProperties.put("password", pwd);
        } else {
            basicDataSource.log(String.format("DBCP DataSource configured without a '%s'", "password"));
        }
        String connectionFactoryClassName = basicDataSource.getConnectionFactoryClassName();
        if (connectionFactoryClassName != null) {
            try {
                Class<?> connectionFactoryFromCCL = Class.forName(connectionFactoryClassName);
                return (ConnectionFactory)connectionFactoryFromCCL.getConstructor(Driver.class, String.class, Properties.class).newInstance(driver, url, connectionProperties);
            }
            catch (Exception t) {
                String message = "Cannot load ConnectionFactory implementation '" + connectionFactoryClassName + "'";
                basicDataSource.log(message, t);
                throw new SQLException(message, t);
            }
        }
        return new DriverConnectionFactory(driver, url, connectionProperties);
    }
}


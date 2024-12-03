/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.jdbc.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.springframework.jdbc.datasource.AbstractDriverBasedDataSource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class DriverManagerDataSource
extends AbstractDriverBasedDataSource {
    public DriverManagerDataSource() {
    }

    public DriverManagerDataSource(String url) {
        this.setUrl(url);
    }

    public DriverManagerDataSource(String url, String username, String password) {
        this.setUrl(url);
        this.setUsername(username);
        this.setPassword(password);
    }

    public DriverManagerDataSource(String url, Properties conProps) {
        this.setUrl(url);
        this.setConnectionProperties(conProps);
    }

    public void setDriverClassName(String driverClassName) {
        Assert.hasText((String)driverClassName, (String)"Property 'driverClassName' must not be empty");
        String driverClassNameToUse = driverClassName.trim();
        try {
            Class.forName(driverClassNameToUse, true, ClassUtils.getDefaultClassLoader());
        }
        catch (ClassNotFoundException ex) {
            throw new IllegalStateException("Could not load JDBC driver class [" + driverClassNameToUse + "]", ex);
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Loaded JDBC driver: " + driverClassNameToUse));
        }
    }

    @Override
    protected Connection getConnectionFromDriver(Properties props) throws SQLException {
        String url = this.getUrl();
        Assert.state((url != null ? 1 : 0) != 0, (String)"'url' not set");
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Creating new JDBC DriverManager Connection to [" + url + "]"));
        }
        return this.getConnectionFromDriverManager(url, props);
    }

    protected Connection getConnectionFromDriverManager(String url, Properties props) throws SQLException {
        return DriverManager.getConnection(url, props);
    }
}


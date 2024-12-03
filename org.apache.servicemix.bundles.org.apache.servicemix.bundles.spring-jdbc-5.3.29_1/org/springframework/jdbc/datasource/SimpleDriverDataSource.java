/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeanUtils
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.jdbc.datasource;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.datasource.AbstractDriverBasedDataSource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class SimpleDriverDataSource
extends AbstractDriverBasedDataSource {
    @Nullable
    private Driver driver;

    public SimpleDriverDataSource() {
    }

    public SimpleDriverDataSource(Driver driver, String url) {
        this.setDriver(driver);
        this.setUrl(url);
    }

    public SimpleDriverDataSource(Driver driver, String url, String username, String password) {
        this.setDriver(driver);
        this.setUrl(url);
        this.setUsername(username);
        this.setPassword(password);
    }

    public SimpleDriverDataSource(Driver driver, String url, Properties conProps) {
        this.setDriver(driver);
        this.setUrl(url);
        this.setConnectionProperties(conProps);
    }

    public void setDriverClass(Class<? extends Driver> driverClass) {
        this.driver = (Driver)BeanUtils.instantiateClass(driverClass);
    }

    public void setDriver(@Nullable Driver driver) {
        this.driver = driver;
    }

    @Nullable
    public Driver getDriver() {
        return this.driver;
    }

    @Override
    protected Connection getConnectionFromDriver(Properties props) throws SQLException {
        Driver driver = this.getDriver();
        String url = this.getUrl();
        Assert.notNull((Object)driver, (String)"Driver must not be null");
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Creating new JDBC Driver Connection to [" + url + "]"));
        }
        return driver.connect(url, props);
    }
}


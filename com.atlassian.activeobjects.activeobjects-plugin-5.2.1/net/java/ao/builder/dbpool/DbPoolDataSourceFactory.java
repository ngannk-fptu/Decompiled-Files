/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  snaq.db.DBPoolDataSource
 */
package net.java.ao.builder.dbpool;

import java.sql.Driver;
import javax.sql.DataSource;
import net.java.ao.Disposable;
import net.java.ao.DisposableDataSource;
import net.java.ao.builder.ClassUtils;
import net.java.ao.builder.DataSourceFactory;
import net.java.ao.builder.DelegatingDisposableDataSourceHandler;
import snaq.db.DBPoolDataSource;

public final class DbPoolDataSourceFactory
implements DataSourceFactory {
    @Override
    public DisposableDataSource getDataSource(Class<? extends Driver> driverClass, String url, String username, String password) {
        final DBPoolDataSource ds = new DBPoolDataSource();
        ds.setName("active-objects");
        ds.setDriverClassName(driverClass.getName());
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setPoolSize(5);
        ds.setMaxSize(30);
        ds.setExpiryTime(3600);
        return DelegatingDisposableDataSourceHandler.newInstance((DataSource)ds, new Disposable(){

            @Override
            public void dispose() {
                ds.releaseConnectionPool();
            }
        });
    }

    public static boolean isAvailable() {
        return ClassUtils.loadClass("snaq.db.ConnectionPool") != null;
    }
}


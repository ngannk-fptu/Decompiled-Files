/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.dbcp.BasicDataSource
 */
package net.java.ao.builder.dbcp;

import java.sql.Driver;
import java.sql.SQLException;
import javax.sql.DataSource;
import net.java.ao.Disposable;
import net.java.ao.DisposableDataSource;
import net.java.ao.builder.ClassUtils;
import net.java.ao.builder.DataSourceFactory;
import net.java.ao.builder.DelegatingDisposableDataSourceHandler;
import org.apache.commons.dbcp.BasicDataSource;

public final class DbcpDataSourceFactory
implements DataSourceFactory {
    @Override
    public DisposableDataSource getDataSource(Class<? extends Driver> driverClass, String url, String username, String password) {
        final BasicDataSource dbcp = new BasicDataSource();
        dbcp.setUrl(url);
        dbcp.setUsername(username);
        dbcp.setPassword(password);
        return DelegatingDisposableDataSourceHandler.newInstance((DataSource)dbcp, new Disposable(){

            @Override
            public void dispose() {
                try {
                    dbcp.close();
                }
                catch (SQLException sQLException) {
                    // empty catch block
                }
            }
        });
    }

    public static boolean isAvailable() {
        return ClassUtils.loadClass("org.apache.commons.dbcp.BasicDataSource") != null;
    }
}


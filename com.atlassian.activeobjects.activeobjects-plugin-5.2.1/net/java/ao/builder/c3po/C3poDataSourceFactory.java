/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v2.c3p0.ComboPooledDataSource
 *  com.mchange.v2.c3p0.DataSources
 */
package net.java.ao.builder.c3po;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;
import java.beans.PropertyVetoException;
import java.sql.Driver;
import java.sql.SQLException;
import javax.sql.DataSource;
import net.java.ao.ActiveObjectsException;
import net.java.ao.Disposable;
import net.java.ao.DisposableDataSource;
import net.java.ao.builder.ClassUtils;
import net.java.ao.builder.DataSourceFactory;
import net.java.ao.builder.DelegatingDisposableDataSourceHandler;

public class C3poDataSourceFactory
implements DataSourceFactory {
    @Override
    public DisposableDataSource getDataSource(Class<? extends Driver> driverClass, String url, String username, String password) {
        final ComboPooledDataSource cpds = new ComboPooledDataSource();
        try {
            cpds.setDriverClass(driverClass.getName());
        }
        catch (PropertyVetoException e) {
            throw new ActiveObjectsException(e);
        }
        cpds.setJdbcUrl(url);
        cpds.setUser(username);
        cpds.setPassword(password);
        return DelegatingDisposableDataSourceHandler.newInstance((DataSource)cpds, new Disposable(){

            @Override
            public void dispose() {
                try {
                    DataSources.destroy((DataSource)cpds);
                }
                catch (SQLException sQLException) {
                    // empty catch block
                }
            }
        });
    }

    public static boolean isAvailable() {
        return ClassUtils.loadClass("com.mchange.v2.c3p0.ComboPooledDataSource") != null;
    }
}


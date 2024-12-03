/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.logicalcobwebs.proxool.ProxoolDataSource
 *  org.logicalcobwebs.proxool.ProxoolException
 *  org.logicalcobwebs.proxool.ProxoolFacade
 */
package net.java.ao.builder.proxool;

import java.sql.Driver;
import javax.sql.DataSource;
import net.java.ao.Disposable;
import net.java.ao.DisposableDataSource;
import net.java.ao.builder.ClassUtils;
import net.java.ao.builder.DataSourceFactory;
import net.java.ao.builder.DelegatingDisposableDataSourceHandler;
import org.logicalcobwebs.proxool.ProxoolDataSource;
import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.ProxoolFacade;

public class ProxoolDataSourceFactory
implements DataSourceFactory {
    private static final String ALIAS = "active-objects";

    @Override
    public DisposableDataSource getDataSource(Class<? extends Driver> driverClass, String url, String username, String password) {
        ProxoolDataSource source = new ProxoolDataSource(ALIAS);
        source.setUser(username);
        source.setPassword(password);
        source.setDriver(driverClass.getName());
        source.setDriverUrl(url);
        source.setMaximumConnectionCount(30);
        return DelegatingDisposableDataSourceHandler.newInstance((DataSource)source, new Disposable(){

            @Override
            public void dispose() {
                try {
                    ProxoolFacade.removeConnectionPool((String)ProxoolDataSourceFactory.ALIAS);
                }
                catch (ProxoolException proxoolException) {
                    // empty catch block
                }
            }
        });
    }

    public static boolean isAvailable() {
        return ClassUtils.loadClass("org.logicalcobwebs.proxool.ProxoolDriver") != null;
    }
}


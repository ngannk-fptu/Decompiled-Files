/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.builder;

import java.lang.reflect.InvocationTargetException;
import java.sql.Driver;
import java.util.Objects;
import net.java.ao.ActiveObjectsException;
import net.java.ao.Disposable;
import net.java.ao.DisposableDataSource;
import net.java.ao.builder.DataSourceFactory;
import net.java.ao.builder.DelegatingDisposableDataSourceHandler;
import net.java.ao.builder.DriverManagerDataSource;
import net.java.ao.builder.c3po.C3poDataSourceFactory;
import net.java.ao.builder.dbcp.DbcpDataSourceFactory;
import net.java.ao.builder.dbpool.DbPoolDataSourceFactory;
import net.java.ao.builder.proxool.ProxoolDataSourceFactory;

public enum ConnectionPool implements DataSourceFactory
{
    C3PO(C3poDataSourceFactory.class),
    DBPOOL(DbPoolDataSourceFactory.class),
    PROXOOL(ProxoolDataSourceFactory.class),
    DBCP(DbcpDataSourceFactory.class),
    NONE(null){

        @Override
        public boolean isAvailable() {
            return true;
        }

        @Override
        public DisposableDataSource getDataSource(Class<? extends Driver> driverClass, String url, String username, String password) {
            return DelegatingDisposableDataSourceHandler.newInstance(new DriverManagerDataSource(url, username, password), new Disposable(){

                @Override
                public void dispose() {
                }
            });
        }
    };

    private final Class<? extends DataSourceFactory> dataSourceFactoryClass;

    private ConnectionPool(Class<? extends DataSourceFactory> dataSourceFactoryClass) {
        this.dataSourceFactoryClass = dataSourceFactoryClass;
    }

    @Override
    public DisposableDataSource getDataSource(Class<? extends Driver> driverClass, String url, String username, String password) {
        Objects.requireNonNull(this.dataSourceFactoryClass, "dataSourceFactoryClass can't be null");
        try {
            return this.dataSourceFactoryClass.newInstance().getDataSource(driverClass, url, username, password);
        }
        catch (IllegalAccessException | InstantiationException e) {
            throw new ActiveObjectsException("Could not create an instance of <" + this.dataSourceFactoryClass + ">, have you called isAvailable before hand?", e);
        }
    }

    public boolean isAvailable() {
        Objects.requireNonNull(this.dataSourceFactoryClass, "dataSourceFactoryClass can't be null");
        try {
            return (Boolean)this.dataSourceFactoryClass.getMethod("isAvailable", new Class[0]).invoke(null, new Object[0]);
        }
        catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            return false;
        }
    }
}


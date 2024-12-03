/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleActivator
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceRegistration
 *  org.osgi.service.jdbc.DataSourceFactory
 */
package com.microsoft.sqlserver.jdbc.osgi;

import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import com.microsoft.sqlserver.jdbc.osgi.SQLServerDataSourceFactory;
import java.util.Dictionary;
import java.util.Hashtable;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.jdbc.DataSourceFactory;

public class Activator
implements BundleActivator {
    private ServiceRegistration<DataSourceFactory> service;

    public void start(BundleContext context) throws Exception {
        Hashtable<String, Object> properties = new Hashtable<String, Object>();
        SQLServerDriver driver = new SQLServerDriver();
        ((Dictionary)properties).put("osgi.jdbc.driver.class", driver.getClass().getName());
        ((Dictionary)properties).put("osgi.jdbc.driver.name", "Microsoft JDBC Driver for SQL Server");
        ((Dictionary)properties).put("osgi.jdbc.driver.version", driver.getMajorVersion() + "." + driver.getMinorVersion());
        ((Dictionary)properties).put("osgi.jdbc.datasourcefactory.capability", new String[]{"driver", "datasource", "connectionpooldatasource", "xadatasource"});
        this.service = context.registerService(DataSourceFactory.class, (Object)new SQLServerDataSourceFactory(), properties);
        SQLServerDriver.register();
    }

    public void stop(BundleContext context) throws Exception {
        if (this.service != null) {
            this.service.unregister();
        }
        SQLServerDriver.deregister();
    }
}


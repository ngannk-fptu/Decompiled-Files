/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.tomcat.jdbc.pool;

import java.util.Hashtable;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.sql.ConnectionPoolDataSource;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jdbc.pool.DataSourceProxy;
import org.apache.tomcat.jdbc.pool.PoolConfiguration;
import org.apache.tomcat.jdbc.pool.jmx.ConnectionPoolMBean;
import org.apache.tomcat.jdbc.pool.jmx.JmxUtil;

public class DataSource
extends DataSourceProxy
implements javax.sql.DataSource,
MBeanRegistration,
ConnectionPoolMBean,
ConnectionPoolDataSource {
    private static final Log log = LogFactory.getLog(DataSource.class);
    protected volatile ObjectName oname = null;

    public DataSource() {
    }

    public DataSource(PoolConfiguration poolProperties) {
        super(poolProperties);
    }

    @Override
    public void postDeregister() {
        if (this.oname != null) {
            this.unregisterJmx();
        }
    }

    @Override
    public void postRegister(Boolean registrationDone) {
    }

    @Override
    public void preDeregister() throws Exception {
    }

    @Override
    public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception {
        try {
            if (this.isJmxEnabled()) {
                this.oname = this.createObjectName(name);
                if (this.oname != null) {
                    this.registerJmx();
                }
            }
        }
        catch (MalformedObjectNameException x) {
            log.error((Object)"Unable to create object name for JDBC pool.", (Throwable)x);
        }
        return name;
    }

    public ObjectName createObjectName(ObjectName original) throws MalformedObjectNameException {
        String domain = "tomcat.jdbc";
        Hashtable<String, String> properties = original.getKeyPropertyList();
        String origDomain = original.getDomain();
        properties.put("type", "ConnectionPool");
        properties.put("class", this.getClass().getName());
        if (original.getKeyProperty("path") != null || properties.get("context") != null) {
            properties.put("engine", origDomain);
        }
        ObjectName name = new ObjectName(domain, properties);
        return name;
    }

    protected void registerJmx() {
        if (this.pool.getJmxPool() != null) {
            JmxUtil.registerJmx(this.oname, null, this.pool.getJmxPool());
        }
    }

    protected void unregisterJmx() {
        JmxUtil.unregisterJmx(this.oname);
    }
}


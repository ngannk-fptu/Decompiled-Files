/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.modeler.Registry
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.util;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.catalina.JmxEnabled;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.util.LifecycleBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;

public abstract class LifecycleMBeanBase
extends LifecycleBase
implements JmxEnabled {
    private static final Log log = LogFactory.getLog(LifecycleMBeanBase.class);
    private static final StringManager sm = StringManager.getManager((String)"org.apache.catalina.util");
    private String domain = null;
    private ObjectName oname = null;
    @Deprecated
    protected MBeanServer mserver = null;

    @Override
    protected void initInternal() throws LifecycleException {
        if (this.oname == null) {
            this.mserver = Registry.getRegistry(null, null).getMBeanServer();
            this.oname = this.register(this, this.getObjectNameKeyProperties());
        }
    }

    @Override
    protected void destroyInternal() throws LifecycleException {
        this.unregister(this.oname);
    }

    @Override
    public final void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public final String getDomain() {
        if (this.domain == null) {
            this.domain = this.getDomainInternal();
        }
        if (this.domain == null) {
            this.domain = "Catalina";
        }
        return this.domain;
    }

    protected abstract String getDomainInternal();

    @Override
    public final ObjectName getObjectName() {
        return this.oname;
    }

    protected abstract String getObjectNameKeyProperties();

    protected final ObjectName register(Object obj, String objectNameKeyProperties) {
        StringBuilder name = new StringBuilder(this.getDomain());
        name.append(':');
        name.append(objectNameKeyProperties);
        ObjectName on = null;
        try {
            on = new ObjectName(name.toString());
            Registry.getRegistry(null, null).registerComponent(obj, on, null);
        }
        catch (Exception e) {
            log.warn((Object)sm.getString("lifecycleMBeanBase.registerFail", new Object[]{obj, name}), (Throwable)e);
        }
        return on;
    }

    protected final void unregister(String objectNameKeyProperties) {
        StringBuilder name = new StringBuilder(this.getDomain());
        name.append(':');
        name.append(objectNameKeyProperties);
        Registry.getRegistry(null, null).unregisterComponent(name.toString());
    }

    protected final void unregister(ObjectName on) {
        Registry.getRegistry(null, null).unregisterComponent(on);
    }

    @Override
    public final void postDeregister() {
    }

    @Override
    public final void postRegister(Boolean registrationDone) {
    }

    @Override
    public final void preDeregister() throws Exception {
    }

    @Override
    public final ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception {
        this.mserver = server;
        this.oname = name;
        this.domain = name.getDomain().intern();
        return this.oname;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.osgi.container.OsgiContainerManager
 *  org.springframework.beans.factory.FactoryBean
 */
package com.atlassian.applinks.host.spring;

import com.atlassian.applinks.host.OsgiServiceProxyFactory;
import com.atlassian.plugin.osgi.container.OsgiContainerManager;
import org.springframework.beans.factory.FactoryBean;

abstract class AbstractAppLinksServiceFactoryBean
implements FactoryBean {
    private final OsgiServiceProxyFactory applinksApiProxyFactory;
    private final Class apiClass;
    private long timeoutInMillis;

    public AbstractAppLinksServiceFactoryBean(OsgiContainerManager osgiContainerManager, Class apiClass) {
        this.applinksApiProxyFactory = new OsgiServiceProxyFactory(osgiContainerManager);
        this.apiClass = apiClass;
        this.timeoutInMillis = 10000L;
    }

    public Object getObject() throws Exception {
        return this.applinksApiProxyFactory.createProxy(this.apiClass, this.timeoutInMillis);
    }

    public Class getObjectType() {
        return this.apiClass;
    }

    public boolean isSingleton() {
        return true;
    }

    public long getTimeoutInMillis() {
        return this.timeoutInMillis;
    }

    public void setTimeoutInMillis(long timeoutInMillis) {
        this.timeoutInMillis = timeoutInMillis;
    }
}


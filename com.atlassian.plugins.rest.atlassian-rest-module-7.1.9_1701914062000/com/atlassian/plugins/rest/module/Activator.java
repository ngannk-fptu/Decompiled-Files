/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleActivator
 *  org.osgi.framework.BundleContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.plugins.rest.module;

import com.sun.jersey.server.impl.provider.RuntimeDelegateImpl;
import javax.ws.rs.ext.RuntimeDelegate;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class Activator
implements InitializingBean,
DisposableBean {
    private static final Logger LOG = LoggerFactory.getLogger(Activator.class);
    private final BundleActivator coreActivator = new com.sun.jersey.core.osgi.Activator();
    private final BundleContext bundleContext;

    public Activator(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public void afterPropertiesSet() throws Exception {
        this.coreActivator.start(this.bundleContext);
        LOG.debug("jersey-server bundle activator registering JAX-RS RuntimeDelegate instance");
        RuntimeDelegate.setInstance(new RuntimeDelegateImpl());
    }

    public void destroy() throws Exception {
        this.coreActivator.stop(this.bundleContext);
    }
}


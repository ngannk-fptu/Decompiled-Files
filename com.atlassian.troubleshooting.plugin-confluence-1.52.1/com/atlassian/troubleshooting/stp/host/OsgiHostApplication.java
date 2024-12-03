/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleContext
 *  org.osgi.util.tracker.ServiceTracker
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.stp.host;

import com.atlassian.troubleshooting.stp.spi.HostApplication;
import java.util.concurrent.Callable;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;

public class OsgiHostApplication
implements HostApplication,
DisposableBean {
    private final ServiceTracker hostApplicationTracker;

    @Autowired
    public OsgiHostApplication(BundleContext bundleContext) {
        this.hostApplicationTracker = new ServiceTracker(bundleContext, HostApplication.class.getName(), null);
        this.hostApplicationTracker.open();
    }

    @Override
    public <T> Callable<T> asUser(String username, Callable<T> operation) {
        HostApplication delegate = (HostApplication)this.hostApplicationTracker.getService();
        return delegate == null ? operation : delegate.asUser(username, operation);
    }

    public void destroy() {
        this.hostApplicationTracker.close();
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ReferenceMode
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleListener
 *  org.osgi.framework.ServiceReference
 *  org.osgi.util.tracker.ServiceTracker
 *  org.osgi.util.tracker.ServiceTrackerCustomizer
 */
package com.atlassian.plugin.osgi.container;

import com.atlassian.plugin.ReferenceMode;
import com.atlassian.plugin.osgi.hostcomponents.HostComponentRegistration;
import java.io.File;
import java.util.List;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public interface OsgiContainerManager {
    public void start();

    public void stop();

    public Bundle installBundle(File var1, ReferenceMode var2);

    public boolean isRunning();

    public Bundle[] getBundles();

    public ServiceReference[] getRegisteredServices();

    public List<HostComponentRegistration> getHostComponentRegistrations();

    public ServiceTracker getServiceTracker(String var1);

    public ServiceTracker getServiceTracker(String var1, ServiceTrackerCustomizer var2);

    public void addBundleListener(BundleListener var1);

    public void removeBundleListener(BundleListener var1);
}


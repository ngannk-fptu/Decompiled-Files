/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.felix.framework.Felix;
import org.apache.felix.framework.PackageAdminImpl;
import org.apache.felix.framework.ServiceRegistry;
import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.FrameworkWiring;
import org.osgi.resource.Requirement;
import org.osgi.service.packageadmin.PackageAdmin;

class FrameworkWiringImpl
implements FrameworkWiring,
Runnable {
    private final Felix m_felix;
    private final ServiceRegistry m_registry;
    private final List<Collection<Bundle>> m_requests = new ArrayList<Collection<Bundle>>();
    private final List<FrameworkListener[]> m_requestListeners = new ArrayList<FrameworkListener[]>();
    private ServiceRegistration<PackageAdmin> m_paReg;
    private Thread m_thread = null;

    public FrameworkWiringImpl(Felix felix, ServiceRegistry registry) {
        this.m_felix = felix;
        this.m_registry = registry;
    }

    void start() {
        this.m_paReg = this.m_registry.registerService(this.m_felix, new String[]{PackageAdmin.class.getName()}, new PackageAdminImpl(this.m_felix), null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void stop() {
        List<Collection<Bundle>> list = this.m_requests;
        synchronized (list) {
            if (this.m_thread != null) {
                this.m_thread = null;
                this.m_requests.notifyAll();
            }
        }
    }

    @Override
    public Bundle getBundle() {
        return this.m_felix;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void refreshBundles(Collection<Bundle> bundles, FrameworkListener ... listeners) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new AdminPermission(this.m_felix, "resolve"));
        }
        List<Collection<Bundle>> list = this.m_requests;
        synchronized (list) {
            if (this.m_thread == null) {
                this.m_thread = new Thread((Runnable)this, "FelixFrameworkWiring");
                this.m_thread.setDaemon(true);
                this.m_thread.start();
            }
            this.m_requests.add(bundles);
            this.m_requestListeners.add(listeners);
            this.m_requests.notifyAll();
        }
    }

    @Override
    public boolean resolveBundles(Collection<Bundle> bundles) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new AdminPermission(this.m_felix, "resolve"));
        }
        if (this.m_thread == null) {
            return false;
        }
        return this.m_felix.resolveBundles(bundles);
    }

    @Override
    public Collection<Bundle> getRemovalPendingBundles() {
        return this.m_felix.getRemovalPendingBundles();
    }

    @Override
    public Collection<Bundle> getDependencyClosure(Collection<Bundle> targets) {
        return this.m_felix.getDependencyClosure(targets);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        while (true) {
            Collection<Bundle> bundles = null;
            FrameworkListener[] listeners = null;
            List<Collection<Bundle>> list = this.m_requests;
            synchronized (list) {
                while (this.m_requests.isEmpty()) {
                    if (this.m_thread == null) {
                        return;
                    }
                    try {
                        this.m_requests.wait();
                    }
                    catch (InterruptedException interruptedException) {}
                }
                bundles = this.m_requests.get(0);
                listeners = this.m_requestListeners.get(0);
            }
            this.m_felix.refreshPackages(bundles, listeners);
            list = this.m_requests;
            synchronized (list) {
                this.m_requests.remove(0);
                this.m_requestListeners.remove(0);
            }
        }
    }

    @Override
    public Collection<BundleCapability> findProviders(Requirement requirement) {
        return this.m_felix.findProviders(requirement);
    }
}


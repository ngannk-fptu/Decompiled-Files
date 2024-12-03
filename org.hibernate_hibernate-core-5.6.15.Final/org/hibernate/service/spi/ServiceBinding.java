/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.service.spi;

import org.hibernate.service.Service;
import org.hibernate.service.spi.ServiceInitiator;
import org.jboss.logging.Logger;

public final class ServiceBinding<R extends Service> {
    private static final Logger log = Logger.getLogger(ServiceBinding.class);
    private final ServiceLifecycleOwner lifecycleOwner;
    private final Class<R> serviceRole;
    private final ServiceInitiator<R> serviceInitiator;
    private volatile R service;

    public ServiceBinding(ServiceLifecycleOwner lifecycleOwner, Class<R> serviceRole, R service) {
        this.lifecycleOwner = lifecycleOwner;
        this.serviceRole = serviceRole;
        this.serviceInitiator = null;
        this.service = service;
    }

    public ServiceBinding(ServiceLifecycleOwner lifecycleOwner, ServiceInitiator<R> serviceInitiator) {
        this.lifecycleOwner = lifecycleOwner;
        this.serviceRole = serviceInitiator.getServiceInitiated();
        this.serviceInitiator = serviceInitiator;
    }

    public ServiceLifecycleOwner getLifecycleOwner() {
        return this.lifecycleOwner;
    }

    public Class<R> getServiceRole() {
        return this.serviceRole;
    }

    public ServiceInitiator<R> getServiceInitiator() {
        return this.serviceInitiator;
    }

    public R getService() {
        return this.service;
    }

    public void setService(R service) {
        if (this.service != null && log.isDebugEnabled()) {
            log.debug((Object)("Overriding existing service binding [" + this.serviceRole.getName() + "]"));
        }
        this.service = service;
    }

    public static interface ServiceLifecycleOwner {
        public <R extends Service> R initiateService(ServiceInitiator<R> var1);

        public <R extends Service> void configureService(ServiceBinding<R> var1);

        public <R extends Service> void injectDependencies(ServiceBinding<R> var1);

        public <R extends Service> void startService(ServiceBinding<R> var1);

        public <R extends Service> void stopService(ServiceBinding<R> var1);
    }
}


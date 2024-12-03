/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.tenancy.api.TenantAccessor
 *  com.atlassian.tenancy.api.helper.PerTenantInitialiser
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.jmx.export.MBeanExportException
 *  org.springframework.jmx.export.MBeanExporter
 */
package com.atlassian.confluence.jmx;

import com.atlassian.confluence.jmx.MBeanExporterWithUnregister;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.tenancy.api.TenantAccessor;
import com.atlassian.tenancy.api.helper.PerTenantInitialiser;
import com.google.common.base.Preconditions;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import javax.management.ObjectName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.MBeanExportException;
import org.springframework.jmx.export.MBeanExporter;

public class MBeanExporterWithUnregisterImpl
extends MBeanExporter
implements MBeanExporterWithUnregister {
    private static final Logger log = LoggerFactory.getLogger(MBeanExporterWithUnregisterImpl.class);
    private final AtomicBoolean enabled = new AtomicBoolean();
    private final PerTenantInitialiser perTenantInitialiser;
    private Map<String, Supplier<Object>> lazyBeans;

    public MBeanExporterWithUnregisterImpl(EventPublisher eventPublisher, TenantAccessor tenantAccessor) {
        this.perTenantInitialiser = new PerTenantInitialiser(eventPublisher, tenantAccessor, new Runnable(){

            @Override
            public synchronized void run() {
                if (!MBeanExporterWithUnregisterImpl.this.enabled.get()) {
                    Preconditions.checkState((MBeanExporterWithUnregisterImpl.this.server != null ? 1 : 0) != 0, (Object)"MBeanServer not initialized. More wiring required!");
                    MBeanExporterWithUnregisterImpl.super.afterPropertiesSet();
                    MBeanExporterWithUnregisterImpl.this.registerLazyBeans();
                    if (!MBeanExporterWithUnregisterImpl.this.enabled.compareAndSet(false, true)) {
                        log.error("unexpected concurrent init");
                    }
                }
            }
        });
    }

    public void setLazyBeans(Map<String, Supplier<Object>> lazyBeans) {
        this.lazyBeans = lazyBeans;
    }

    private void registerLazyBeans() {
        if (this.lazyBeans != null && !this.lazyBeans.isEmpty()) {
            for (Map.Entry<String, Supplier<Object>> entry : this.lazyBeans.entrySet()) {
                Object exportedObj = entry.getValue().get();
                if (exportedObj == null) continue;
                this.registerBeanNameOrInstance(exportedObj, entry.getKey());
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return this.enabled.get();
    }

    public void afterPropertiesSet() {
        if (Boolean.getBoolean("confluence.jmx.disabled")) {
            return;
        }
        this.perTenantInitialiser.init();
    }

    public void destroy() {
        this.perTenantInitialiser.destroy();
        this.enabled.set(false);
        super.destroy();
    }

    public void unregisterManagedResource(ObjectName objectName) {
        if (!this.isEnabled() || this.server == null) {
            return;
        }
        super.unregisterManagedResource(objectName);
    }

    @Override
    @Deprecated
    public void unregisterBean(ObjectName name) {
        this.unregisterManagedResource(name);
    }

    @Override
    public boolean isRegistered(ObjectName name) {
        return this.server != null && this.server.isRegistered(name);
    }

    @Override
    public void safeRegisterManagedResource(Object value, ObjectName name) {
        try {
            if (this.isRegistered(name)) {
                this.logger.warn((Object)("Object '" + name + "' was already registered. Unregistering object."));
                this.unregisterManagedResource(name);
            }
        }
        catch (RuntimeException e) {
            this.logger.error((Object)("Error unregistering object : " + e.getMessage()), (Throwable)e);
        }
        try {
            this.registerManagedResource(value, name);
        }
        catch (MBeanExportException e) {
            this.logger.error((Object)e.getMessage(), (Throwable)e);
        }
    }

    public void registerManagedResource(Object o, ObjectName objectName) throws MBeanExportException {
        if (!this.isEnabled() || this.server == null) {
            return;
        }
        super.registerManagedResource(o, objectName);
    }
}


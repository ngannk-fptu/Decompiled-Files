/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceRegistration
 *  org.osgi.framework.Version
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationListener
 *  org.springframework.context.event.ApplicationContextEvent
 *  org.springframework.context.event.ContextClosedEvent
 *  org.springframework.context.event.ContextRefreshedEvent
 *  org.springframework.util.ObjectUtils
 */
package org.eclipse.gemini.blueprint.blueprint.container.support;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Dictionary;
import java.util.Hashtable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.context.support.internal.security.SecurityUtils;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.eclipse.gemini.blueprint.util.OsgiServiceUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Version;
import org.osgi.service.blueprint.container.BlueprintContainer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.ObjectUtils;

public class BlueprintContainerServicePublisher
implements ApplicationListener<ApplicationContextEvent> {
    private static final Log log = LogFactory.getLog(BlueprintContainerServicePublisher.class);
    private static final String BLUEPRINT_SYMNAME = "osgi.blueprint.container.symbolicname";
    private static final String BLUEPRINT_VERSION = "osgi.blueprint.container.version";
    private final BlueprintContainer blueprintContainer;
    private final BundleContext bundleContext;
    private volatile ServiceRegistration registration;

    public BlueprintContainerServicePublisher(BlueprintContainer blueprintContainer, BundleContext bundleContext) {
        this.blueprintContainer = blueprintContainer;
        this.bundleContext = bundleContext;
    }

    public void onApplicationEvent(ApplicationContextEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            this.registerService(event.getApplicationContext());
        } else if (event instanceof ContextClosedEvent) {
            this.unregisterService();
        }
    }

    private void registerService(ApplicationContext applicationContext) {
        Hashtable<String, String> serviceProperties = new Hashtable<String, String>();
        Bundle bundle = this.bundleContext.getBundle();
        String symName = bundle.getSymbolicName();
        ((Dictionary)serviceProperties).put("Bundle-SymbolicName", symName);
        ((Dictionary)serviceProperties).put(BLUEPRINT_SYMNAME, symName);
        Version version = OsgiBundleUtils.getBundleVersion(bundle);
        ((Dictionary)serviceProperties).put("Bundle-Version", (String)version);
        ((Dictionary)serviceProperties).put(BLUEPRINT_VERSION, (String)version);
        log.info((Object)("Publishing BlueprintContainer as OSGi service with properties " + serviceProperties));
        Object[] serviceNames = new String[]{BlueprintContainer.class.getName()};
        if (log.isDebugEnabled()) {
            log.debug((Object)("Publishing service under classes " + ObjectUtils.nullSafeToString((Object[])serviceNames)));
        }
        AccessControlContext acc = SecurityUtils.getAccFrom(applicationContext);
        this.registration = System.getSecurityManager() != null ? AccessController.doPrivileged(new PrivilegedAction<ServiceRegistration>((String[])serviceNames, serviceProperties){
            final /* synthetic */ String[] val$serviceNames;
            final /* synthetic */ Dictionary val$serviceProperties;
            {
                this.val$serviceNames = stringArray;
                this.val$serviceProperties = dictionary;
            }

            @Override
            public ServiceRegistration run() {
                return BlueprintContainerServicePublisher.this.bundleContext.registerService(this.val$serviceNames, (Object)BlueprintContainerServicePublisher.this.blueprintContainer, this.val$serviceProperties);
            }
        }, acc) : this.bundleContext.registerService((String[])serviceNames, (Object)this.blueprintContainer, serviceProperties);
    }

    private void unregisterService() {
        OsgiServiceUtils.unregisterService(this.registration);
        this.registration = null;
    }
}


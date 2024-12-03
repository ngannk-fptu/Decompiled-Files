/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.application;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.spi.component.ComponentProvider;
import com.sun.jersey.core.spi.component.ProviderFactory;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Logger;
import javax.ws.rs.core.Application;

public class DeferredResourceConfig
extends DefaultResourceConfig {
    private static final Logger LOGGER = Logger.getLogger(DeferredResourceConfig.class.getName());
    private final Class<? extends Application> appClass;
    private final Set<Class<?>> defaultClasses;

    public DeferredResourceConfig(Class<? extends Application> appClass) {
        this(appClass, Collections.emptySet());
    }

    public DeferredResourceConfig(Class<? extends Application> appClass, Set<Class<?>> defaultClasses) {
        this.appClass = appClass;
        this.defaultClasses = defaultClasses;
    }

    public ApplicationHolder getApplication(ProviderFactory pf) {
        return new ApplicationHolder(pf);
    }

    public class ApplicationHolder {
        private final Application originalApp;
        private final DefaultResourceConfig adaptedApp;

        private ApplicationHolder(ProviderFactory pf) {
            ComponentProvider cp = pf.getComponentProvider(DeferredResourceConfig.this.appClass);
            if (cp == null) {
                throw new ContainerException("The Application class " + DeferredResourceConfig.this.appClass.getName() + " could not be instantiated");
            }
            this.originalApp = (Application)cp.getInstance();
            if ((this.originalApp.getClasses() == null || this.originalApp.getClasses().isEmpty()) && (this.originalApp.getSingletons() == null || this.originalApp.getSingletons().isEmpty())) {
                LOGGER.info("Instantiated the Application class " + DeferredResourceConfig.this.appClass.getName() + ". The following root resource and provider classes are registered: " + DeferredResourceConfig.this.defaultClasses);
                this.adaptedApp = new DefaultResourceConfig(DeferredResourceConfig.this.defaultClasses);
                this.adaptedApp.add(this.originalApp);
            } else {
                LOGGER.info("Instantiated the Application class " + DeferredResourceConfig.this.appClass.getName());
                this.adaptedApp = null;
            }
            if (this.originalApp instanceof ResourceConfig) {
                ResourceConfig rc = (ResourceConfig)this.originalApp;
                DeferredResourceConfig.this.getFeatures().putAll(rc.getFeatures());
                DeferredResourceConfig.this.getProperties().putAll(rc.getProperties());
                DeferredResourceConfig.this.getExplicitRootResources().putAll(rc.getExplicitRootResources());
                DeferredResourceConfig.this.getMediaTypeMappings().putAll(rc.getMediaTypeMappings());
                DeferredResourceConfig.this.getLanguageMappings().putAll(rc.getLanguageMappings());
            }
        }

        public Application getOriginalApplication() {
            return this.originalApp;
        }

        public Application getApplication() {
            return this.adaptedApp != null ? this.adaptedApp : this.originalApp;
        }
    }
}


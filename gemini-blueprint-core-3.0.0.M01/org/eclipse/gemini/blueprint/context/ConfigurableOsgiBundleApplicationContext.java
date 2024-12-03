/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.springframework.context.ConfigurableApplicationContext
 */
package org.eclipse.gemini.blueprint.context;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.context.ConfigurableApplicationContext;

public interface ConfigurableOsgiBundleApplicationContext
extends ConfigurableApplicationContext {
    public static final String APPLICATION_CONTEXT_SERVICE_PROPERTY_NAME = "org.eclipse.gemini.blueprint.context.service.name";
    public static final String SPRING_DM_APPLICATION_CONTEXT_SERVICE_PROPERTY_NAME = "org.springframework.context.service.name";
    public static final String BUNDLE_CONTEXT_BEAN_NAME = "bundleContext";
    public static final String BUNDLE_BEAN_NAME = "bundle";

    public void setConfigLocations(String ... var1);

    public void setBundleContext(BundleContext var1);

    public BundleContext getBundleContext();

    public Bundle getBundle();

    public void setPublishContextAsService(boolean var1);
}


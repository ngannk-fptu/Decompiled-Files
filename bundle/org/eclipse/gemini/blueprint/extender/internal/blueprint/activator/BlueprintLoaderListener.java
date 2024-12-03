/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextListener
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 */
package org.eclipse.gemini.blueprint.extender.internal.blueprint.activator;

import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextListener;
import org.eclipse.gemini.blueprint.extender.OsgiApplicationContextCreator;
import org.eclipse.gemini.blueprint.extender.internal.activator.ApplicationContextConfigurationFactory;
import org.eclipse.gemini.blueprint.extender.internal.activator.ContextLoaderListener;
import org.eclipse.gemini.blueprint.extender.internal.activator.ListenerServiceActivator;
import org.eclipse.gemini.blueprint.extender.internal.activator.OsgiContextProcessor;
import org.eclipse.gemini.blueprint.extender.internal.activator.TypeCompatibilityChecker;
import org.eclipse.gemini.blueprint.extender.internal.blueprint.activator.BlueprintContainerProcessor;
import org.eclipse.gemini.blueprint.extender.internal.blueprint.activator.BlueprintListenerManager;
import org.eclipse.gemini.blueprint.extender.internal.blueprint.activator.BlueprintTypeCompatibilityChecker;
import org.eclipse.gemini.blueprint.extender.internal.blueprint.activator.support.BlueprintContainerConfig;
import org.eclipse.gemini.blueprint.extender.internal.blueprint.activator.support.BlueprintContainerCreator;
import org.eclipse.gemini.blueprint.extender.internal.blueprint.event.EventAdminDispatcher;
import org.eclipse.gemini.blueprint.extender.internal.support.ExtenderConfiguration;
import org.eclipse.gemini.blueprint.extender.support.ApplicationContextConfiguration;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class BlueprintLoaderListener
extends ContextLoaderListener {
    private volatile BlueprintListenerManager listenerManager;
    private volatile BlueprintContainerProcessor contextProcessor;
    private volatile TypeCompatibilityChecker typeChecker;
    private ListenerServiceActivator listenerServiceActivator;

    public BlueprintLoaderListener(ExtenderConfiguration extenderConfiguration, ListenerServiceActivator listenerServiceActivator) {
        super(extenderConfiguration);
        this.listenerServiceActivator = listenerServiceActivator;
    }

    @Override
    public void start(BundleContext context) throws Exception {
        this.listenerManager = new BlueprintListenerManager(context);
        EventAdminDispatcher dispatcher = new EventAdminDispatcher(context);
        Bundle bundle = context.getBundle();
        this.contextProcessor = new BlueprintContainerProcessor(dispatcher, this.listenerManager, bundle);
        this.typeChecker = new BlueprintTypeCompatibilityChecker(bundle);
        this.listenerServiceActivator.getMulticaster().addApplicationListener((OsgiBundleApplicationContextListener)this.contextProcessor);
        super.start(context);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
        this.listenerManager.destroy();
    }

    @Override
    protected ApplicationContextConfigurationFactory createContextConfigFactory() {
        return new ApplicationContextConfigurationFactory(){

            @Override
            public ApplicationContextConfiguration createConfiguration(Bundle bundle) {
                return new BlueprintContainerConfig(bundle);
            }
        };
    }

    @Override
    protected OsgiApplicationContextCreator getOsgiApplicationContextCreator() {
        return new BlueprintContainerCreator();
    }

    @Override
    protected OsgiContextProcessor createContextProcessor() {
        return this.contextProcessor;
    }

    @Override
    protected TypeCompatibilityChecker getTypeCompatibilityChecker() {
        return this.typeChecker;
    }

    @Override
    protected String getManagedBundleExtenderVersionHeader() {
        return "BlueprintExtender-Version";
    }
}


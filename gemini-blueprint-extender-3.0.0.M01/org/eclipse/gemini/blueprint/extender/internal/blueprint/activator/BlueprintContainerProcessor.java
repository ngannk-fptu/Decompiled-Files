/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.eclipse.gemini.blueprint.blueprint.container.SpringBlueprintContainer
 *  org.eclipse.gemini.blueprint.blueprint.container.SpringBlueprintConverter
 *  org.eclipse.gemini.blueprint.blueprint.container.SpringBlueprintConverterService
 *  org.eclipse.gemini.blueprint.blueprint.container.support.BlueprintContainerServicePublisher
 *  org.eclipse.gemini.blueprint.context.BundleContextAware
 *  org.eclipse.gemini.blueprint.context.ConfigurableOsgiBundleApplicationContext
 *  org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEvent
 *  org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextListener
 *  org.eclipse.gemini.blueprint.context.event.OsgiBundleContextFailedEvent
 *  org.eclipse.gemini.blueprint.context.event.OsgiBundleContextRefreshedEvent
 *  org.eclipse.gemini.blueprint.service.importer.event.OsgiServiceDependencyWaitStartingEvent
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.service.blueprint.container.BlueprintContainer
 *  org.osgi.service.blueprint.container.BlueprintEvent
 *  org.springframework.beans.BeanUtils
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.BeanFactoryPostProcessor
 *  org.springframework.beans.factory.config.ConfigurableBeanFactory
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.beans.factory.config.ConstructorArgumentValues
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.beans.factory.support.GenericBeanDefinition
 *  org.springframework.context.ApplicationEvent
 *  org.springframework.context.ApplicationListener
 *  org.springframework.context.ConfigurableApplicationContext
 *  org.springframework.context.event.ContextClosedEvent
 *  org.springframework.context.event.ContextRefreshedEvent
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.util.ClassUtils
 */
package org.eclipse.gemini.blueprint.extender.internal.blueprint.activator;

import java.util.Collection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.blueprint.container.SpringBlueprintContainer;
import org.eclipse.gemini.blueprint.blueprint.container.SpringBlueprintConverter;
import org.eclipse.gemini.blueprint.blueprint.container.SpringBlueprintConverterService;
import org.eclipse.gemini.blueprint.blueprint.container.support.BlueprintContainerServicePublisher;
import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.eclipse.gemini.blueprint.context.ConfigurableOsgiBundleApplicationContext;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEvent;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextListener;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleContextFailedEvent;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleContextRefreshedEvent;
import org.eclipse.gemini.blueprint.extender.event.BootstrappingDependenciesEvent;
import org.eclipse.gemini.blueprint.extender.event.BootstrappingDependenciesFailedEvent;
import org.eclipse.gemini.blueprint.extender.internal.activator.OsgiContextProcessor;
import org.eclipse.gemini.blueprint.extender.internal.blueprint.activator.BlueprintListenerManager;
import org.eclipse.gemini.blueprint.extender.internal.blueprint.event.EventAdminDispatcher;
import org.eclipse.gemini.blueprint.service.importer.event.OsgiServiceDependencyWaitStartingEvent;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.BlueprintContainer;
import org.osgi.service.blueprint.container.BlueprintEvent;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.ClassUtils;

public class BlueprintContainerProcessor
implements OsgiBundleApplicationContextListener<OsgiBundleApplicationContextEvent>,
OsgiContextProcessor {
    private static final Log log = LogFactory.getLog(BlueprintContainerProcessor.class);
    private static final Class<?> ENV_FB_CLASS;
    private final EventAdminDispatcher dispatcher;
    private final BlueprintListenerManager listenerManager;
    private final Bundle extenderBundle;
    private final BeanFactoryPostProcessor cycleBreaker;

    public BlueprintContainerProcessor(EventAdminDispatcher dispatcher, BlueprintListenerManager listenerManager, Bundle extenderBundle) {
        this.dispatcher = dispatcher;
        this.listenerManager = listenerManager;
        this.extenderBundle = extenderBundle;
        Class processorClass = ClassUtils.resolveClassName((String)"org.eclipse.gemini.blueprint.blueprint.container.support.internal.config.CycleOrderingProcessor", (ClassLoader)BundleContextAware.class.getClassLoader());
        this.cycleBreaker = (BeanFactoryPostProcessor)BeanUtils.instantiate((Class)processorClass);
    }

    @Override
    public void postProcessClose(ConfigurableOsgiBundleApplicationContext context) {
        BlueprintEvent destroyedEvent = new BlueprintEvent(4, context.getBundle(), this.extenderBundle);
        this.listenerManager.blueprintEvent(destroyedEvent);
        this.dispatcher.afterClose(destroyedEvent);
    }

    @Override
    public void postProcessRefresh(ConfigurableOsgiBundleApplicationContext context) {
        BlueprintEvent createdEvent = new BlueprintEvent(2, context.getBundle(), this.extenderBundle);
        this.listenerManager.blueprintEvent(createdEvent);
        this.dispatcher.afterRefresh(createdEvent);
    }

    @Override
    public void postProcessRefreshFailure(ConfigurableOsgiBundleApplicationContext context, Throwable th) {
        BlueprintEvent failureEvent = new BlueprintEvent(5, context.getBundle(), this.extenderBundle, th);
        this.listenerManager.blueprintEvent(failureEvent);
        this.dispatcher.refreshFailure(failureEvent);
    }

    @Override
    public void preProcessClose(ConfigurableOsgiBundleApplicationContext context) {
        BlueprintEvent destroyingEvent = new BlueprintEvent(3, context.getBundle(), this.extenderBundle);
        this.listenerManager.blueprintEvent(destroyingEvent);
        this.dispatcher.beforeClose(destroyingEvent);
    }

    @Override
    public void preProcessRefresh(final ConfigurableOsgiBundleApplicationContext context) {
        final BundleContext bundleContext = context.getBundleContext();
        final BlueprintContainer blueprintContainer = this.createBlueprintContainer(context);
        context.addApplicationListener((ApplicationListener)new BlueprintContainerServicePublisher(blueprintContainer, bundleContext));
        context.addApplicationListener((ApplicationListener)new BlueprintWaitingEventDispatcher(context.getBundleContext()));
        context.addBeanFactoryPostProcessor(new BeanFactoryPostProcessor(){
            private static final String BLUEPRINT_BUNDLE = "blueprintBundle";
            private static final String BLUEPRINT_BUNDLE_CONTEXT = "blueprintBundleContext";
            private static final String BLUEPRINT_CONTAINER = "blueprintContainer";
            private static final String BLUEPRINT_EXTENDER = "blueprintExtenderBundle";
            private static final String BLUEPRINT_CONVERTER = "blueprintConverter";

            public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
                Log logger = LogFactory.getLog(context.getClass());
                if (!(beanFactory instanceof BeanDefinitionRegistry)) {
                    logger.warn((Object)("Environmental beans will be registered as singletons instead of usual bean definitions since beanFactory " + beanFactory + " is not a BeanDefinitionRegistry"));
                }
                this.addPredefinedBlueprintBean(beanFactory, BLUEPRINT_BUNDLE, bundleContext.getBundle(), logger);
                this.addPredefinedBlueprintBean(beanFactory, BLUEPRINT_BUNDLE_CONTEXT, bundleContext, logger);
                this.addPredefinedBlueprintBean(beanFactory, BLUEPRINT_CONTAINER, blueprintContainer, logger);
                this.addPredefinedBlueprintBean(beanFactory, BLUEPRINT_CONVERTER, new SpringBlueprintConverter((ConfigurableBeanFactory)beanFactory), logger);
                beanFactory.setConversionService((ConversionService)new SpringBlueprintConverterService(beanFactory.getConversionService(), (ConfigurableBeanFactory)beanFactory));
            }

            private void addPredefinedBlueprintBean(ConfigurableListableBeanFactory beanFactory, String beanName, Object value, Log logger) {
                if (!beanFactory.containsLocalBean(beanName)) {
                    logger.debug((Object)("Registering pre-defined bean named " + beanName));
                    if (beanFactory instanceof BeanDefinitionRegistry) {
                        BeanDefinitionRegistry registry = (BeanDefinitionRegistry)beanFactory;
                        GenericBeanDefinition def = new GenericBeanDefinition();
                        def.setBeanClass(ENV_FB_CLASS);
                        ConstructorArgumentValues cav = new ConstructorArgumentValues();
                        cav.addIndexedArgumentValue(0, value);
                        def.setConstructorArgumentValues(cav);
                        def.setLazyInit(false);
                        def.setRole(2);
                        registry.registerBeanDefinition(beanName, (BeanDefinition)def);
                    } else {
                        beanFactory.registerSingleton(beanName, value);
                    }
                } else {
                    logger.warn((Object)("A bean named " + beanName + " already exists; aborting registration of the predefined value..."));
                }
            }
        });
        context.addBeanFactoryPostProcessor(this.cycleBreaker);
        BlueprintEvent creatingEvent = new BlueprintEvent(1, context.getBundle(), this.extenderBundle);
        this.listenerManager.blueprintEvent(creatingEvent);
        this.dispatcher.beforeRefresh(creatingEvent);
    }

    private BlueprintContainer createBlueprintContainer(ConfigurableOsgiBundleApplicationContext context) {
        return new SpringBlueprintContainer((ConfigurableApplicationContext)context);
    }

    public void onOsgiApplicationEvent(OsgiBundleApplicationContextEvent evt) {
        if (evt instanceof BootstrappingDependenciesEvent) {
            BootstrappingDependenciesEvent event = (BootstrappingDependenciesEvent)evt;
            Collection<String> flts = event.getDependencyFilters();
            if (flts.isEmpty()) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("All dependencies satisfied, not sending Blueprint GRACE event with emtpy dependencies from " + (Object)((Object)event)));
                }
            } else {
                String[] filters = flts.toArray(new String[flts.size()]);
                BlueprintEvent graceEvent = new BlueprintEvent(6, evt.getBundle(), this.extenderBundle, filters);
                this.listenerManager.blueprintEvent(graceEvent);
                this.dispatcher.grace(graceEvent);
            }
            return;
        }
        if (evt instanceof BootstrappingDependenciesFailedEvent) {
            BootstrappingDependenciesFailedEvent event = (BootstrappingDependenciesFailedEvent)evt;
            Collection<String> flts = event.getDependencyFilters();
            String[] filters = flts.toArray(new String[flts.size()]);
            BlueprintEvent failureEvent = new BlueprintEvent(5, evt.getBundle(), this.extenderBundle, filters, event.getFailureCause());
            this.listenerManager.blueprintEvent(failureEvent);
            this.dispatcher.refreshFailure(failureEvent);
            return;
        }
        if (evt instanceof OsgiBundleContextRefreshedEvent) {
            this.postProcessRefresh((ConfigurableOsgiBundleApplicationContext)evt.getApplicationContext());
            return;
        }
        if (evt instanceof OsgiBundleContextFailedEvent) {
            OsgiBundleContextFailedEvent failureEvent = (OsgiBundleContextFailedEvent)evt;
            this.postProcessRefreshFailure((ConfigurableOsgiBundleApplicationContext)failureEvent.getApplicationContext(), failureEvent.getFailureCause());
            return;
        }
    }

    static {
        String className = "org.eclipse.gemini.blueprint.blueprint.reflect.internal.metadata.EnvironmentManagerFactoryBean";
        ClassLoader loader = OsgiBundleApplicationContextEvent.class.getClassLoader();
        ENV_FB_CLASS = ClassUtils.resolveClassName((String)className, (ClassLoader)loader);
    }

    class BlueprintWaitingEventDispatcher
    implements ApplicationListener<ApplicationEvent> {
        private final BundleContext bundleContext;
        private volatile boolean enabled = true;
        private volatile boolean initialized = false;

        BlueprintWaitingEventDispatcher(BundleContext context) {
            this.bundleContext = context;
        }

        public void onApplicationEvent(ApplicationEvent event) {
            if (event instanceof ContextClosedEvent) {
                this.enabled = false;
                return;
            }
            if (event instanceof ContextRefreshedEvent) {
                this.initialized = true;
                return;
            }
            if (event instanceof OsgiServiceDependencyWaitStartingEvent) {
                if (this.enabled) {
                    OsgiServiceDependencyWaitStartingEvent evt = (OsgiServiceDependencyWaitStartingEvent)event;
                    String[] filter = new String[]{evt.getServiceDependency().getServiceFilter().toString()};
                    BlueprintEvent waitingEvent = new BlueprintEvent(7, this.bundleContext.getBundle(), BlueprintContainerProcessor.this.extenderBundle, filter);
                    BlueprintContainerProcessor.this.listenerManager.blueprintEvent(waitingEvent);
                    BlueprintContainerProcessor.this.dispatcher.waiting(waitingEvent);
                }
                return;
            }
        }
    }
}


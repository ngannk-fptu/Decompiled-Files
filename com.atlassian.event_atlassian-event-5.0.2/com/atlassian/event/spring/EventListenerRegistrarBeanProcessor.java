/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.events.PluginDisabledEvent
 *  com.atlassian.plugin.event.events.PluginModuleDisabledEvent
 *  com.atlassian.plugin.event.events.PluginModuleEnabledEvent
 *  com.atlassian.plugin.eventlistener.descriptors.EventListenerModuleDescriptor
 *  com.atlassian.plugin.osgi.factory.descriptor.ComponentImportModuleDescriptor
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.NoSuchBeanDefinitionException
 *  org.springframework.beans.factory.config.ConfigurableBeanFactory
 *  org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor
 *  org.springframework.context.ApplicationEvent
 *  org.springframework.context.ApplicationListener
 *  org.springframework.context.event.ContextRefreshedEvent
 *  org.springframework.core.Ordered
 */
package com.atlassian.event.spring;

import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.event.config.ListenerHandlersConfiguration;
import com.atlassian.event.spi.ListenerHandler;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.events.PluginDisabledEvent;
import com.atlassian.plugin.event.events.PluginModuleDisabledEvent;
import com.atlassian.plugin.event.events.PluginModuleEnabledEvent;
import com.atlassian.plugin.eventlistener.descriptors.EventListenerModuleDescriptor;
import com.atlassian.plugin.osgi.factory.descriptor.ComponentImportModuleDescriptor;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;

@Deprecated
public class EventListenerRegistrarBeanProcessor
implements DestructionAwareBeanPostProcessor,
BeanFactoryAware,
Ordered,
ApplicationListener {
    private static final Set<String> BLACKLISTED_PLUGIN_KEYS = ImmutableSet.of((Object)"com.atlassian.upm.atlassian-universal-plugin-manager-plugin", (Object)"com.atlassian.activeobjects.activeobjects-plugin", (Object)"com.atlassian.applinks.applinks-plugin", (Object)"com.atlassian.crowd.embedded.admin", (Object)"com.atlassian.oauth.admin", (Object)"com.atlassian.oauth.consumer", (Object[])new String[]{"com.atlassian.oauth.consumer.sal", "com.atlassian.oauth.serviceprovider", "com.atlassian.oauth.serviceprovider.sal", "com.atlassian.plugins.rest.atlassian-rest-module", "com.atlassian.soy.soy-template-plugin", "com.atlassian.templaterenderer.api", "com.atlassian.templaterenderer.atlassian-template-renderer-velocity1.6-plugin", "com.atlassian.auiplugin"});
    private static final Logger LOG = LoggerFactory.getLogger(EventListenerRegistrarBeanProcessor.class);
    private final String eventListenerRegistrarBeanName;
    private final ListenerHandlersConfiguration listenerHandlersConfiguration;
    private final Map<String, Object> listenersToBeRegistered = Maps.newHashMap();
    private final Multimap<String, Object> eventListenersFromPlugins = HashMultimap.create();
    private ConfigurableBeanFactory beanFactory;
    private EventListenerRegistrar eventListenerRegistrar;
    private boolean ignoreFurtherBeanProcessing;

    public EventListenerRegistrarBeanProcessor(String eventListenerRegistrarBeanName, ListenerHandlersConfiguration listenerHandlersConfiguration) {
        this.eventListenerRegistrarBeanName = (String)Preconditions.checkNotNull((Object)eventListenerRegistrarBeanName);
        this.listenerHandlersConfiguration = (ListenerHandlersConfiguration)Preconditions.checkNotNull((Object)listenerHandlersConfiguration);
    }

    public int getOrder() {
        return 1;
    }

    @PluginEventListener
    public void onPluginModuleEnabled(PluginModuleEnabledEvent event) {
        Plugin plugin = event.getModule().getPlugin();
        if (BLACKLISTED_PLUGIN_KEYS.contains(plugin.getKey())) {
            return;
        }
        ModuleDescriptor moduleDescriptor = event.getModule();
        if (EventListenerRegistrarBeanProcessor.isSuitablePluginModule(moduleDescriptor)) {
            if (EventListenerRegistrarBeanProcessor.moduleDescriptorReturnsNewInstanceEveryTime(moduleDescriptor)) {
                return;
            }
            try {
                Object module = moduleDescriptor.getModule();
                try {
                    if (this.canBeRegisteredAsAListener(moduleDescriptor.getKey(), module)) {
                        this.eventListenersFromPlugins.put((Object)plugin.getKey(), module);
                        this.registerListener(moduleDescriptor.getKey(), module);
                    }
                }
                catch (NoClassDefFoundError e) {
                    LOG.debug("Skipping " + moduleDescriptor.getCompleteKey() + " because not all referenced classes are visible from the classloader.");
                }
                catch (Throwable t) {
                    LOG.info("Error registering eventlisteners for module " + moduleDescriptor.getCompleteKey() + "; skipping.", t);
                }
            }
            catch (Exception e) {
                return;
            }
        }
    }

    private static boolean isSuitablePluginModule(ModuleDescriptor moduleDescriptor) {
        Class<?> moduleDescriptorClass = moduleDescriptor.getClass();
        Class moduleClass = moduleDescriptor.getModuleClass();
        return !moduleDescriptorClass.equals(ComponentImportModuleDescriptor.class) && moduleClass != null && !moduleClass.equals(Void.class) && !(moduleDescriptor instanceof EventListenerModuleDescriptor);
    }

    @PluginEventListener
    public void onPluginDisabled(PluginDisabledEvent event) {
        Plugin plugin = event.getPlugin();
        Collection listeners = this.eventListenersFromPlugins.get((Object)plugin.getKey());
        if (listeners != null) {
            for (Object eventListener : listeners) {
                this.eventListenerRegistrar.unregister(eventListener);
            }
            this.eventListenersFromPlugins.removeAll((Object)plugin.getKey());
        }
    }

    @PluginEventListener
    public void onPluginModuleDisabled(PluginModuleDisabledEvent event) {
        ModuleDescriptor moduleDescriptor = event.getModule();
        Object module = null;
        try {
            module = moduleDescriptor.getModule();
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (module != null && this.eventListenersFromPlugins.remove((Object)moduleDescriptor.getPluginKey(), module)) {
            this.eventListenerRegistrar.unregister(module);
        }
    }

    private static boolean moduleDescriptorReturnsNewInstanceEveryTime(ModuleDescriptor moduleDescriptor) {
        return moduleDescriptor.getModule() != moduleDescriptor.getModule();
    }

    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof ContextRefreshedEvent) {
            this.ignoreFurtherBeanProcessing = true;
        }
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (beanName.equals(this.eventListenerRegistrarBeanName)) {
            this.eventListenerRegistrar = (EventListenerRegistrar)bean;
            if (this.isAListener(this)) {
                this.eventListenerRegistrar.register(this);
            }
            for (Object object : this.listenersToBeRegistered.values()) {
                this.eventListenerRegistrar.register(object);
            }
            this.listenersToBeRegistered.clear();
        }
        return bean;
    }

    public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
        this.unregisterListener(bean, beanName);
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (!this.ignoreFurtherBeanProcessing && this.canBeRegisteredAsAListener(beanName, bean)) {
            this.registerListener(beanName, bean);
        }
        return bean;
    }

    private boolean canBeRegisteredAsAListener(String beanName, Object bean) {
        if (this.isAListener(bean)) {
            try {
                return this.beanFactory.getMergedBeanDefinition(beanName).isSingleton();
            }
            catch (NoSuchBeanDefinitionException e) {
                return true;
            }
        }
        return false;
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableBeanFactory)beanFactory;
    }

    private void registerListener(String beanName, Object bean) {
        LOG.debug("Registering {} instance as an eventlistener", (Object)beanName);
        if (this.eventListenerRegistrar != null) {
            this.eventListenerRegistrar.register(bean);
        } else {
            this.listenersToBeRegistered.put(beanName, bean);
        }
    }

    private void unregisterListener(Object bean, String beanName) {
        if (this.eventListenerRegistrar != null) {
            this.eventListenerRegistrar.unregister(bean);
        } else {
            this.listenersToBeRegistered.remove(beanName);
        }
    }

    private boolean isAListener(Object object) {
        for (ListenerHandler handler : this.listenerHandlersConfiguration.getListenerHandlers()) {
            if (handler.getInvokers(object).isEmpty()) continue;
            return true;
        }
        return false;
    }
}


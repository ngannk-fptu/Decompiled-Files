/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ContainerManagedPlugin
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.osgi.factory.OsgiPlugin
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  javax.annotation.Nonnull
 *  org.dom4j.Element
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.ServiceRegistration
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.activeobjects.plugin;

import com.atlassian.activeobjects.ActiveObjectsPluginException;
import com.atlassian.activeobjects.EntitiesValidator;
import com.atlassian.activeobjects.admin.PluginInfo;
import com.atlassian.activeobjects.admin.PluginToTablesMapping;
import com.atlassian.activeobjects.config.ActiveObjectsConfiguration;
import com.atlassian.activeobjects.config.ActiveObjectsConfigurationFactory;
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask;
import com.atlassian.activeobjects.internal.TimedActiveObjectsUpgradeTask;
import com.atlassian.activeobjects.osgi.OsgiServiceUtils;
import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ContainerManagedPlugin;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.osgi.factory.OsgiPlugin;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import net.java.ao.RawEntity;
import net.java.ao.schema.TableNameConverter;
import org.dom4j.Element;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActiveObjectModuleDescriptor
extends AbstractModuleDescriptor<Object> {
    private final Logger logger = LoggerFactory.getLogger(((Object)((Object)this)).getClass());
    private final ActiveObjectsConfigurationFactory configurationFactory;
    private final OsgiServiceUtils osgiUtils;
    private final EntitiesValidator entitiesValidator;
    private final PluginToTablesMapping pluginToTablesMapping;
    private ServiceRegistration activeObjectsConfigurationServiceRegistration;
    private ServiceRegistration tableNameConverterServiceRegistration;
    private ActiveObjectsConfiguration configuration;

    ActiveObjectModuleDescriptor(ModuleFactory moduleFactory, ActiveObjectsConfigurationFactory configurationFactory, OsgiServiceUtils osgiUtils, PluginToTablesMapping pluginToTablesMapping, EntitiesValidator entitiesValidator) {
        super(moduleFactory);
        this.configurationFactory = (ActiveObjectsConfigurationFactory)Preconditions.checkNotNull((Object)configurationFactory);
        this.osgiUtils = (OsgiServiceUtils)Preconditions.checkNotNull((Object)osgiUtils);
        this.pluginToTablesMapping = (PluginToTablesMapping)Preconditions.checkNotNull((Object)pluginToTablesMapping);
        this.entitiesValidator = (EntitiesValidator)Preconditions.checkNotNull((Object)entitiesValidator);
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        Set<Class<? extends RawEntity<?>>> entities = this.getEntities(element);
        List<ActiveObjectsUpgradeTask> upgradeTasks = this.getUpgradeTasks(element);
        this.configuration = this.getActiveObjectsConfiguration(this.getNameSpace(element), entities, upgradeTasks);
        Set<Class<? extends RawEntity<?>>> entityClasses = this.entitiesValidator.check(entities, this.configuration.getNameConverters());
        this.recordTables(entityClasses, this.configuration.getNameConverters().getTableNameConverter());
    }

    @VisibleForTesting
    List<ActiveObjectsUpgradeTask> getUpgradeTasks(Element element) {
        List<Element> upgradeTask = this.getUpgradeTaskElements(element);
        List<Class<ActiveObjectsUpgradeTask>> classes = this.getUpgradeTaskClasses(upgradeTask);
        return Lists.transform(classes, upgradeTaskClass -> new TimedActiveObjectsUpgradeTask((ActiveObjectsUpgradeTask)this.createBean((Class)upgradeTaskClass), this.getPluginKey()));
    }

    @VisibleForTesting
    <T> T createBean(@Nonnull Class<T> clazz) {
        if (!(this.getPlugin() instanceof ContainerManagedPlugin)) {
            throw new ActiveObjectsPluginException("Plugin " + this.getPlugin().getKey() + " " + this.getPlugin().getClass().getCanonicalName() + " is not a ContainerManagedPlugin, cannot wire context");
        }
        return (T)((ContainerManagedPlugin)this.getPlugin()).getContainerAccessor().createBean(clazz);
    }

    @VisibleForTesting
    List<Element> getUpgradeTaskElements(Element element) {
        return ActiveObjectModuleDescriptor.getSubElements(element, "upgradeTask");
    }

    @VisibleForTesting
    List<Class<ActiveObjectsUpgradeTask>> getUpgradeTaskClasses(List<Element> upgradeTask) {
        return Lists.transform(upgradeTask, utElement -> {
            String upgradeTaskClass = utElement.getText().trim();
            this.logger.debug("Found upgrade task class {}", (Object)upgradeTaskClass);
            return this.getUpgradeTaskClass(upgradeTaskClass);
        });
    }

    private Class<ActiveObjectsUpgradeTask> getUpgradeTaskClass(String upgradeTask) {
        try {
            return this.getPlugin().loadClass(upgradeTask, ((Object)((Object)this)).getClass());
        }
        catch (ClassNotFoundException e) {
            throw new ActiveObjectsPluginException(e);
        }
    }

    private void recordTables(Set<Class<? extends RawEntity<?>>> entityClasses, final TableNameConverter tableNameConverter) {
        this.pluginToTablesMapping.add(PluginInfo.of(this.getPlugin()), Lists.transform((List)Lists.newLinkedList(entityClasses), (Function)new Function<Class<? extends RawEntity<?>>, String>(){

            public String apply(Class<? extends RawEntity<?>> from) {
                return tableNameConverter.getName(from);
            }
        }));
    }

    public void enabled() {
        super.enabled();
        if (this.tableNameConverterServiceRegistration == null) {
            this.tableNameConverterServiceRegistration = this.osgiUtils.registerService(this.getBundle(), TableNameConverter.class, this.configuration.getNameConverters().getTableNameConverter());
        }
        if (this.activeObjectsConfigurationServiceRegistration == null) {
            this.activeObjectsConfigurationServiceRegistration = this.osgiUtils.registerService(this.getBundle(), ActiveObjectsConfiguration.class, this.configuration);
        }
    }

    public void disabled() {
        this.unregister(this.activeObjectsConfigurationServiceRegistration);
        this.activeObjectsConfigurationServiceRegistration = null;
        this.unregister(this.tableNameConverterServiceRegistration);
        this.tableNameConverterServiceRegistration = null;
        super.disabled();
    }

    public Object getModule() {
        return null;
    }

    public ActiveObjectsConfiguration getConfiguration() {
        return this.configuration;
    }

    private ActiveObjectsConfiguration getActiveObjectsConfiguration(String namespace, Set<Class<? extends RawEntity<?>>> entities, List<ActiveObjectsUpgradeTask> upgradeTasks) {
        return this.configurationFactory.getConfiguration(this.getBundle(), namespace, entities, upgradeTasks);
    }

    private void unregister(ServiceRegistration serviceRegistration) {
        if (serviceRegistration != null) {
            try {
                serviceRegistration.unregister();
            }
            catch (IllegalStateException ignored) {
                this.logger.debug("Service has already been unregistered", (Throwable)ignored);
            }
        }
    }

    private String getNameSpace(Element element) {
        String custom = element.attributeValue("namespace");
        return custom != null ? custom : this.getBundle().getSymbolicName();
    }

    private Set<Class<? extends RawEntity<?>>> getEntities(Element element) {
        return Sets.newHashSet((Iterable)Iterables.transform(this.getEntityClassNames(element), (Function)new Function<String, Class<? extends RawEntity<?>>>(){

            public Class<? extends RawEntity<?>> apply(String entityClassName) {
                return ActiveObjectModuleDescriptor.this.getEntityClass(entityClassName);
            }
        }));
    }

    private Class<? extends RawEntity<?>> getEntityClass(String entityClassName) {
        try {
            return this.getPlugin().loadClass(entityClassName, ((Object)((Object)this)).getClass());
        }
        catch (ClassNotFoundException e) {
            throw new ActiveObjectsPluginException(e);
        }
    }

    private Iterable<String> getEntityClassNames(Element element) {
        return Iterables.transform(ActiveObjectModuleDescriptor.getSubElements(element, "entity"), (Function)new Function<Element, String>(){

            public String apply(Element entityElement) {
                String entityClassName = entityElement.getText().trim();
                ActiveObjectModuleDescriptor.this.logger.debug("Found entity class {}", (Object)entityClassName);
                return entityClassName;
            }
        });
    }

    private Bundle getBundle() {
        return ((OsgiPlugin)this.getPlugin()).getBundle();
    }

    private static List<Element> getSubElements(Element element, String name) {
        return element.elements(name);
    }
}


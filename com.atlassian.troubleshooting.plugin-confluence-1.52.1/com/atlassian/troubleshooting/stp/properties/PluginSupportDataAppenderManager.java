/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.tracker.DefaultPluginModuleTracker
 *  com.atlassian.plugin.tracker.PluginModuleTracker
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.Validate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.stp.properties;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.tracker.DefaultPluginModuleTracker;
import com.atlassian.plugin.tracker.PluginModuleTracker;
import com.atlassian.troubleshooting.stp.properties.DefaultSupportDataBuilderContext;
import com.atlassian.troubleshooting.stp.properties.PropertyStore;
import com.atlassian.troubleshooting.stp.properties.SupportDataAppenderManager;
import com.atlassian.troubleshooting.stp.spi.SupportDataAppender;
import com.atlassian.troubleshooting.stp.spi.SupportDataBuilder;
import com.atlassian.troubleshooting.stp.spi.SupportDataBuilderContext;
import com.atlassian.troubleshooting.stp.spi.SupportDataDetail;
import com.atlassian.troubleshooting.stp.spi.SupportDataModuleDescriptor;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;

public class PluginSupportDataAppenderManager
implements SupportDataAppenderManager,
DisposableBean {
    private static final Logger LOG = LoggerFactory.getLogger(PluginSupportDataAppenderManager.class);
    private final PluginModuleTracker<SupportDataAppender<?>, SupportDataModuleDescriptor> moduleTracker;
    private final int maximumContextObjectDepth = Integer.valueOf(System.getProperty("stp.spi.contexts.maximum", "1000"));
    private final int maximumCategoryDepth = Integer.valueOf(System.getProperty("stp.spi.categories.maximum", "25"));

    @Autowired
    public PluginSupportDataAppenderManager(PluginAccessor pluginAccessor, PluginEventManager pluginEventManager) {
        this((PluginModuleTracker<SupportDataAppender<?>, SupportDataModuleDescriptor>)new DefaultPluginModuleTracker(pluginAccessor, pluginEventManager, SupportDataModuleDescriptor.class));
    }

    protected PluginSupportDataAppenderManager(PluginModuleTracker<SupportDataAppender<?>, SupportDataModuleDescriptor> moduleTracker) {
        this.moduleTracker = (PluginModuleTracker)Preconditions.checkNotNull(moduleTracker);
    }

    private static Predicate<SupportDataModuleDescriptor> contextClassMatches(Object context) {
        return descriptor -> {
            Class aClass = Void.class;
            if (context != null) {
                aClass = context.getClass();
            }
            return descriptor.getContextClass().isAssignableFrom(aClass);
        };
    }

    public void destroy() {
        this.moduleTracker.close();
    }

    @Override
    public void addSupportData(PropertyStore propertyStore, SupportDataDetail detail) {
        Preconditions.checkNotNull((Object)propertyStore);
        Preconditions.checkNotNull((Object)((Object)detail));
        PropertyStoreSupportDataBuilder builder = new PropertyStoreSupportDataBuilder(propertyStore, new DefaultSupportDataBuilderContext(detail));
        this.addSupportData(builder, null);
    }

    private <T> void addSupportData(SupportDataBuilder builder, T context) {
        ArrayList filteredModuleDescriptors = Lists.newArrayList((Iterable)StreamSupport.stream(this.moduleTracker.getModuleDescriptors().spliterator(), false).filter(PluginSupportDataAppenderManager.contextClassMatches(context)).collect(Collectors.toList()));
        Collections.sort(filteredModuleDescriptors);
        for (SupportDataModuleDescriptor descriptor : filteredModuleDescriptors) {
            Object appender = descriptor.getModule();
            try {
                appender.addSupportData(builder, context);
            }
            catch (Exception e) {
                LOG.warn("Problem adding support info for module '{}'", (Object)descriptor.getCompleteKey(), (Object)e);
            }
        }
    }

    private class PropertyStoreSupportDataBuilder
    implements SupportDataBuilder {
        private final int countOfContextObjects;
        private final PropertyStore propertyStore;
        private final Set<Integer> allContextObjectsIds;
        private final SupportDataBuilderContext builderContext;
        private final int currentCategoryDepth;

        private PropertyStoreSupportDataBuilder(PropertyStore propertyStore, SupportDataBuilderContext builderContext) {
            this.propertyStore = (PropertyStore)Preconditions.checkNotNull((Object)propertyStore);
            this.builderContext = (SupportDataBuilderContext)Preconditions.checkNotNull((Object)builderContext);
            this.allContextObjectsIds = new HashSet<Integer>();
            this.countOfContextObjects = 0;
            this.currentCategoryDepth = 0;
        }

        private PropertyStoreSupportDataBuilder(PropertyStoreSupportDataBuilder parentBuilder, PropertyStore propertyStore) {
            this.propertyStore = (PropertyStore)Preconditions.checkNotNull((Object)propertyStore);
            this.builderContext = parentBuilder.builderContext;
            this.allContextObjectsIds = parentBuilder.allContextObjectsIds;
            this.countOfContextObjects = parentBuilder.countOfContextObjects;
            this.currentCategoryDepth = parentBuilder.currentCategoryDepth + 1;
        }

        private PropertyStoreSupportDataBuilder(PropertyStoreSupportDataBuilder parentBuilder, Object context) {
            this.propertyStore = parentBuilder.propertyStore;
            this.builderContext = parentBuilder.builderContext;
            this.allContextObjectsIds = ImmutableSet.builder().addAll(parentBuilder.allContextObjectsIds).add((Object)System.identityHashCode(context)).build();
            this.countOfContextObjects = parentBuilder.countOfContextObjects + 1;
            this.currentCategoryDepth = parentBuilder.currentCategoryDepth;
        }

        @Override
        public SupportDataBuilderContext getBuilderContext() {
            return this.builderContext;
        }

        @Override
        public SupportDataBuilder addValue(String key, String value) {
            Validate.notBlank((CharSequence)key, (String)"empty or blank key", (Object[])new Object[0]);
            if (StringUtils.isNotEmpty((CharSequence)value)) {
                this.propertyStore.setValue(key, value);
            }
            return this;
        }

        @Override
        public SupportDataBuilder addCategory(String categoryKey) {
            Preconditions.checkState((this.currentCategoryDepth <= PluginSupportDataAppenderManager.this.maximumCategoryDepth ? 1 : 0) != 0, (Object)"Maximum number of categories reached");
            Preconditions.checkNotNull((Object)categoryKey, (Object)"categoryKey");
            return new PropertyStoreSupportDataBuilder(this, this.propertyStore.addCategory(categoryKey));
        }

        @Override
        public <T> SupportDataBuilder addContext(T context) {
            Preconditions.checkState((!this.allContextObjectsIds.contains(System.identityHashCode(context)) ? 1 : 0) != 0, (Object)"Cannot have recursive context objects");
            Preconditions.checkNotNull(context, (Object)"context");
            PropertyStoreSupportDataBuilder child = new PropertyStoreSupportDataBuilder(this, context);
            if (this.countOfContextObjects < PluginSupportDataAppenderManager.this.maximumContextObjectDepth) {
                PluginSupportDataAppenderManager.this.addSupportData(child, context);
            } else {
                LOG.warn("Maximum number of context objects reached, not calling child appenders");
            }
            return child;
        }
    }
}


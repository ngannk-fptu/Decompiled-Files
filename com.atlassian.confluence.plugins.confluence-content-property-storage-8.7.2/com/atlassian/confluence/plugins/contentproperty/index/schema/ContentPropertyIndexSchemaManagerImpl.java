/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Iterables
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.base.Joiner
 *  com.google.common.collect.ImmutableMultimap
 *  com.google.common.collect.ImmutableMultimap$Builder
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Ordering
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.contentproperty.index.schema;

import com.atlassian.confluence.plugins.contentproperty.index.config.ContentPropertyIndexSchemaModuleDescriptor;
import com.atlassian.confluence.plugins.contentproperty.index.config.OwningPluginAndModuleNamesComparator;
import com.atlassian.confluence.plugins.contentproperty.index.schema.ContentPropertyIndexSchemaManager;
import com.atlassian.confluence.plugins.contentproperty.index.schema.ContentPropertySchemaField;
import com.atlassian.fugue.Iterables;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContentPropertyIndexSchemaManagerImpl
implements ContentPropertyIndexSchemaManager {
    private static final Logger log = LoggerFactory.getLogger(ContentPropertyIndexSchemaManagerImpl.class);
    private final PluginAccessor pluginAccessor;
    private final Comparator<ContentPropertyIndexSchemaModuleDescriptor> indexSchemaModuleDescriptorComparator = new OwningPluginAndModuleNamesComparator();

    @Autowired
    public ContentPropertyIndexSchemaManagerImpl(@ComponentImport PluginAccessor pluginAccessor) {
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor);
    }

    @Override
    public Multimap<String, ContentPropertySchemaField> getIndexSchema() {
        ImmutableMultimap.Builder indexSchema = ImmutableMultimap.builder();
        for (ContentPropertyIndexSchemaModuleDescriptor contentPropertyIndexSchema : this.findAllEnabledIndexSchemaModuleDescriptors()) {
            Multimap<String, ContentPropertySchemaField> moduleSchema = contentPropertyIndexSchema.getModule().asMultimap();
            Set moduleDefinedContentPropertyKeys = moduleSchema.keySet();
            Sets.SetView newContentPropertyKeys = Sets.difference((Set)moduleDefinedContentPropertyKeys, (Set)indexSchema.build().keySet());
            if (newContentPropertyKeys.size() == moduleDefinedContentPropertyKeys.size()) {
                indexSchema.putAll(moduleSchema);
                continue;
            }
            Sets.SetView collidingKeys = Sets.difference((Set)moduleDefinedContentPropertyKeys, (Set)newContentPropertyKeys);
            ContentPropertySchemaField schemaField = this.findFirstSchemaField(moduleSchema);
            log.warn("Discarding content property index schema defined in plugin '{}', module '{}' due to its attempts to override existing configuration. Colliding keys: {}", new Object[]{schemaField.getOwningPlugin(), schemaField.getOwningModule(), this.getCollidedKeysDetailedInfo((Set<String>)collidingKeys, (Multimap<String, ContentPropertySchemaField>)indexSchema.build())});
        }
        return indexSchema.build();
    }

    private Iterable<ContentPropertyIndexSchemaModuleDescriptor> findAllEnabledIndexSchemaModuleDescriptors() {
        List enabledModuleDescriptors = this.pluginAccessor.getEnabledModuleDescriptorsByClass(ContentPropertyIndexSchemaModuleDescriptor.class);
        return Ordering.from(this.indexSchemaModuleDescriptorComparator).immutableSortedCopy((Iterable)enabledModuleDescriptors);
    }

    private ContentPropertySchemaField findFirstSchemaField(Multimap<String, ContentPropertySchemaField> moduleSchema) {
        return (ContentPropertySchemaField)Iterables.first((Iterable)moduleSchema.values()).get();
    }

    private String getCollidedKeysDetailedInfo(Set<String> collidingKeys, Multimap<String, ContentPropertySchemaField> existingSchema) {
        ArrayList<String> collidingInfo = new ArrayList<String>();
        for (String collidingKey : collidingKeys) {
            ContentPropertySchemaField existingField = (ContentPropertySchemaField)Iterables.first((Iterable)existingSchema.get((Object)collidingKey)).get();
            collidingInfo.add(String.format("'%s' - defined at plugin '%s', module '%s'", collidingKey, existingField.getOwningPlugin(), existingField.getOwningModule()));
        }
        return Joiner.on((String)"; ").join(collidingInfo);
    }
}


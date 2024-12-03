/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Iterables
 *  com.google.common.collect.ComparisonChain
 *  com.google.common.collect.Multimap
 */
package com.atlassian.confluence.plugins.contentproperty.index.config;

import com.atlassian.confluence.plugins.contentproperty.index.config.ContentPropertyIndexSchemaModuleDescriptor;
import com.atlassian.confluence.plugins.contentproperty.index.schema.ContentPropertySchemaField;
import com.atlassian.fugue.Iterables;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Multimap;
import java.util.Comparator;

public class OwningPluginAndModuleNamesComparator
implements Comparator<ContentPropertyIndexSchemaModuleDescriptor> {
    private static final String BUNDLED_PLUGINS_PACKAGE_PREFIX = "com.atlassian";

    @Override
    public int compare(ContentPropertyIndexSchemaModuleDescriptor left, ContentPropertyIndexSchemaModuleDescriptor right) {
        ContentPropertySchemaField leftSchemaField = this.findFirstSchemaField(left.getModule().asMultimap());
        ContentPropertySchemaField rightSchemaField = this.findFirstSchemaField(right.getModule().asMultimap());
        if (this.isOwningPluginPrivileged(leftSchemaField)) {
            if (this.isOwningPluginPrivileged(rightSchemaField)) {
                return this.comparePlugins(leftSchemaField, rightSchemaField);
            }
            return this.rightIsGreater();
        }
        if (this.isOwningPluginPrivileged(rightSchemaField)) {
            return this.leftIsGreater();
        }
        return this.comparePlugins(leftSchemaField, rightSchemaField);
    }

    private int comparePlugins(ContentPropertySchemaField left, ContentPropertySchemaField right) {
        return ComparisonChain.start().compare((Object)left.getOwningPlugin(), (Object)right.getOwningPlugin(), Comparator.nullsLast(Comparator.naturalOrder())).compare((Object)left.getOwningModule(), (Object)right.getOwningModule(), Comparator.nullsLast(Comparator.naturalOrder())).result();
    }

    private ContentPropertySchemaField findFirstSchemaField(Multimap<String, ContentPropertySchemaField> moduleSchema) {
        return (ContentPropertySchemaField)Iterables.first((Iterable)moduleSchema.values()).get();
    }

    private boolean isOwningPluginPrivileged(ContentPropertySchemaField schemaField) {
        return schemaField.getOwningPlugin() != null && schemaField.getOwningPlugin().startsWith(BUNDLED_PLUGINS_PACKAGE_PREFIX);
    }

    private int leftIsGreater() {
        return 1;
    }

    private int rightIsGreater() {
        return -1;
    }
}


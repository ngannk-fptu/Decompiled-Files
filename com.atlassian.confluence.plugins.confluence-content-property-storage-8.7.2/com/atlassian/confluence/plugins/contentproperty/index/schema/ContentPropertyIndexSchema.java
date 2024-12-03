/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMultimap
 *  com.google.common.collect.Multimap
 */
package com.atlassian.confluence.plugins.contentproperty.index.schema;

import com.atlassian.confluence.plugins.contentproperty.index.schema.ContentPropertySchemaField;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Set;

public class ContentPropertyIndexSchema {
    private final Multimap<String, ContentPropertySchemaField> schema;

    public ContentPropertyIndexSchema(Multimap<String, ContentPropertySchemaField> schema) {
        this.schema = ImmutableMultimap.copyOf(schema);
    }

    public Collection<ContentPropertySchemaField> getSchemaFieldsByKey(String key) {
        return this.schema.get((Object)key);
    }

    public Set<String> getContentPropertyKeys() {
        return this.schema.keySet();
    }

    public Multimap<String, ContentPropertySchemaField> asMultimap() {
        return this.schema;
    }
}


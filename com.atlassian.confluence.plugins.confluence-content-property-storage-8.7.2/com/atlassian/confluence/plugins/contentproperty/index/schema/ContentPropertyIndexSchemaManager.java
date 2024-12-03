/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimap
 */
package com.atlassian.confluence.plugins.contentproperty.index.schema;

import com.atlassian.confluence.plugins.contentproperty.index.schema.ContentPropertySchemaField;
import com.google.common.collect.Multimap;

public interface ContentPropertyIndexSchemaManager {
    public Multimap<String, ContentPropertySchemaField> getIndexSchema();
}


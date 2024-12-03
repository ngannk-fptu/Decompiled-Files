/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.JsonString
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor
 */
package com.atlassian.confluence.plugins.contentproperty.index.extractor;

import com.atlassian.confluence.api.model.JsonString;
import com.atlassian.confluence.plugins.contentproperty.index.schema.ContentPropertySchemaField;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;

public interface ContentPropertyExtractionManager {
    public Iterable<FieldDescriptor> extract(JsonString var1, Iterable<ContentPropertySchemaField> var2);
}


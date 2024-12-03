/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.index.lucene;

import com.atlassian.confluence.plugins.index.api.mapping.FieldMapping;
import com.atlassian.confluence.search.v2.FieldMappings;

public class LuceneFieldMappingWriter
implements FieldMappings.FieldMappingWriter {
    @Override
    public boolean putIfAbsent(FieldMapping mapping) {
        return false;
    }
}


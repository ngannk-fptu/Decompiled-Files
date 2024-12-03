/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.metadata.filter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.tika.config.Field;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.filter.MetadataFilter;

public class IncludeFieldMetadataFilter
extends MetadataFilter {
    private final Set<String> includeSet;

    public IncludeFieldMetadataFilter() {
        this(new HashSet<String>());
    }

    public IncludeFieldMetadataFilter(Set<String> fields) {
        this.includeSet = fields;
    }

    @Field
    public void setInclude(List<String> include) {
        this.includeSet.addAll(include);
    }

    @Override
    public void filter(Metadata metadata) throws TikaException {
        for (String n : metadata.names()) {
            if (this.includeSet.contains(n)) continue;
            metadata.remove(n);
        }
    }
}


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

public class ExcludeFieldMetadataFilter
extends MetadataFilter {
    private final Set<String> excludeSet;

    public ExcludeFieldMetadataFilter() {
        this(new HashSet<String>());
    }

    public ExcludeFieldMetadataFilter(Set<String> exclude) {
        this.excludeSet = exclude;
    }

    @Override
    public void filter(Metadata metadata) throws TikaException {
        for (String field : this.excludeSet) {
            metadata.remove(field);
        }
    }

    @Field
    public void setExclude(List<String> exclude) {
        this.excludeSet.addAll(exclude);
    }
}


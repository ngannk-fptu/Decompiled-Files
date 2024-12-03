/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.metadata.filter;

import java.util.List;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.filter.MetadataFilter;

public class CompositeMetadataFilter
extends MetadataFilter {
    private final List<MetadataFilter> filters;

    public CompositeMetadataFilter(List<MetadataFilter> filters) {
        this.filters = filters;
    }

    @Override
    public void filter(Metadata metadata) throws TikaException {
        for (MetadataFilter filter : this.filters) {
            filter.filter(metadata);
        }
    }
}


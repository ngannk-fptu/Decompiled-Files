/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.metadata.filter;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.filter.MetadataFilter;

public class NoOpFilter
implements MetadataFilter {
    public static NoOpFilter NOOP_FILTER = new NoOpFilter();

    @Override
    public void filter(Metadata metadata) throws TikaException {
    }
}


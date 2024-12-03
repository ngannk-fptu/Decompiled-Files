/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.metadata.writefilter;

import org.apache.tika.metadata.writefilter.MetadataWriteFilter;

public interface MetadataWriteFilterFactory {
    public MetadataWriteFilter newInstance();
}


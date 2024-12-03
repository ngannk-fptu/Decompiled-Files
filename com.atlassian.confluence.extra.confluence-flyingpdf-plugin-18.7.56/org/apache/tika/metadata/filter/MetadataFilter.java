/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.metadata.filter;

import java.io.Serializable;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;

public interface MetadataFilter
extends Serializable {
    public void filter(Metadata var1) throws TikaException;
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.metadata.filter;

import java.io.IOException;
import java.io.Serializable;
import org.apache.tika.config.ConfigBase;
import org.apache.tika.exception.TikaConfigException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.filter.CompositeMetadataFilter;
import org.apache.tika.metadata.filter.NoOpFilter;
import org.w3c.dom.Element;

public abstract class MetadataFilter
extends ConfigBase
implements Serializable {
    public static MetadataFilter load(Element root, boolean allowMissing) throws TikaConfigException, IOException {
        try {
            return MetadataFilter.buildComposite("metadataFilters", CompositeMetadataFilter.class, "metadataFilter", MetadataFilter.class, root);
        }
        catch (TikaConfigException e) {
            if (allowMissing && e.getMessage().contains("could not find metadataFilters")) {
                return new NoOpFilter();
            }
            throw e;
        }
    }

    public abstract void filter(Metadata var1) throws TikaException;
}


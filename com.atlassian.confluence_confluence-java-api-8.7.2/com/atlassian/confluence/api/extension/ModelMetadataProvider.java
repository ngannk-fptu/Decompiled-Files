/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicSpi
 */
package com.atlassian.confluence.api.extension;

import com.atlassian.annotations.PublicSpi;
import com.atlassian.confluence.api.extension.MetadataProperty;
import com.atlassian.confluence.api.model.Expansions;
import java.util.List;
import java.util.Map;

@PublicSpi
public interface ModelMetadataProvider {
    @Deprecated
    default public Map<String, ?> getMetadata(Object entity, Expansions expansions) {
        return null;
    }

    public Map<Object, Map<String, ?>> getMetadataForAll(Iterable<Object> var1, Expansions var2);

    @Deprecated
    public List<String> getMetadataProperties();

    default public List<MetadataProperty> getProperties() {
        return null;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.google.common.base.Supplier
 */
package com.atlassian.confluence.api.impl.service.content.factory;

import com.atlassian.confluence.api.impl.service.content.factory.Fauxpansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.core.ContentEntityObject;
import com.google.common.base.Supplier;
import java.util.Map;
import java.util.stream.Collectors;

public interface ContentMetadataFactory {
    @Deprecated
    default public Map<ContentEntityObject, Map<String, Object>> buildMetadata(Map<ContentEntityObject, Supplier<Content>> contentMap, Fauxpansions fauxpansions) {
        return this.buildMetadataForContentEntityObjects(contentMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)), fauxpansions);
    }

    public Map<ContentEntityObject, Map<String, Object>> buildMetadataForContentEntityObjects(Map<ContentEntityObject, java.util.function.Supplier<Content>> var1, Fauxpansions var2);
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.elements.ResourceLocation
 */
package com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.contenttype;

import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.contenttype.ContentTypeStrategy;

public class DefaultContentTypeStrategy
implements ContentTypeStrategy {
    private ResourceLocation resourceLocation;

    DefaultContentTypeStrategy(ResourceLocation resource) {
        this.resourceLocation = resource;
    }

    @Override
    public String getContentType() {
        return this.resourceLocation.getContentType();
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.elements.ResourceLocation
 */
package com.atlassian.plugin.webresource.impl.snapshot.resource;

import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.webresource.impl.snapshot.Context;
import com.atlassian.plugin.webresource.impl.snapshot.resource.Resource;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.contentprovider.ContentProviderStrategy;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.contenttype.ContentTypeStrategy;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.path.PathStrategy;

public class ContextResource
extends Resource {
    private final Context contextParent;

    ContextResource(Context parent, ResourceLocation resourceLocation, ContentTypeStrategy contentTypeStrategy, PathStrategy pathStrategy, ContentProviderStrategy contentProviderStrategy) {
        super(parent, resourceLocation, "js", "js", contentTypeStrategy, null, pathStrategy, contentProviderStrategy);
        this.contextParent = parent;
    }

    @Override
    public boolean isTransformable() {
        return false;
    }
}


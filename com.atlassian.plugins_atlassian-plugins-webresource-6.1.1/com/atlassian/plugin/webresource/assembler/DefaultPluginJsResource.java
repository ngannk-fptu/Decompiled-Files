/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.assembler.resource.PluginJsResource
 *  com.atlassian.webresource.api.assembler.resource.PluginJsResourceParams
 *  com.atlassian.webresource.api.assembler.resource.ResourcePhase
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.assembler;

import com.atlassian.plugin.webresource.ResourceUrl;
import com.atlassian.plugin.webresource.assembler.DefaultPluginJsResourceParams;
import com.atlassian.plugin.webresource.assembler.DefaultPluginUrlResource;
import com.atlassian.webresource.api.assembler.resource.PluginJsResource;
import com.atlassian.webresource.api.assembler.resource.PluginJsResourceParams;
import com.atlassian.webresource.api.assembler.resource.ResourcePhase;
import javax.annotation.Nonnull;

public class DefaultPluginJsResource
extends DefaultPluginUrlResource<PluginJsResourceParams>
implements PluginJsResource {
    public DefaultPluginJsResource(@Nonnull ResourceUrl resourceUrl) {
        super(resourceUrl);
    }

    public DefaultPluginJsResource(@Nonnull ResourceUrl resourceUrl, @Nonnull ResourcePhase resourcePhase) {
        super(resourceUrl, resourcePhase);
    }

    public PluginJsResourceParams getParams() {
        return new DefaultPluginJsResourceParams(this.resourceUrl.getParams(), this.resourceUrl.getKey(), this.resourceUrl.getBatchType());
    }
}


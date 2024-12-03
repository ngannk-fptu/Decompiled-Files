/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.assembler.resource.PluginCssResource
 *  com.atlassian.webresource.api.assembler.resource.PluginCssResourceParams
 *  com.atlassian.webresource.api.assembler.resource.ResourcePhase
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.assembler;

import com.atlassian.plugin.webresource.ResourceUrl;
import com.atlassian.plugin.webresource.assembler.DefaultPluginCssResourceParams;
import com.atlassian.plugin.webresource.assembler.DefaultPluginUrlResource;
import com.atlassian.webresource.api.assembler.resource.PluginCssResource;
import com.atlassian.webresource.api.assembler.resource.PluginCssResourceParams;
import com.atlassian.webresource.api.assembler.resource.ResourcePhase;
import javax.annotation.Nonnull;

public class DefaultPluginCssResource
extends DefaultPluginUrlResource<PluginCssResourceParams>
implements PluginCssResource {
    public DefaultPluginCssResource(@Nonnull ResourceUrl resourceUrl) {
        super(resourceUrl);
    }

    public DefaultPluginCssResource(@Nonnull ResourceUrl resourceUrl, @Nonnull ResourcePhase resourcePhase) {
        super(resourceUrl, resourcePhase);
    }

    public PluginCssResourceParams getParams() {
        return new DefaultPluginCssResourceParams(this.resourceUrl.getParams(), this.resourceUrl.getKey(), this.resourceUrl.getBatchType());
    }
}


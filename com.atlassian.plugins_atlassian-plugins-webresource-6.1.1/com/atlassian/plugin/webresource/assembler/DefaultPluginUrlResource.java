/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.UrlMode
 *  com.atlassian.webresource.api.assembler.resource.PluginUrlResource
 *  com.atlassian.webresource.api.assembler.resource.PluginUrlResource$BatchType
 *  com.atlassian.webresource.api.assembler.resource.PluginUrlResourceParams
 *  com.atlassian.webresource.api.assembler.resource.PrebakeError
 *  com.atlassian.webresource.api.assembler.resource.ResourcePhase
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.assembler;

import com.atlassian.plugin.webresource.ResourceUrl;
import com.atlassian.webresource.api.UrlMode;
import com.atlassian.webresource.api.assembler.resource.PluginUrlResource;
import com.atlassian.webresource.api.assembler.resource.PluginUrlResourceParams;
import com.atlassian.webresource.api.assembler.resource.PrebakeError;
import com.atlassian.webresource.api.assembler.resource.ResourcePhase;
import java.util.List;
import javax.annotation.Nonnull;

abstract class DefaultPluginUrlResource<T extends PluginUrlResourceParams>
implements PluginUrlResource<T> {
    protected final ResourceUrl resourceUrl;
    private final ResourcePhase resourcePhase;

    DefaultPluginUrlResource(@Nonnull ResourceUrl resourceUrl, @Nonnull ResourcePhase resourcePhase) {
        this.resourceUrl = resourceUrl;
        this.resourcePhase = resourcePhase;
    }

    DefaultPluginUrlResource(@Nonnull ResourceUrl resourceUrl) {
        this(resourceUrl, ResourcePhase.defaultPhase());
    }

    public String getStaticUrl(UrlMode urlMode) {
        return this.resourceUrl.getUrl(urlMode == UrlMode.ABSOLUTE);
    }

    public boolean isTainted() {
        return this.resourceUrl.isTainted();
    }

    public List<PrebakeError> getPrebakeErrors() {
        return this.resourceUrl.getPrebakeErrors();
    }

    public String toString() {
        return this.resourceUrl.getKey() + ':' + this.resourceUrl.getName();
    }

    public String getKey() {
        return this.resourceUrl.getKey();
    }

    public PluginUrlResource.BatchType getBatchType() {
        return this.resourceUrl.getBatchType();
    }

    @Nonnull
    public ResourcePhase getResourcePhase() {
        return this.resourcePhase;
    }
}


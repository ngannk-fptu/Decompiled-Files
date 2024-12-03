/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.assembler.resource.PluginUrlResource$BatchType
 *  com.atlassian.webresource.api.assembler.resource.PrebakeError
 */
package com.atlassian.plugin.webresource;

import com.atlassian.plugin.webresource.impl.RequestCache;
import com.atlassian.plugin.webresource.impl.snapshot.resource.Resource;
import com.atlassian.webresource.api.assembler.resource.PluginUrlResource;
import com.atlassian.webresource.api.assembler.resource.PrebakeError;
import java.util.List;
import java.util.Map;

public abstract class ResourceUrl {
    private final List<PrebakeError> prebakeErrors;

    public ResourceUrl(List<PrebakeError> prebakeErrors) {
        this.prebakeErrors = prebakeErrors;
    }

    public abstract String getName();

    public abstract String getKey();

    public abstract String getType();

    public abstract String getUrl(boolean var1);

    public abstract Map<String, String> getParams();

    public boolean isTainted() {
        return !this.prebakeErrors.isEmpty();
    }

    public List<PrebakeError> getPrebakeErrors() {
        return this.prebakeErrors;
    }

    public abstract PluginUrlResource.BatchType getBatchType();

    public abstract List<Resource> getResources(RequestCache var1);
}


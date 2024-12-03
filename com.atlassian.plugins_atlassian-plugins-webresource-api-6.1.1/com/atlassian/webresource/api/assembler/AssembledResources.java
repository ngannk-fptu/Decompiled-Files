/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.webresource.api.assembler;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.webresource.api.assembler.WebResourceSet;

@ExperimentalApi
public interface AssembledResources {
    public WebResourceSet drainIncludedResources();

    public WebResourceSet pollIncludedResources();

    public WebResourceSet peek();

    @Deprecated
    default public WebResourceSet drainIncludedSyncResources() {
        return this.drainIncludedResources();
    }
}


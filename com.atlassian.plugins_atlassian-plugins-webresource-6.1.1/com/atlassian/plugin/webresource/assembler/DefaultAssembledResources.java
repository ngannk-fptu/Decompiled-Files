/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.assembler.AssembledResources
 *  com.atlassian.webresource.api.assembler.WebResourceSet
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.assembler;

import com.atlassian.plugin.webresource.assembler.DefaultWebResourceSetBuilder;
import com.atlassian.plugin.webresource.impl.RequestState;
import com.atlassian.webresource.api.assembler.AssembledResources;
import com.atlassian.webresource.api.assembler.WebResourceSet;
import javax.annotation.Nonnull;

class DefaultAssembledResources
implements AssembledResources {
    private final DefaultWebResourceSetBuilder builder;

    DefaultAssembledResources(@Nonnull RequestState requestState) {
        this.builder = new DefaultWebResourceSetBuilder(requestState);
    }

    @Nonnull
    public WebResourceSet drainIncludedResources() {
        return this.builder.enableDrainBigPipe().disableBlockOnBigPipe().enableCleanUpAfterInclude().enableAdditionOfWebResourceJavascriptApiDependencies().enableSuperbatch().build();
    }

    public WebResourceSet drainIncludedSyncResources() {
        return this.builder.enableDrainBigPipe().disableBlockOnBigPipe().enableCleanUpAfterInclude().disableAdditionOfWebResourceJavascriptApiDependencies().disableSuperbatch().build();
    }

    @Nonnull
    public WebResourceSet pollIncludedResources() {
        return this.builder.enableDrainBigPipe().enableBlockOnBigPipe().enableCleanUpAfterInclude().disableAdditionOfWebResourceJavascriptApiDependencies().enableSuperbatch().build();
    }

    @Nonnull
    public WebResourceSet peek() {
        return this.builder.disableDrainBigPipe().disableBlockOnBigPipe().disableCleanUpAfterInclude().disableAdditionOfWebResourceJavascriptApiDependencies().enableSuperbatch().build();
    }
}


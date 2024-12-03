/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.assembler.AssembledResources
 *  com.atlassian.webresource.api.assembler.RequiredData
 *  com.atlassian.webresource.api.assembler.RequiredResources
 *  com.atlassian.webresource.api.assembler.WebResourceAssembler
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.assembler;

import com.atlassian.plugin.webresource.assembler.DefaultAssembledResources;
import com.atlassian.plugin.webresource.assembler.DefaultRequiredData;
import com.atlassian.plugin.webresource.assembler.DefaultRequiredResources;
import com.atlassian.plugin.webresource.impl.Globals;
import com.atlassian.plugin.webresource.impl.RequestState;
import com.atlassian.plugin.webresource.prebake.PrebakeWebResourceAssembler;
import com.atlassian.webresource.api.assembler.AssembledResources;
import com.atlassian.webresource.api.assembler.RequiredData;
import com.atlassian.webresource.api.assembler.RequiredResources;
import com.atlassian.webresource.api.assembler.WebResourceAssembler;
import java.util.Objects;
import javax.annotation.Nonnull;

class DefaultWebResourceAssembler
implements PrebakeWebResourceAssembler {
    private final AssembledResources assembledResources;
    private final Globals globals;
    private final RequestState requestState;
    private final RequiredData requiredData;
    private final RequiredResources requiredResources;

    DefaultWebResourceAssembler(@Nonnull RequestState requestState, @Nonnull Globals globals) {
        this.globals = Objects.requireNonNull(globals, "The globals is mandatory to build the web resource assembler.");
        this.requestState = Objects.requireNonNull(requestState, "The request state is mandatory to build the web resource assembler.");
        this.requiredData = new DefaultRequiredData(requestState);
        this.requiredResources = new DefaultRequiredResources(requestState);
        this.assembledResources = new DefaultAssembledResources(requestState);
    }

    @Nonnull
    public AssembledResources assembled() {
        return this.assembledResources;
    }

    @Nonnull
    public RequiredResources resources() {
        return this.requiredResources;
    }

    @Nonnull
    public RequiredData data() {
        return this.requiredData;
    }

    @Nonnull
    public WebResourceAssembler copy() {
        return new DefaultWebResourceAssembler(this.requestState.deepClone(), this.globals);
    }
}


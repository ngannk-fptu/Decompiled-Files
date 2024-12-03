/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.webresource.api.assembler;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.webresource.api.assembler.WebResourceAssembler;
import java.util.concurrent.TimeUnit;

@ExperimentalApi
public interface WebResourceAssemblerBuilder {
    @Deprecated
    public WebResourceAssemblerBuilder includeSuperbatchResources(boolean var1);

    public WebResourceAssemblerBuilder includeSyncbatchResources(boolean var1);

    public WebResourceAssemblerBuilder autoIncludeFrontendRuntime(boolean var1);

    public WebResourceAssemblerBuilder asyncDataDeadline(long var1, TimeUnit var3);

    public WebResourceAssembler build();
}


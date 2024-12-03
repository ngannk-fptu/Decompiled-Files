/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.webresource.api.assembler;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.webresource.api.assembler.WebResourceAssemblerBuilder;

@ExperimentalApi
public interface WebResourceAssemblerFactory {
    public WebResourceAssemblerBuilder create();

    @Deprecated
    public void clearCache();
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.webresource.api.assembler;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.webresource.api.assembler.WebResourceAssembler;

@ExperimentalApi
public interface PageBuilderService {
    public WebResourceAssembler assembler();

    public void seed(WebResourceAssembler var1);
}


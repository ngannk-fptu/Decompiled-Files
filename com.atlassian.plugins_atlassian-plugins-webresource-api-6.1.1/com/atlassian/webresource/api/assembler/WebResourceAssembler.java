/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.webresource.api.assembler;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.webresource.api.assembler.AssembledResources;
import com.atlassian.webresource.api.assembler.RequiredData;
import com.atlassian.webresource.api.assembler.RequiredResources;

@ExperimentalApi
public interface WebResourceAssembler {
    public AssembledResources assembled();

    public RequiredResources resources();

    public RequiredData data();

    public WebResourceAssembler copy();
}


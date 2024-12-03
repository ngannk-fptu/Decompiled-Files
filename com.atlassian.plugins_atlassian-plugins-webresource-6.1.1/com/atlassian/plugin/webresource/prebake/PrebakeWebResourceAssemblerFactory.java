/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.assembler.WebResourceAssemblerFactory
 *  com.atlassian.webresource.api.prebake.Dimensions
 */
package com.atlassian.plugin.webresource.prebake;

import com.atlassian.plugin.webresource.impl.snapshot.Bundle;
import com.atlassian.plugin.webresource.prebake.PrebakeWebResourceAssemblerBuilder;
import com.atlassian.webresource.api.assembler.WebResourceAssemblerFactory;
import com.atlassian.webresource.api.prebake.Dimensions;

public interface PrebakeWebResourceAssemblerFactory
extends WebResourceAssemblerFactory {
    public PrebakeWebResourceAssemblerBuilder create();

    public Dimensions computeDimensions();

    public Dimensions computeBundleDimensions(Bundle var1);

    public String computeGlobalStateHash();
}


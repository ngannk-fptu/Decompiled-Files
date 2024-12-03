/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.assembler.WebResourceAssemblerBuilder
 *  com.atlassian.webresource.api.prebake.Coordinate
 */
package com.atlassian.plugin.webresource.prebake;

import com.atlassian.plugin.webresource.prebake.PrebakeWebResourceAssembler;
import com.atlassian.webresource.api.assembler.WebResourceAssemblerBuilder;
import com.atlassian.webresource.api.prebake.Coordinate;

public interface PrebakeWebResourceAssemblerBuilder
extends WebResourceAssemblerBuilder {
    public PrebakeWebResourceAssemblerBuilder withCoordinate(Coordinate var1);

    public PrebakeWebResourceAssembler build();
}


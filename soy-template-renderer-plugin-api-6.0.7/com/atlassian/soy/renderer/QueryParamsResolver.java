/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.plugin.webresource.QueryParams
 *  com.google.common.base.Supplier
 *  javax.annotation.Nonnull
 */
package com.atlassian.soy.renderer;

import com.atlassian.annotations.PublicApi;
import com.atlassian.plugin.webresource.QueryParams;
import com.google.common.base.Supplier;
import javax.annotation.Nonnull;

@PublicApi
public interface QueryParamsResolver
extends Supplier<QueryParams> {
    @Nonnull
    public QueryParams get();
}


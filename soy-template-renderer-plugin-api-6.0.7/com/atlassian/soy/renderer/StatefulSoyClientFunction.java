/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 *  com.atlassian.soy.renderer.SoyClientFunction
 *  com.atlassian.webresource.api.prebake.DimensionAwareTransformerUrlBuilder
 *  com.atlassian.webresource.api.prebake.Dimensions
 */
package com.atlassian.soy.renderer;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import com.atlassian.soy.renderer.SoyClientFunction;
import com.atlassian.webresource.api.prebake.DimensionAwareTransformerUrlBuilder;
import com.atlassian.webresource.api.prebake.Dimensions;

@TenantAware(value=TenancyScope.TENANTLESS)
public interface StatefulSoyClientFunction
extends DimensionAwareTransformerUrlBuilder,
SoyClientFunction {
    public Dimensions computeDimensions();
}


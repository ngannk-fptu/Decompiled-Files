/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jersey.spi.container.ContainerRequest
 *  com.sun.jersey.spi.container.ContainerRequestFilter
 *  com.sun.jersey.spi.container.ContainerResponse
 *  com.sun.jersey.spi.container.ContainerResponseFilter
 *  com.sun.jersey.spi.container.ResourceFilter
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.rest.filter;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b&\u0018\u00002\u00020\u00012\u00020\u00022\u00020\u0003B\u0005\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006H&J\u0018\u0010\u0005\u001a\u00020\b2\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\bH\u0016J\b\u0010\n\u001a\u00020\u0000H\u0016J\b\u0010\u000b\u001a\u00020\u0000H\u0016\u00a8\u0006\f"}, d2={"Lcom/addonengine/addons/analytics/rest/filter/PrerequestFilter;", "Lcom/sun/jersey/spi/container/ResourceFilter;", "Lcom/sun/jersey/spi/container/ContainerRequestFilter;", "Lcom/sun/jersey/spi/container/ContainerResponseFilter;", "()V", "filter", "Lcom/sun/jersey/spi/container/ContainerRequest;", "containerRequest", "Lcom/sun/jersey/spi/container/ContainerResponse;", "containerResponse", "getRequestFilter", "getResponseFilter", "analytics"})
public abstract class PrerequestFilter
implements ResourceFilter,
ContainerRequestFilter,
ContainerResponseFilter {
    @NotNull
    public abstract ContainerRequest filter(@NotNull ContainerRequest var1);

    @NotNull
    public ContainerResponse filter(@NotNull ContainerRequest containerRequest, @NotNull ContainerResponse containerResponse) {
        Intrinsics.checkNotNullParameter((Object)containerRequest, (String)"containerRequest");
        Intrinsics.checkNotNullParameter((Object)containerResponse, (String)"containerResponse");
        return containerResponse;
    }

    @NotNull
    public PrerequestFilter getResponseFilter() {
        return this;
    }

    @NotNull
    public PrerequestFilter getRequestFilter() {
        return this;
    }
}


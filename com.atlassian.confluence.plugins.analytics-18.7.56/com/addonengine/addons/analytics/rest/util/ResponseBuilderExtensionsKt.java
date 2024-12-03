/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.Response$ResponseBuilder
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.rest.util;

import javax.ws.rs.core.Response;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=2, xi=48, d1={"\u0000\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\u001a\u0012\u0010\u0000\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003\u00a8\u0006\u0004"}, d2={"contentDispositionForAttachment", "Ljavax/ws/rs/core/Response$ResponseBuilder;", "fileName", "", "analytics"})
public final class ResponseBuilderExtensionsKt {
    @NotNull
    public static final Response.ResponseBuilder contentDispositionForAttachment(@NotNull Response.ResponseBuilder $this$contentDispositionForAttachment, @NotNull String fileName) {
        Intrinsics.checkNotNullParameter((Object)$this$contentDispositionForAttachment, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)fileName, (String)"fileName");
        Response.ResponseBuilder responseBuilder = $this$contentDispositionForAttachment.header("content-disposition", (Object)("attachment; filename = " + fileName));
        Intrinsics.checkNotNullExpressionValue((Object)responseBuilder, (String)"header(...)");
        return responseBuilder;
    }
}


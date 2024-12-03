/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  javax.inject.Named
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.addonengine.addons.analytics.rest.util.excel;

import com.addonengine.addons.analytics.service.confluence.UrlBuilder;
import java.net.URI;
import java.net.URL;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
@Named
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0003\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00060\u0001j\u0002`\u00022\b\u0012\u0004\u0012\u00020\u00000\u0003B\u0007\b\u0016\u00a2\u0006\u0002\u0010\u0004B\u000f\b\u0016\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\u0002\u0010\u0007J\u0010\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0000H\u0016R\u0016\u0010\b\u001a\n \n*\u0004\u0018\u00010\t0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001e\u0010\u000b\u001a\u00020\f8\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010\u00a8\u0006\u0014"}, d2={"Lcom/addonengine/addons/analytics/rest/util/excel/ExcelGenerationException;", "Ljava/lang/Exception;", "Lkotlin/Exception;", "Ljavax/ws/rs/ext/ExceptionMapper;", "()V", "cause", "", "(Ljava/lang/Throwable;)V", "log", "Lorg/slf4j/Logger;", "kotlin.jvm.PlatformType", "urlBuilder", "Lcom/addonengine/addons/analytics/service/confluence/UrlBuilder;", "getUrlBuilder", "()Lcom/addonengine/addons/analytics/service/confluence/UrlBuilder;", "setUrlBuilder", "(Lcom/addonengine/addons/analytics/service/confluence/UrlBuilder;)V", "toResponse", "Ljavax/ws/rs/core/Response;", "exception", "analytics"})
public final class ExcelGenerationException
extends Exception
implements ExceptionMapper<ExcelGenerationException> {
    private final Logger log;
    @Inject
    public UrlBuilder urlBuilder;

    public ExcelGenerationException() {
        this.log = LoggerFactory.getLogger(this.getClass());
    }

    public ExcelGenerationException(@NotNull Throwable cause) {
        Intrinsics.checkNotNullParameter((Object)cause, (String)"cause");
        super(cause);
        this.log = LoggerFactory.getLogger(this.getClass());
    }

    @NotNull
    public final UrlBuilder getUrlBuilder() {
        UrlBuilder urlBuilder = this.urlBuilder;
        if (urlBuilder != null) {
            return urlBuilder;
        }
        Intrinsics.throwUninitializedPropertyAccessException((String)"urlBuilder");
        return null;
    }

    public final void setUrlBuilder(@NotNull UrlBuilder urlBuilder) {
        Intrinsics.checkNotNullParameter((Object)urlBuilder, (String)"<set-?>");
        this.urlBuilder = urlBuilder;
    }

    @NotNull
    public Response toResponse(@NotNull ExcelGenerationException exception) {
        Intrinsics.checkNotNullParameter((Object)exception, (String)"exception");
        this.log.error(exception.toString(), (Throwable)exception);
        URL url = this.getUrlBuilder().buildHostActionUrl("servererror");
        Response response = Response.temporaryRedirect((URI)url.toURI()).build();
        Intrinsics.checkNotNullExpressionValue((Object)response, (String)"build(...)");
        return response;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.ws.rs.core.CacheControl
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 */
package com.atlassian.oauth2.client.rest.resource.exception;

import com.atlassian.oauth2.client.rest.api.RestErrorCollection;
import com.atlassian.oauth2.common.rest.validator.ErrorCollection;
import javax.annotation.Nonnull;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public abstract class AbstractExceptionMapper<T extends Throwable>
implements ExceptionMapper<T> {
    private String getMediaType() {
        return "application/json";
    }

    private CacheControl getCacheControl() {
        CacheControl cacheControl = new CacheControl();
        cacheControl.setNoStore(true);
        cacheControl.setNoCache(true);
        return cacheControl;
    }

    @Nonnull
    protected Response response(@Nonnull Response.Status status, @Nonnull ErrorCollection errors) {
        return Response.status((Response.Status)status).entity((Object)new RestErrorCollection(errors)).type(this.getMediaType()).cacheControl(this.getCacheControl()).build();
    }

    protected Response response(@Nonnull Response.Status status, @Nonnull String errorMessage) {
        return this.response(status, ErrorCollection.forMessage(errorMessage));
    }
}


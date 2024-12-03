/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.upm.rest;

import com.atlassian.marketplace.client.MpacException;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.apache.commons.lang3.StringUtils;

@Provider
public final class MpacExceptionMapper
implements ExceptionMapper<MpacException> {
    private final UpmRepresentationFactory representationFactory;

    public MpacExceptionMapper(UpmRepresentationFactory representationFactory) {
        this.representationFactory = representationFactory;
    }

    public Response toResponse(MpacException exception) {
        int statusCode;
        String message = exception.getMessage();
        int n = statusCode = !StringUtils.isBlank((CharSequence)message) && StringUtils.isNumeric((CharSequence)message) ? Integer.parseInt(message) : 502;
        if (exception instanceof MpacException.ServerError) {
            statusCode = ((MpacException.ServerError)exception).getStatus();
        }
        return Response.status((int)(statusCode == 500 ? 502 : statusCode)).entity((Object)this.representationFactory.createI18nErrorRepresentation("upm.pac.connection.error")).type("application/vnd.atl.plugins.error+json").build();
    }
}


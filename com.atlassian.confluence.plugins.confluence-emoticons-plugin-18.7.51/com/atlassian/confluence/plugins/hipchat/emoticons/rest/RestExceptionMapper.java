/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.rest.api.model.ExceptionConverter$Server
 *  com.atlassian.confluence.rest.api.model.RestError
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.rest;

import com.atlassian.confluence.plugins.hipchat.emoticons.exception.EmoticonGenerateThumbnailException;
import com.atlassian.confluence.plugins.hipchat.emoticons.exception.EmoticonValidationException;
import com.atlassian.confluence.rest.api.model.ExceptionConverter;
import com.atlassian.confluence.rest.api.model.RestError;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RestExceptionMapper
implements ExceptionMapper<Exception> {
    public final Response toResponse(Exception exception) {
        if (exception instanceof WebApplicationException) {
            WebApplicationException web = (WebApplicationException)exception;
            return web.getResponse();
        }
        if (exception instanceof EmoticonValidationException) {
            return Response.status((Response.Status)Response.Status.CONFLICT).entity((Object)exception.getMessage()).build();
        }
        if (exception instanceof EmoticonGenerateThumbnailException) {
            return Response.status((Response.Status)Response.Status.UNSUPPORTED_MEDIA_TYPE).entity((Object)"EMOTICON_GENERATE_THUMBNAIL_EXCEPTION").build();
        }
        RestError errorBean = ExceptionConverter.Server.convertServiceException((Exception)exception);
        return Response.status((int)errorBean.getStatusCode()).type(MediaType.APPLICATION_JSON_TYPE).entity((Object)errorBean).build();
    }
}


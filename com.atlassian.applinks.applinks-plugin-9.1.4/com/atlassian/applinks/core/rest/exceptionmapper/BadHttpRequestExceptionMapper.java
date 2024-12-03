/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.applinks.core.rest.exceptionmapper;

import com.atlassian.applinks.core.rest.util.BadHttpRequestException;
import com.atlassian.applinks.core.rest.util.RestUtil;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class BadHttpRequestExceptionMapper
implements ExceptionMapper<BadHttpRequestException> {
    public Response toResponse(BadHttpRequestException exception) {
        return RestUtil.badRequest(exception.getMessage());
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.CacheControl
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.ext.ExceptionMapper
 */
package com.atlassian.troubleshooting.stp.rest;

import com.atlassian.troubleshooting.stp.security.AuthenticationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class SecurityExceptionMapper
implements ExceptionMapper<SecurityException> {
    private static CacheControl never() {
        CacheControl cacheNever = new CacheControl();
        cacheNever.setNoStore(true);
        cacheNever.setNoCache(true);
        return cacheNever;
    }

    public Response toResponse(SecurityException exception) {
        if (exception instanceof AuthenticationException) {
            return Response.status((int)401).cacheControl(SecurityExceptionMapper.never()).build();
        }
        return Response.status((int)403).cacheControl(SecurityExceptionMapper.never()).entity((Object)exception.getMessage()).build();
    }
}


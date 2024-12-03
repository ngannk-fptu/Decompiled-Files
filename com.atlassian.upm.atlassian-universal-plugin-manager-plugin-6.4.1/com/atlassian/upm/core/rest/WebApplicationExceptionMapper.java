/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.upm.core.rest;

import java.io.PrintWriter;
import java.io.StringWriter;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class WebApplicationExceptionMapper
implements ExceptionMapper<WebApplicationException> {
    public Response toResponse(WebApplicationException exception) {
        Response r = exception.getResponse();
        if (r.getStatus() >= 500 && r.getEntity() == null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exception.printStackTrace(pw);
            pw.flush();
            r = Response.status((int)r.getStatus()).entity((Object)sw.toString()).type("text/plain").build();
        }
        return r;
    }
}


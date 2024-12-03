/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 *  org.codehaus.jackson.map.JsonMappingException
 */
package com.atlassian.analytics.client.browser;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.codehaus.jackson.map.JsonMappingException;

@Provider
public class JsonExceptionMapper
implements ExceptionMapper<JsonMappingException> {
    public Response toResponse(JsonMappingException ex) {
        String errorMessage = "The analytics event passed through is in an invalid format.\nDetailed error message:\n" + ex.getMessage();
        return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)errorMessage).type("text/plain").build();
    }
}


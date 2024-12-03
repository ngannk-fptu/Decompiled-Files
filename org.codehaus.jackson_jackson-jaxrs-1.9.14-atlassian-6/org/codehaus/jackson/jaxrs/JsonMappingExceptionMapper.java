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
package org.codehaus.jackson.jaxrs;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.codehaus.jackson.map.JsonMappingException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Provider
public class JsonMappingExceptionMapper
implements ExceptionMapper<JsonMappingException> {
    public Response toResponse(JsonMappingException exception) {
        return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)exception.getMessage()).type("text/plain").build();
    }
}


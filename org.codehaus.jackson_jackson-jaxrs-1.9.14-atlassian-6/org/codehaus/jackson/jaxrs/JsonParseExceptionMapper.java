/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 *  org.codehaus.jackson.JsonParseException
 */
package org.codehaus.jackson.jaxrs;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.codehaus.jackson.JsonParseException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Provider
public class JsonParseExceptionMapper
implements ExceptionMapper<JsonParseException> {
    public Response toResponse(JsonParseException exception) {
        return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)exception.getMessage()).type("text/plain").build();
    }
}


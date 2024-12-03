/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.audit.rest.v1.validation.mapper;

import com.atlassian.audit.csv.LicenseException;
import com.atlassian.audit.rest.model.ResponseErrorJson;
import java.time.Instant;
import java.util.Collections;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class LicenseExceptionMapper
implements ExceptionMapper<LicenseException> {
    public Response toResponse(LicenseException e) {
        Response.Status status = Response.Status.FORBIDDEN;
        return Response.status((Response.Status)status).entity((Object)new ResponseErrorJson(status.getStatusCode(), e.getMessage(), Collections.emptyList(), Instant.now().toString())).build();
    }
}


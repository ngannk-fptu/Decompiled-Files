/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.validator.ValidationError
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.crowd.plugin.rest.exception.mapper;

import com.atlassian.crowd.plugin.rest.exception.ValidationFailedException;
import com.atlassian.crowd.plugin.rest.exception.entity.ValidationErrorsEntity;
import com.atlassian.crowd.validator.ValidationError;
import java.util.List;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ValidationFailedExceptionMapper
implements ExceptionMapper<ValidationFailedException> {
    public Response toResponse(ValidationFailedException e) {
        List<ValidationError> errorMessages = e.getErrorMessages();
        ValidationErrorsEntity responseEntity = new ValidationErrorsEntity(errorMessages);
        return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)responseEntity).build();
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.business.insights.core.rest.exception.mapper;

import com.atlassian.business.insights.core.rest.exception.BadRequestException;
import com.atlassian.business.insights.core.rest.exception.mapper.DiagnosticDescriptionTranslator;
import com.atlassian.business.insights.core.rest.model.ErrorStatusResponse;
import com.atlassian.business.insights.core.rest.validation.DiagnosticDescription;
import java.time.Instant;
import java.util.List;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class BadRequestExceptionMapper
implements ExceptionMapper<BadRequestException> {
    private final DiagnosticDescriptionTranslator descriptionTranslator;

    public BadRequestExceptionMapper(DiagnosticDescriptionTranslator descriptionTranslator) {
        this.descriptionTranslator = descriptionTranslator;
    }

    public Response toResponse(BadRequestException e) {
        List<DiagnosticDescription> translatedErrors = this.descriptionTranslator.translateValidationErrors(e.getValidationResult().getErrors());
        return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)new ErrorStatusResponse(Response.Status.BAD_REQUEST.getStatusCode(), e.getMessage(), translatedErrors, Instant.now().toString())).build();
    }
}


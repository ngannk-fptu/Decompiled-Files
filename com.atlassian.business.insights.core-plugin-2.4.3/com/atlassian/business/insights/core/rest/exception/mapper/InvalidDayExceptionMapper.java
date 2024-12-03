/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.collect.ImmutableList
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.business.insights.core.rest.exception.mapper;

import com.atlassian.business.insights.core.rest.exception.InvalidDayException;
import com.atlassian.business.insights.core.rest.model.ErrorStatusResponse;
import com.atlassian.business.insights.core.rest.validation.DiagnosticDescription;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.collect.ImmutableList;
import java.time.Instant;
import java.util.List;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InvalidDayExceptionMapper
implements ExceptionMapper<InvalidDayException> {
    private final I18nResolver i18nResolver;

    public InvalidDayExceptionMapper(I18nResolver i18nResolver) {
        this.i18nResolver = i18nResolver;
    }

    public Response toResponse(InvalidDayException e) {
        ImmutableList diagnosticDescriptions = ImmutableList.of((Object)new DiagnosticDescription("data-pipeline.api.rest.request.body.config.days.of.week.invalid", this.i18nResolver.getText("data-pipeline.api.rest.request.body.config.days.of.week.invalid")));
        return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)new ErrorStatusResponse(Response.Status.BAD_REQUEST.getStatusCode(), "Invalid request body", (List<DiagnosticDescription>)diagnosticDescriptions, Instant.now().toString())).build();
    }
}


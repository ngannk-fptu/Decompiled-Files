/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.permission.AuthorisationException
 *  com.google.common.collect.ImmutableMap
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.annotation.Nonnull
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.ratelimiting.rest.resource;

import com.atlassian.ratelimiting.exceptions.ExemptionsLimitExceededException;
import com.atlassian.ratelimiting.rest.exception.InvalidDateStringFormatException;
import com.atlassian.ratelimiting.rest.exception.InvalidSortException;
import com.atlassian.ratelimiting.rest.exception.UserNotFoundException;
import com.atlassian.sal.api.permission.AuthorisationException;
import com.google.common.collect.ImmutableMap;
import com.sun.jersey.spi.resource.Singleton;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
@Singleton
public class RateLimitingExceptionMapper
implements ExceptionMapper<Exception> {
    private static final Logger logger = LoggerFactory.getLogger(RateLimitingExceptionMapper.class);
    private static final String FIELD_ERROR = "error";
    private static final String EXCEPTION_UNAUTHORIZED_REQUESTING_USER = "Requesting user is unauthorized to perform this operation";
    private static final String EXCEPTION_GENERAL_EXCEPTION = "The server could not perform this operation";

    @Nonnull
    public Response toResponse(@Nonnull Exception exception) {
        logger.debug("Got exception: [{}] with message: [{}]", (Object)exception.getClass().getSimpleName(), (Object)exception.getMessage());
        if (exception instanceof AuthorisationException) {
            return this.handleUnauthorized((AuthorisationException)exception);
        }
        if (exception instanceof IllegalArgumentException) {
            return this.handleBadRequest((IllegalArgumentException)exception);
        }
        if (exception instanceof IllegalStateException) {
            return this.handleBadRequest((IllegalStateException)exception);
        }
        if (exception instanceof UserNotFoundException) {
            return this.handleUserNotFound((UserNotFoundException)exception);
        }
        if (exception instanceof InvalidSortException) {
            return this.handleUserInvalidSort((InvalidSortException)exception);
        }
        if (exception instanceof InvalidDateStringFormatException) {
            return this.handleInvalidDateFormat((InvalidDateStringFormatException)exception);
        }
        if (exception instanceof ExemptionsLimitExceededException) {
            return this.handleTooManyExemptions((ExemptionsLimitExceededException)exception);
        }
        return this.handleGeneralException(exception);
    }

    private Response handleBadRequest(IllegalArgumentException exception) {
        return this.createErrorResponse(Response.Status.BAD_REQUEST, exception);
    }

    private Response handleBadRequest(IllegalStateException exception) {
        return this.createErrorResponse(Response.Status.BAD_REQUEST, exception);
    }

    private Response handleUserNotFound(UserNotFoundException exception) {
        return this.createErrorResponse(Response.Status.NOT_FOUND, exception);
    }

    private Response handleUserInvalidSort(InvalidSortException exception) {
        return this.createErrorResponse(Response.Status.BAD_REQUEST, exception);
    }

    private Response handleInvalidDateFormat(InvalidDateStringFormatException exception) {
        return this.createErrorResponse(Response.Status.BAD_REQUEST, exception);
    }

    private Response createErrorResponse(Response.Status status, Exception exception) {
        return Response.status((Response.Status)status).entity((Object)ImmutableMap.of((Object)FIELD_ERROR, (Object)exception.getLocalizedMessage())).type("application/json;charset=UTF-8").build();
    }

    private Response handleUnauthorized(AuthorisationException exception) {
        return Response.status((Response.Status)Response.Status.UNAUTHORIZED).entity(this.constructErrorMap((Exception)exception, EXCEPTION_UNAUTHORIZED_REQUESTING_USER)).type("application/json;charset=UTF-8").build();
    }

    private ImmutableMap<String, String> constructErrorMap(Exception exception, String defaultErrorString) {
        return ImmutableMap.of((Object)"exception", (Object)exception.getClass().getCanonicalName(), (Object)"message", (Object)(Objects.nonNull(exception.getLocalizedMessage()) ? exception.getLocalizedMessage() : defaultErrorString));
    }

    private Response handleTooManyExemptions(ExemptionsLimitExceededException exception) {
        return Response.status((Response.Status)Response.Status.CONFLICT).entity(this.constructErrorMap(exception, "Unable to add more user exemptions; the limit has been reached.")).type("application/json;charset=UTF-8").build();
    }

    private Response handleGeneralException(Exception exception) {
        logger.debug("Caught unknown exception: ", (Throwable)exception);
        return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).entity(this.constructErrorMap(exception, EXCEPTION_GENERAL_EXCEPTION)).type("application/json;charset=UTF-8").build();
    }
}


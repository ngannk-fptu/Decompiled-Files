/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.permission.AuthorisationException
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 *  org.codehaus.jackson.JsonProcessingException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.pats.rest;

import com.atlassian.pats.exception.CreateTokenFailedException;
import com.atlassian.pats.exception.InvalidDateStringFormatException;
import com.atlassian.pats.exception.InvalidLicenseException;
import com.atlassian.pats.exception.InvalidSortException;
import com.atlassian.pats.exception.UserTokenLimitExceededException;
import com.atlassian.pats.rest.RestError;
import com.atlassian.sal.api.permission.AuthorisationException;
import com.sun.jersey.spi.resource.Singleton;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.codehaus.jackson.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
@Singleton
public class PersonalTokenExceptionMapper
implements ExceptionMapper<Exception> {
    private static final Logger logger = LoggerFactory.getLogger(PersonalTokenExceptionMapper.class);
    private static final String APPLICATION_JSON_WITH_UTF8_TYPE = "application/json;charset=UTF-8";
    private static final String UNAUTHORIZED_EXCEPTION_MESSAGE = "Requesting user is unauthorized to perform this operation";
    private static final String GENERIC_EXCEPTION_MESSAGE = "The server could not perform this operation - please check Application logs";

    public Response toResponse(Exception exception) {
        logger.debug("Got exception: [{}] with message: [{}]", exception.getClass(), (Object)exception.getMessage());
        if (exception instanceof AuthorisationException) {
            return this.handleUnauthorized((AuthorisationException)exception);
        }
        if (exception instanceof IllegalArgumentException) {
            return this.handleBadRequest((IllegalArgumentException)exception);
        }
        if (exception instanceof IllegalStateException) {
            return this.handleBadRequest((IllegalStateException)exception);
        }
        if (exception instanceof WebApplicationException) {
            return this.handleWebApplicationException((WebApplicationException)exception);
        }
        if (exception instanceof DateTimeParseException) {
            return this.handleBadRequest((DateTimeParseException)exception);
        }
        if (exception instanceof CreateTokenFailedException) {
            return this.handleBadRequest((CreateTokenFailedException)exception);
        }
        if (exception instanceof JsonProcessingException) {
            return this.handleBadRequest((JsonProcessingException)exception);
        }
        if (exception instanceof UserTokenLimitExceededException) {
            return this.handleForbidden((UserTokenLimitExceededException)exception);
        }
        if (exception instanceof InvalidDateStringFormatException) {
            return this.handleInvalidDateFormat((InvalidDateStringFormatException)exception);
        }
        if (exception instanceof InvalidSortException) {
            return this.handleUserInvalidSort((InvalidSortException)exception);
        }
        if (exception instanceof InvalidLicenseException) {
            return this.handleForbidden((InvalidLicenseException)exception);
        }
        return this.handleGeneralException(exception);
    }

    private Response handleWebApplicationException(WebApplicationException exception) {
        return exception.getResponse();
    }

    private Response handleBadRequest(IllegalArgumentException exception) {
        return this.createErrorResponse(exception, Response.Status.BAD_REQUEST);
    }

    private Response handleBadRequest(CreateTokenFailedException exception) {
        return this.createErrorResponse(exception, Response.Status.BAD_REQUEST);
    }

    private Response handleBadRequest(DateTimeParseException exception) {
        return this.createErrorResponse(exception, Response.Status.BAD_REQUEST);
    }

    private Response handleBadRequest(IllegalStateException exception) {
        return this.createErrorResponse(exception, Response.Status.BAD_REQUEST);
    }

    private Response handleBadRequest(JsonProcessingException exception) {
        return this.createErrorResponse((Exception)exception, Response.Status.BAD_REQUEST);
    }

    private Response handleForbidden(InvalidLicenseException exception) {
        return this.createErrorResponse(exception, Response.Status.FORBIDDEN);
    }

    private Response createErrorResponse(Exception exception, Response.Status status) {
        return this.handleException(exception, status, Objects.nonNull(exception.getLocalizedMessage()) ? exception.getLocalizedMessage() : "");
    }

    private Response handleUnauthorized(AuthorisationException exception) {
        return this.handleException((Exception)exception, Response.Status.UNAUTHORIZED, UNAUTHORIZED_EXCEPTION_MESSAGE);
    }

    private Response handleForbidden(UserTokenLimitExceededException exception) {
        return this.createErrorResponse(exception, Response.Status.FORBIDDEN);
    }

    private Response handleInvalidDateFormat(InvalidDateStringFormatException exception) {
        return this.createErrorResponse(exception, Response.Status.BAD_REQUEST);
    }

    private Response handleUserInvalidSort(InvalidSortException exception) {
        return this.createErrorResponse(exception, Response.Status.BAD_REQUEST);
    }

    private Response handleGeneralException(Exception exception) {
        logger.info("Caught unknown exception: ", (Throwable)exception);
        return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).entity((Object)RestError.builder().error(GENERIC_EXCEPTION_MESSAGE).build()).type(APPLICATION_JSON_WITH_UTF8_TYPE).build();
    }

    private Response handleException(Exception exception, Response.Status status, String message) {
        return Response.status((Response.Status)status).entity((Object)RestError.builder().exception(Objects.nonNull(exception.getClass().getCanonicalName()) ? exception.getClass().getCanonicalName() : exception.getClass().getName()).error(Objects.nonNull(exception.getLocalizedMessage()) ? exception.getLocalizedMessage() : message).build()).type(APPLICATION_JSON_WITH_UTF8_TYPE).build();
    }
}


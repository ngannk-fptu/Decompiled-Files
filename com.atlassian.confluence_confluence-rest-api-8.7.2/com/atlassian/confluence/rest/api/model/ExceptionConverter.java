/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.ConflictException
 *  com.atlassian.confluence.api.service.exceptions.ContentTooLongException
 *  com.atlassian.confluence.api.service.exceptions.GoneException
 *  com.atlassian.confluence.api.service.exceptions.InternalServerException
 *  com.atlassian.confluence.api.service.exceptions.LicenseUnavailableException
 *  com.atlassian.confluence.api.service.exceptions.NotAuthenticatedException
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.confluence.api.service.exceptions.ReadOnlyException
 *  com.atlassian.confluence.api.service.exceptions.SeeOtherException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException
 *  com.atlassian.sal.api.permission.AuthorisationException
 *  com.atlassian.sal.api.permission.NotAuthenticatedException
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.core.Response$Status$Family
 *  javax.ws.rs.core.Response$StatusType
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.codehaus.jackson.map.JsonMappingException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.rest.api.model;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.ConflictException;
import com.atlassian.confluence.api.service.exceptions.ContentTooLongException;
import com.atlassian.confluence.api.service.exceptions.GoneException;
import com.atlassian.confluence.api.service.exceptions.InternalServerException;
import com.atlassian.confluence.api.service.exceptions.LicenseUnavailableException;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.api.service.exceptions.ReadOnlyException;
import com.atlassian.confluence.api.service.exceptions.SeeOtherException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException;
import com.atlassian.confluence.rest.api.model.RestError;
import com.atlassian.confluence.rest.api.model.validation.RestValidationResult;
import com.atlassian.sal.api.permission.AuthorisationException;
import com.atlassian.sal.api.permission.NotAuthenticatedException;
import javax.ws.rs.core.Response;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExperimentalApi
public class ExceptionConverter {
    private static final Logger log = LoggerFactory.getLogger(ExceptionConverter.class);
    public static final Response.StatusType NOT_IMPLEMENTED = new Response.StatusType(){

        public int getStatusCode() {
            return 501;
        }

        public Response.Status.Family getFamily() {
            return Response.Status.Family.SERVER_ERROR;
        }

        public String getReasonPhrase() {
            return "Not Implemented";
        }
    };

    public static enum AdditionalStatus implements Response.StatusType
    {
        PAYMENT_REQUIRED(402, "PAYMENT_REQUIRED", Response.Status.Family.CLIENT_ERROR),
        READ_ONLY_MODE_ENABLED(405, "READ_ONLY", Response.Status.Family.CLIENT_ERROR),
        REQUEST_TOO_LONG(413, "Request Entity Too Large", Response.Status.Family.CLIENT_ERROR);

        private final int code;
        private final String reason;
        private Response.Status.Family family;

        private AdditionalStatus(int statusCode, String reasonPhrase, Response.Status.Family family) {
            this.code = statusCode;
            this.reason = reasonPhrase;
            this.family = family;
        }

        public Response.Status.Family getFamily() {
            return this.family;
        }

        public int getStatusCode() {
            return this.code;
        }

        public String getReasonPhrase() {
            return this.toString();
        }

        public String toString() {
            return this.reason;
        }
    }

    public static class Server {
        public static RestError convertServiceException(Exception e) {
            Response.StatusType type = Server.getStatusTypeForException(e);
            RestValidationResult validationResult = Server.getValidationResultForException(e);
            if (type != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Logging converted API exception: ", (Throwable)e);
                } else if (type == Response.Status.INTERNAL_SERVER_ERROR) {
                    log.error("Converted internal server error: ", (Throwable)e);
                }
                return new RestError(type, e.getMessage(), validationResult);
            }
            log.error("No status code found for exception, converting to internal server error : ", (Throwable)e);
            return new RestError((Response.StatusType)Response.Status.INTERNAL_SERVER_ERROR, "", validationResult);
        }

        private static @Nullable RestValidationResult getValidationResultForException(Exception e) {
            if (e instanceof ServiceException) {
                return new RestValidationResult(((ServiceException)e).optionalValidationResult().orElse(null));
            }
            return null;
        }

        private static // Could not load outer class - annotation placement on inner may be incorrect
         @Nullable Response.StatusType getStatusTypeForException(Exception e) {
            if (e instanceof NotFoundException) {
                return Response.Status.NOT_FOUND;
            }
            if (e instanceof LicenseUnavailableException) {
                return AdditionalStatus.PAYMENT_REQUIRED;
            }
            if (e instanceof ReadOnlyException) {
                return AdditionalStatus.READ_ONLY_MODE_ENABLED;
            }
            if (e instanceof GoneException) {
                return Response.Status.GONE;
            }
            if (e instanceof NotAuthenticatedException || e instanceof com.atlassian.confluence.api.service.exceptions.NotAuthenticatedException) {
                return Response.Status.UNAUTHORIZED;
            }
            if (e instanceof PermissionException || e instanceof AuthorisationException) {
                return Response.Status.FORBIDDEN;
            }
            if (e instanceof SeeOtherException) {
                return Response.Status.SEE_OTHER;
            }
            if (e instanceof BadRequestException || e instanceof JsonMappingException) {
                return Response.Status.BAD_REQUEST;
            }
            if (e instanceof ContentTooLongException) {
                return AdditionalStatus.REQUEST_TOO_LONG;
            }
            if (e instanceof UnsupportedOperationException || e instanceof NotImplementedServiceException) {
                return NOT_IMPLEMENTED;
            }
            if (e instanceof ConflictException) {
                return Response.Status.CONFLICT;
            }
            if (e instanceof InternalServerException) {
                return Response.Status.INTERNAL_SERVER_ERROR;
            }
            return null;
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.auth.Authenticator$Result
 *  com.atlassian.sal.api.auth.Authenticator$Result$Error
 *  com.atlassian.sal.api.auth.Authenticator$Result$Failure
 *  com.atlassian.sal.api.auth.Authenticator$Result$Success
 *  com.atlassian.sal.api.message.Message
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.jwt.internal.sal;

import com.atlassian.jwt.Jwt;
import com.atlassian.jwt.core.http.auth.AuthenticationResultHandler;
import com.atlassian.jwt.exception.JwtSignatureMismatchException;
import com.atlassian.sal.api.auth.Authenticator;
import com.atlassian.sal.api.message.Message;
import java.io.IOException;
import java.io.Serializable;
import java.security.Principal;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultAuthenticationResultHandler
implements AuthenticationResultHandler<HttpServletResponse, Authenticator.Result> {
    private static final Logger log = LoggerFactory.getLogger(DefaultAuthenticationResultHandler.class);

    @Override
    public Authenticator.Result createAndSendInternalError(Exception e, HttpServletResponse response, String externallyVisibleMessage) {
        return DefaultAuthenticationResultHandler.createAndSendError(e, response, 500, externallyVisibleMessage);
    }

    @Override
    public Authenticator.Result createAndSendBadRequestError(Exception e, HttpServletResponse response, String externallyVisibleMessage) {
        return DefaultAuthenticationResultHandler.createAndSendError(e, response, 400, externallyVisibleMessage);
    }

    @Override
    public Authenticator.Result createAndSendUnauthorisedFailure(Exception e, HttpServletResponse response, String externallyVisibleMessage) {
        return DefaultAuthenticationResultHandler.createAndSendFailure(e, response, 401, externallyVisibleMessage);
    }

    @Override
    public Authenticator.Result createAndSendForbiddenError(Exception e, HttpServletResponse response) {
        return DefaultAuthenticationResultHandler.createAndSendError(e, response, 403, "Access to this resource is forbidden without successful authentication. Please supply valid credentials.");
    }

    @Override
    public Authenticator.Result success(String message, Principal principal, Jwt authenticatedJwt) {
        return new Authenticator.Result.Success(DefaultAuthenticationResultHandler.createMessage(message), principal);
    }

    private static Authenticator.Result.Error createAndSendError(Exception e, HttpServletResponse response, int httpResponseCode, String externallyVisibleMessage) {
        log.debug("Error during JWT authentication: ", (Throwable)e);
        DefaultAuthenticationResultHandler.sendErrorResponse(response, httpResponseCode, externallyVisibleMessage);
        return new Authenticator.Result.Error(DefaultAuthenticationResultHandler.createMessage(e.getLocalizedMessage()));
    }

    private static Authenticator.Result.Failure createAndSendFailure(Exception e, HttpServletResponse response, int httpResponseCode, String externallyVisibleMessage) {
        if (e instanceof JwtSignatureMismatchException) {
            JwtSignatureMismatchException mismatch = (JwtSignatureMismatchException)e;
            String issuer = StringUtils.defaultString((String)mismatch.getIssuer(), (String)"unavailable");
            log.warn("Signature mismatch during JWT authentication, issuer: {}", (Object)issuer, (Object)e);
        } else {
            log.warn("Failure during JWT authentication", (Throwable)e);
        }
        DefaultAuthenticationResultHandler.sendErrorResponse(response, httpResponseCode, externallyVisibleMessage);
        return new Authenticator.Result.Failure(DefaultAuthenticationResultHandler.createMessage(e.getLocalizedMessage()));
    }

    private static void sendErrorResponse(HttpServletResponse response, int httpResponseCode, String externallyVisibleMessage) {
        response.reset();
        try {
            response.sendError(httpResponseCode, externallyVisibleMessage);
        }
        catch (IOException doubleFacePalm) {
            log.error("Encountered IOException while trying to report an authentication failure.", (Throwable)doubleFacePalm);
            response.reset();
            response.setStatus(httpResponseCode);
        }
    }

    private static Message createMessage(final String message) {
        return new Message(){

            public String getKey() {
                return message;
            }

            public Serializable[] getArguments() {
                return null;
            }

            public String toString() {
                return message;
            }
        };
    }
}


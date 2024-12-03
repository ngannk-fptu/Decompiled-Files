/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.jwt.core.http.auth;

import com.atlassian.jwt.CanonicalHttpRequest;
import com.atlassian.jwt.Jwt;
import com.atlassian.jwt.core.http.JwtRequestExtractor;
import com.atlassian.jwt.core.http.auth.AuthenticationResultHandler;
import com.atlassian.jwt.core.http.auth.JwtAuthenticator;
import com.atlassian.jwt.exception.JwtIssuerLacksSharedSecretException;
import com.atlassian.jwt.exception.JwtParseException;
import com.atlassian.jwt.exception.JwtUnknownIssuerException;
import com.atlassian.jwt.exception.JwtUserRejectedException;
import com.atlassian.jwt.exception.JwtVerificationException;
import com.atlassian.jwt.httpclient.CanonicalRequestUtil;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractJwtAuthenticator<REQ, RES, S>
implements JwtAuthenticator<REQ, RES, S> {
    private static final String BAD_CREDENTIALS_MESSAGE = "Your presented credentials do not provide access to this resource.";
    private static final Logger log = LoggerFactory.getLogger(AbstractJwtAuthenticator.class);
    private final JwtRequestExtractor<REQ> jwtExtractor;
    private final AuthenticationResultHandler<RES, S> authenticationResultHandler;

    public AbstractJwtAuthenticator(JwtRequestExtractor<REQ> jwtExtractor, AuthenticationResultHandler<RES, S> authenticationResultHandler) {
        this.jwtExtractor = Objects.requireNonNull(jwtExtractor, "jwtExtractor");
        this.authenticationResultHandler = Objects.requireNonNull(authenticationResultHandler, "authenticationResultHandler");
    }

    @Override
    public S authenticate(REQ request, RES response) {
        try {
            String jwtString = this.jwtExtractor.extractJwt(request);
            if (null == jwtString) {
                throw new IllegalArgumentException("Cannot authenticate a request without a JWT token");
            }
            Jwt authenticatedJwt = this.verifyJwt(jwtString, request);
            this.tagRequest(request, authenticatedJwt);
            return this.authenticationResultHandler.success("Authentication successful!", null, authenticatedJwt);
        }
        catch (IOException | IllegalArgumentException | NoSuchAlgorithmException e) {
            return this.createAndSendInternalError(e, response);
        }
        catch (JwtParseException e) {
            return this.authenticationResultHandler.createAndSendBadRequestError(e, response, AbstractJwtAuthenticator.getBriefMessageFromException(e));
        }
        catch (JwtVerificationException e) {
            return this.authenticationResultHandler.createAndSendUnauthorisedFailure(e, response, AbstractJwtAuthenticator.getBriefMessageFromException(e));
        }
        catch (JwtIssuerLacksSharedSecretException | JwtUnknownIssuerException | JwtUserRejectedException e) {
            return this.authenticationResultHandler.createAndSendUnauthorisedFailure(e, response, BAD_CREDENTIALS_MESSAGE);
        }
        catch (Exception e) {
            return this.authenticationResultHandler.createAndSendForbiddenError(e, response);
        }
    }

    protected abstract Jwt verifyJwt(String var1, CanonicalHttpRequest var2) throws JwtParseException, JwtVerificationException, JwtIssuerLacksSharedSecretException, JwtUnknownIssuerException, IOException, NoSuchAlgorithmException;

    protected abstract void tagRequest(REQ var1, Jwt var2) throws JwtUserRejectedException;

    private static String getBriefMessageFromException(Exception e) {
        return e.getLocalizedMessage() + (null == e.getCause() ? "" : " (caused by " + e.getCause().getLocalizedMessage() + ")");
    }

    private Jwt verifyJwt(String jwtString, REQ request) throws JwtParseException, JwtVerificationException, JwtIssuerLacksSharedSecretException, JwtUnknownIssuerException, IOException, NoSuchAlgorithmException {
        CanonicalHttpRequest canonicalHttpRequest = this.jwtExtractor.getCanonicalHttpRequest(request);
        if (log.isDebugEnabled()) {
            log.debug("Canonical request is: {}", (Object)CanonicalRequestUtil.toVerboseString(canonicalHttpRequest));
        }
        return this.verifyJwt(jwtString, canonicalHttpRequest);
    }

    private S createAndSendInternalError(Exception e, RES response) {
        return this.authenticationResultHandler.createAndSendInternalError(e, response, "An internal error occurred. Please check the host product's logs.");
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.auth.Authenticator
 *  com.atlassian.sal.api.auth.Authenticator$Result
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.jwt.internal.sal;

import com.atlassian.jwt.CanonicalHttpRequest;
import com.atlassian.jwt.Jwt;
import com.atlassian.jwt.JwtService;
import com.atlassian.jwt.core.http.JavaxJwtRequestExtractor;
import com.atlassian.jwt.core.http.auth.AbstractJwtAuthenticator;
import com.atlassian.jwt.exception.JwtIssuerLacksSharedSecretException;
import com.atlassian.jwt.exception.JwtParseException;
import com.atlassian.jwt.exception.JwtUnknownIssuerException;
import com.atlassian.jwt.exception.JwtVerificationException;
import com.atlassian.jwt.internal.PluginJwtRegistry;
import com.atlassian.jwt.internal.sal.DefaultAuthenticationResultHandler;
import com.atlassian.jwt.reader.JwtClaimVerifiersBuilder;
import com.atlassian.jwt.reader.JwtReaderFactory;
import com.atlassian.sal.api.auth.Authenticator;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JwtAuthenticatorImpl
extends AbstractJwtAuthenticator<HttpServletRequest, HttpServletResponse, Authenticator.Result>
implements Authenticator {
    private final JwtClaimVerifiersBuilder defaultClaimVerifiersBuilder;
    private final JwtReaderFactory jwtReaderFactory;
    private final PluginJwtRegistry jwtRegistry;
    private final JwtService jwtService;

    public JwtAuthenticatorImpl(JwtClaimVerifiersBuilder defaultClaimVerifiersBuilder, JwtReaderFactory jwtReaderFactory, PluginJwtRegistry jwtRegistry, JwtService jwtService) {
        super(new JavaxJwtRequestExtractor(), new DefaultAuthenticationResultHandler());
        this.defaultClaimVerifiersBuilder = defaultClaimVerifiersBuilder;
        this.jwtReaderFactory = jwtReaderFactory;
        this.jwtRegistry = jwtRegistry;
        this.jwtService = Objects.requireNonNull(jwtService, "jwtService");
    }

    @Override
    protected void tagRequest(HttpServletRequest request, Jwt jwt) {
        request.setAttribute("Plugin-Key", (Object)jwt.getIssuer());
        request.setAttribute("jwt.payload", (Object)jwt.getJsonPayload());
        request.setAttribute("jwt.subject", (Object)jwt.getSubject());
    }

    @Override
    protected Jwt verifyJwt(String jwtString, CanonicalHttpRequest canonicalHttpRequest) throws JwtParseException, JwtVerificationException, JwtIssuerLacksSharedSecretException, JwtUnknownIssuerException, IOException, NoSuchAlgorithmException {
        Jwt jwt = this.jwtReaderFactory.getReader(jwtString).readUnverified(jwtString);
        String issuer = jwt.getIssuer();
        JwtClaimVerifiersBuilder claimVerifiersBuilder = this.getClaimVerifiersBuilder(issuer);
        return this.jwtService.verifyJwt(jwtString, claimVerifiersBuilder.build(canonicalHttpRequest));
    }

    private JwtClaimVerifiersBuilder getClaimVerifiersBuilder(String issuer) {
        if (issuer == null) {
            return this.defaultClaimVerifiersBuilder;
        }
        JwtClaimVerifiersBuilder builder = this.jwtRegistry.getClaimVerifiersBuilder(issuer);
        return builder != null ? builder : this.defaultClaimVerifiersBuilder;
    }
}


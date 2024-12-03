/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.security.auth.trustedapps.filter;

import com.atlassian.security.auth.trustedapps.ApplicationCertificate;
import com.atlassian.security.auth.trustedapps.DefaultEncryptedCertificate;
import com.atlassian.security.auth.trustedapps.InvalidCertificateException;
import com.atlassian.security.auth.trustedapps.TransportErrorMessage;
import com.atlassian.security.auth.trustedapps.TrustedApplication;
import com.atlassian.security.auth.trustedapps.TrustedApplicationUtils;
import com.atlassian.security.auth.trustedapps.TrustedApplicationsManager;
import com.atlassian.security.auth.trustedapps.UnableToVerifySignatureException;
import com.atlassian.security.auth.trustedapps.UserResolver;
import com.atlassian.security.auth.trustedapps.filter.AuthenticationController;
import com.atlassian.security.auth.trustedapps.filter.Authenticator;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrustedApplicationFilterAuthenticator
implements Authenticator {
    private static final Logger log = LoggerFactory.getLogger(TrustedApplicationFilterAuthenticator.class);
    protected static final String FORWARD_REQUEST_URI = "javax.servlet.forward.request_uri";
    final TrustedApplicationsManager appManager;
    final UserResolver resolver;
    final AuthenticationController authenticationController;

    public TrustedApplicationFilterAuthenticator(TrustedApplicationsManager appManager, UserResolver resolver, AuthenticationController authenticationController) {
        this.appManager = appManager;
        this.resolver = resolver;
        this.authenticationController = authenticationController;
    }

    private static boolean atLeast(Integer protocolVersion, int required) {
        return protocolVersion != null && protocolVersion >= required;
    }

    @Override
    public Authenticator.Result authenticate(HttpServletRequest request, HttpServletResponse response) {
        String signedRequestUrl;
        ApplicationCertificate certificate;
        TrustedApplication app;
        Integer protocolVersion;
        String certStr = request.getHeader("X-Seraph-Trusted-App-Cert");
        if (TrustedApplicationFilterAuthenticator.isBlank(certStr)) {
            return new Authenticator.Result.NoAttempt();
        }
        String version = request.getHeader("X-Seraph-Trusted-App-Version");
        try {
            protocolVersion = !TrustedApplicationFilterAuthenticator.isBlank(version) ? Integer.parseInt(version) : 0;
        }
        catch (NumberFormatException e) {
            Authenticator.Result.Error result = new Authenticator.Result.Error(new TransportErrorMessage.BadProtocolVersion(version));
            TrustedApplicationFilterAuthenticator.setFailureHeader(response, result.getMessage());
            return result;
        }
        if (!protocolVersion.equals(TrustedApplicationUtils.getProtocolVersionInUse())) {
            Authenticator.Result.Error result = new Authenticator.Result.Error(new TransportErrorMessage.UnSupportedProtocolVersion(protocolVersion));
            TrustedApplicationFilterAuthenticator.setFailureHeader(response, result.getMessage());
            return result;
        }
        String id = request.getHeader("X-Seraph-Trusted-App-ID");
        if (TrustedApplicationFilterAuthenticator.isBlank(id)) {
            Authenticator.Result.Error result = new Authenticator.Result.Error(new TransportErrorMessage.ApplicationIdNotFoundInRequest());
            TrustedApplicationFilterAuthenticator.setFailureHeader(response, result.getMessage());
            return result;
        }
        String key = request.getHeader("X-Seraph-Trusted-App-Key");
        String magicNumber = request.getHeader("X-Seraph-Trusted-App-Magic");
        if (TrustedApplicationUtils.Constant.VERSION_TWO.equals(TrustedApplicationUtils.getProtocolVersionInUse())) {
            if (TrustedApplicationFilterAuthenticator.isBlank(key)) {
                Authenticator.Result.Error result = new Authenticator.Result.Error(new TransportErrorMessage.SecretKeyNotFoundInRequest());
                TrustedApplicationFilterAuthenticator.setFailureHeader(response, result.getMessage());
                return result;
            }
            if (TrustedApplicationFilterAuthenticator.isBlank(magicNumber)) {
                Authenticator.Result.Error result = new Authenticator.Result.Error(new TransportErrorMessage.MagicNumberNotFoundInRequest());
                TrustedApplicationFilterAuthenticator.setFailureHeader(response, result.getMessage());
                return result;
            }
        }
        if ((app = this.appManager.getTrustedApplication(id)) == null) {
            Authenticator.Result.Failure result = new Authenticator.Result.Failure(new TransportErrorMessage.ApplicationUnknown(id));
            TrustedApplicationFilterAuthenticator.setFailureHeader(response, result.getMessage());
            return result;
        }
        try {
            certificate = app.decode(new DefaultEncryptedCertificate(id, key, certStr, protocolVersion, magicNumber), request);
        }
        catch (InvalidCertificateException ex) {
            log.warn("Failed to login trusted application: " + app.getID() + " due to: " + ex);
            log.debug("Failed to login trusted application cause", (Throwable)ex);
            Authenticator.Result.Error result = new Authenticator.Result.Error(ex.getTransportErrorMessage());
            TrustedApplicationFilterAuthenticator.setFailureHeader(response, result.getMessage());
            return result;
        }
        String signature = request.getHeader("X-Seraph-Trusted-App-Signature");
        if (signature == null) {
            Authenticator.Result.Error result = new Authenticator.Result.Error(new TransportErrorMessage.BadSignature());
            TrustedApplicationFilterAuthenticator.setFailureHeader(response, result.getMessage());
            return result;
        }
        String forwardReqUri = this.getLogicalUri(request);
        log.debug("Got forward URI: {}", (Object)forwardReqUri);
        StringBuffer sb = forwardReqUri == null ? request.getRequestURL() : new StringBuffer(forwardReqUri);
        log.debug("Going ahead with URI: {}", (Object)sb);
        String q = request.getQueryString();
        if (q != null) {
            sb.append('?');
            sb.append(q);
        }
        String expectedSignedUrl = sb.toString();
        try {
            if (!app.verifySignature(certificate.getCreationTime().getTime(), expectedSignedUrl, certificate.getUserName(), signature)) {
                log.warn(String.format("Failed to login trusted application [%s] due to bad URL signature. Received protocol version [%d]. Required protocol version [%d]", app.getID(), certificate.getProtocolVersion(), TrustedApplicationUtils.getProtocolVersionInUse()));
                Authenticator.Result.Error result = new Authenticator.Result.Error(new TransportErrorMessage.BadSignature(expectedSignedUrl));
                TrustedApplicationFilterAuthenticator.setFailureHeader(response, result.getMessage());
                return result;
            }
            signedRequestUrl = expectedSignedUrl;
        }
        catch (UnableToVerifySignatureException e) {
            log.warn("Failed to login trusted application: " + app.getID() + " due to: " + e);
            Authenticator.Result.Error result = new Authenticator.Result.Error(new TransportErrorMessage.BadSignature(expectedSignedUrl));
            TrustedApplicationFilterAuthenticator.setFailureHeader(response, result.getMessage());
            return result;
        }
        Principal user = this.resolver.resolve(certificate);
        if (user == null) {
            log.warn("User '" + certificate.getUserName() + "' referenced by trusted application: '" + app.getID() + "' is not found.");
            Authenticator.Result.Failure result = new Authenticator.Result.Failure(new TransportErrorMessage.UserUnknown(certificate.getUserName()));
            TrustedApplicationFilterAuthenticator.setFailureHeader(response, result.getMessage());
            return result;
        }
        if (!this.authenticationController.canLogin(user, request)) {
            log.warn("User '" + certificate.getUserName() + "' referenced by trusted application: '" + app.getID() + "' cannot login.");
            Authenticator.Result.Failure result = new Authenticator.Result.Failure(new TransportErrorMessage.PermissionDenied());
            TrustedApplicationFilterAuthenticator.setFailureHeader(response, result.getMessage());
            return result;
        }
        if (signedRequestUrl != null) {
            return new Authenticator.Result.Success(user, signedRequestUrl);
        }
        return new Authenticator.Result.Success(user);
    }

    protected String getLogicalUri(HttpServletRequest request) {
        String uriPathBeforeForwarding = (String)request.getAttribute(FORWARD_REQUEST_URI);
        if (uriPathBeforeForwarding == null) {
            return null;
        }
        URI newUri = URI.create(request.getRequestURL().toString());
        try {
            return new URI(newUri.getScheme(), newUri.getAuthority(), uriPathBeforeForwarding, newUri.getQuery(), newUri.getFragment()).toString();
        }
        catch (URISyntaxException e) {
            log.warn("forwarded request had invalid original URI path: " + uriPathBeforeForwarding);
            return null;
        }
    }

    private static void setFailureHeader(HttpServletResponse response, String message) {
        response.setHeader("X-Seraph-Trusted-App-Status", "ERROR");
        response.addHeader("X-Seraph-Trusted-App-Error", message);
        if (log.isDebugEnabled()) {
            log.debug(message, (Throwable)new RuntimeException(message));
        }
    }

    private static boolean isBlank(String input) {
        return input == null || input.trim().length() == 0;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.ApplicationCertificate;
import com.atlassian.security.auth.trustedapps.BouncyCastleEncryptionProvider;
import com.atlassian.security.auth.trustedapps.CertificateTimeoutValidator;
import com.atlassian.security.auth.trustedapps.Clock;
import com.atlassian.security.auth.trustedapps.DefaultRequestValidator;
import com.atlassian.security.auth.trustedapps.EncryptedCertificate;
import com.atlassian.security.auth.trustedapps.EncryptionProvider;
import com.atlassian.security.auth.trustedapps.InvalidCertificateException;
import com.atlassian.security.auth.trustedapps.InvalidRequestException;
import com.atlassian.security.auth.trustedapps.Null;
import com.atlassian.security.auth.trustedapps.RequestConditions;
import com.atlassian.security.auth.trustedapps.RequestValidator;
import com.atlassian.security.auth.trustedapps.SystemClock;
import com.atlassian.security.auth.trustedapps.TrustedApplication;
import com.atlassian.security.auth.trustedapps.TrustedApplicationUtils;
import com.atlassian.security.auth.trustedapps.UnableToVerifySignatureException;
import com.google.common.annotations.VisibleForTesting;
import java.io.UnsupportedEncodingException;
import java.security.PublicKey;
import javax.servlet.http.HttpServletRequest;

public class DefaultTrustedApplication
implements TrustedApplication {
    private final String name;
    private final CertificateTimeoutValidator timeoutValidator;
    protected final String id;
    protected final PublicKey publicKey;
    protected final RequestConditions requestConditions;
    protected final RequestValidator requestValidator;
    protected final EncryptionProvider encryptionProvider;

    public DefaultTrustedApplication(EncryptionProvider encryptionProvider, PublicKey publicKey, String id, String name, RequestConditions requestConditions) {
        this(encryptionProvider, publicKey, id, name, requestConditions, new SystemClock());
    }

    public DefaultTrustedApplication(EncryptionProvider encryptionProvider, PublicKey publicKey, String id, RequestConditions requestConditions) {
        this(encryptionProvider, publicKey, id, null, requestConditions, new SystemClock());
    }

    public DefaultTrustedApplication(PublicKey publicKey, String id, RequestConditions requestConditions) {
        this(new BouncyCastleEncryptionProvider(), publicKey, id, null, requestConditions);
    }

    @VisibleForTesting
    DefaultTrustedApplication(EncryptionProvider encryptionProvider, PublicKey publicKey, String id, String name, RequestConditions requestConditions, Clock clock) {
        Null.not("encryptionProvider", encryptionProvider);
        Null.not("publicKey", publicKey);
        Null.not("id", id);
        Null.not("requestConditions", requestConditions);
        Null.not("clock", clock);
        this.encryptionProvider = encryptionProvider;
        this.publicKey = publicKey;
        this.id = id;
        this.name = name;
        this.requestConditions = requestConditions;
        this.requestValidator = new DefaultRequestValidator(requestConditions.getIPMatcher(), requestConditions.getURLMatcher());
        this.timeoutValidator = new CertificateTimeoutValidator(clock);
    }

    public DefaultTrustedApplication(PublicKey publicKey, String id, String name, RequestConditions requestConditions) {
        this(new BouncyCastleEncryptionProvider(), publicKey, id, name, requestConditions);
    }

    @Override
    public ApplicationCertificate decode(EncryptedCertificate encCert, HttpServletRequest request) throws InvalidCertificateException {
        ApplicationCertificate certificate = this.encryptionProvider.decodeEncryptedCertificate(encCert, this.publicKey, this.getID());
        this.timeoutValidator.checkCertificateExpiry(certificate, this.requestConditions.getCertificateTimeout());
        this.checkRequest(request);
        return certificate;
    }

    @Override
    public boolean verifySignature(long timestamp, String requestUrl, String username, String receivedSignature) throws UnableToVerifySignatureException {
        try {
            return this.encryptionProvider.verifySignature(this.publicKey, TrustedApplicationUtils.generateSignatureBaseString(timestamp, requestUrl, username), receivedSignature);
        }
        catch (StringIndexOutOfBoundsException e) {
            throw new UnableToVerifySignatureException(e);
        }
        catch (UnsupportedEncodingException e) {
            throw new UnableToVerifySignatureException(e);
        }
    }

    @Override
    public RequestConditions getRequestConditions() {
        return this.requestConditions;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getID() {
        return this.id;
    }

    @Override
    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    protected void checkRequest(HttpServletRequest request) throws InvalidCertificateException {
        try {
            this.requestValidator.validate(request);
        }
        catch (InvalidRequestException e) {
            throw new InvalidCertificateException(e);
        }
    }
}


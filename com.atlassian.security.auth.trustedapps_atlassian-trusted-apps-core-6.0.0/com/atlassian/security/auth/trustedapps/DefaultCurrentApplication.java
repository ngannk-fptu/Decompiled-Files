/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  javax.annotation.Nullable
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.ApplicationCertificate;
import com.atlassian.security.auth.trustedapps.BouncyCastleEncryptionProvider;
import com.atlassian.security.auth.trustedapps.CertificateTimeoutValidator;
import com.atlassian.security.auth.trustedapps.Clock;
import com.atlassian.security.auth.trustedapps.CurrentApplication;
import com.atlassian.security.auth.trustedapps.EncryptedCertificate;
import com.atlassian.security.auth.trustedapps.EncryptionProvider;
import com.atlassian.security.auth.trustedapps.InvalidCertificateException;
import com.atlassian.security.auth.trustedapps.Null;
import com.atlassian.security.auth.trustedapps.RequestConditions;
import com.atlassian.security.auth.trustedapps.SystemClock;
import com.atlassian.security.auth.trustedapps.TransportErrorMessage;
import com.atlassian.security.auth.trustedapps.TrustedApplication;
import com.atlassian.security.auth.trustedapps.TrustedApplicationUtils;
import com.atlassian.security.auth.trustedapps.UnableToVerifySignatureException;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

public class DefaultCurrentApplication
implements CurrentApplication,
TrustedApplication {
    private final EncryptionProvider encryptionProvider;
    private final Clock clock;
    private final CertificateTimeoutValidator certificateValidator;
    protected final String id;
    protected final PublicKey publicKey;
    protected final PrivateKey privateKey;
    private final LoadingCache<CacheKey, CachedCertificate> certificateCache = CacheBuilder.newBuilder().expireAfterWrite(900L, TimeUnit.SECONDS).maximumSize(1000L).build((CacheLoader)new CacheLoader<CacheKey, CachedCertificate>(){

        public CachedCertificate load(CacheKey key) {
            return new CachedCertificate(DefaultCurrentApplication.this.encode(key), DefaultCurrentApplication.this.clock.currentTimeMillis());
        }
    });

    public DefaultCurrentApplication(EncryptionProvider encryptionProvider, PublicKey publicKey, PrivateKey privateKey, String id) {
        this(encryptionProvider, publicKey, privateKey, id, new SystemClock());
    }

    public DefaultCurrentApplication(PublicKey publicKey, PrivateKey privateKey, String id) {
        this(new BouncyCastleEncryptionProvider(), publicKey, privateKey, id);
    }

    @VisibleForTesting
    DefaultCurrentApplication(EncryptionProvider encryptionProvider, PublicKey publicKey, PrivateKey privateKey, String id, Clock clock) {
        Null.not("encryptionProvider", encryptionProvider);
        Null.not("publicKey", publicKey);
        Null.not("privateKey", privateKey);
        Null.not("id", id);
        this.encryptionProvider = encryptionProvider;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.id = id;
        this.clock = clock;
        this.certificateValidator = new CertificateTimeoutValidator(clock);
    }

    @Override
    public EncryptedCertificate encode(String userName) {
        return this.encode(userName, null);
    }

    @Override
    public EncryptedCertificate encode(String userName, String urlToSign) {
        CacheKey key = new CacheKey(userName, urlToSign);
        CachedCertificate cachedCertificate = (CachedCertificate)this.certificateCache.getUnchecked((Object)key);
        try {
            return cachedCertificate.checkCertificateCacheExpiry();
        }
        catch (InvalidCertificateException e) {
            this.certificateCache.invalidate((Object)key);
            return ((CachedCertificate)this.certificateCache.getUnchecked((Object)key)).getCertificate();
        }
    }

    private EncryptedCertificate encode(CacheKey key) {
        return this.encryptionProvider.createEncryptedCertificate(key.userName, this.privateKey, this.getID(), key.urlToSign);
    }

    @Override
    public ApplicationCertificate decode(EncryptedCertificate encCert, HttpServletRequest request) throws InvalidCertificateException {
        ApplicationCertificate certificate = this.encryptionProvider.decodeEncryptedCertificate(encCert, this.publicKey, this.getID());
        this.certificateValidator.checkCertificateExpiry(certificate, TrustedApplicationUtils.getLoopbackCallTimeout());
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
    public String getID() {
        return this.id;
    }

    @Override
    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    @Override
    public RequestConditions getRequestConditions() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    public void clearCache() {
        this.certificateCache.invalidateAll();
    }

    private class CachedCertificate {
        private final EncryptedCertificate encryptedCertificate;
        private final long creationTime;

        private CachedCertificate(EncryptedCertificate encryptedCertificate, long creationTime) {
            this.encryptedCertificate = encryptedCertificate;
            this.creationTime = creationTime;
        }

        public EncryptedCertificate checkCertificateCacheExpiry() throws InvalidCertificateException {
            if (this.creationTime + TimeUnit.SECONDS.toMillis(900L) >= System.currentTimeMillis()) {
                return this.encryptedCertificate;
            }
            throw new InvalidCertificateException(new TransportErrorMessage(TransportErrorMessage.Code.OLD_CERT, "Certificate in cache has expired"));
        }

        public EncryptedCertificate getCertificate() {
            return this.encryptedCertificate;
        }
    }

    private class CacheKey {
        private final String userName;
        private final String urlToSign;

        private CacheKey(@Nullable String userName, String urlToSign) {
            Null.not("userName", userName);
            this.userName = userName;
            this.urlToSign = urlToSign;
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || this.getClass() != other.getClass()) {
                return false;
            }
            CacheKey that = (CacheKey)other;
            return (this.urlToSign == null ? that.urlToSign == null : this.urlToSign.equals(that.urlToSign)) && this.userName.equals(that.userName);
        }

        public int hashCode() {
            int result = this.userName.hashCode();
            result = 31 * result + (this.urlToSign != null ? this.urlToSign.hashCode() : 0);
            return result;
        }
    }
}


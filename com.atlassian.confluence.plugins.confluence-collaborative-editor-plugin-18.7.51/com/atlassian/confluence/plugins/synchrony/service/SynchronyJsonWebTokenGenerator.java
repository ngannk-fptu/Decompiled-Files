/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  com.google.common.annotations.VisibleForTesting
 *  org.apache.commons.codec.binary.Base64
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.synchrony.service;

import com.atlassian.config.ConfigurationException;
import com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import com.google.common.annotations.VisibleForTesting;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="synchrony-token-generator")
public class SynchronyJsonWebTokenGenerator {
    private final UserAccessor userAccessor;
    private final SynchronyConfigurationManager synchronyConfigurationManager;
    public static final long TOKEN_EXPIRY_TIME = TimeUnit.MINUTES.toSeconds(15L);
    public static final long TOKEN_EXPIRY_LEEWAY = TimeUnit.MINUTES.toSeconds(1L);

    @Autowired
    public SynchronyJsonWebTokenGenerator(@ComponentImport UserAccessor userAccessor, SynchronyConfigurationManager synchronyConfigurationManager) {
        this.userAccessor = userAccessor;
        this.synchronyConfigurationManager = synchronyConfigurationManager;
    }

    public String create(Long contentId, ConfluenceUser user) throws Exception {
        return this.create(false, contentId, user);
    }

    String createAdminToken() {
        try {
            return this.create(true, null, null);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String create(boolean isAdmin, Long contentId, ConfluenceUser user) throws Exception {
        Map<String, Object> session;
        Map<String, Object> access;
        if (!this.synchronyConfigurationManager.registerWithSynchrony()) {
            throw new ConfigurationException("This instance could not be registered with Synchrony.");
        }
        if (this.synchronyConfigurationManager.getSynchronyPublicKey() == null && !this.synchronyConfigurationManager.retrievePublicKey()) {
            throw new ConfigurationException("Could not retrieve Synchrony public key.");
        }
        String publicKey = this.synchronyConfigurationManager.getSynchronyPublicKey();
        String appId = this.synchronyConfigurationManager.getConfiguredAppID();
        if (appId == null) {
            throw new ConfigurationException("Stored AppID is null, this instance may not have been configured.");
        }
        String appSecret = this.synchronyConfigurationManager.getAppSecret();
        String serviceUri = this.synchronyConfigurationManager.getExternalServiceUrl();
        String passphrase = this.synchronyConfigurationManager.getPassphrase();
        if (passphrase == null) {
            this.synchronyConfigurationManager.generateStorePassphraseIfMissing();
            passphrase = this.synchronyConfigurationManager.getPassphrase();
            if (passphrase == null) {
                throw new ConfigurationException("Could not lazily generate a passphrase.");
            }
        }
        if (isAdmin) {
            access = Collections.singletonMap("admin", "admin");
            session = Collections.emptyMap();
        } else {
            access = this.getAccessData(contentId, appId, "full");
            session = this.getSessionData(user);
        }
        JWTClaimsSet jwtClaims = this.makeClaims(appId, serviceUri, access, session, user, passphrase);
        SignedJWT signedJWT = this.signedJWTFromClaims(jwtClaims, appSecret);
        if (this.synchronyConfigurationManager.isSynchronyEncryptionEnabled()) {
            return this.encryptSignedJWT(signedJWT, publicKey).serialize();
        }
        return signedJWT.serialize();
    }

    @VisibleForTesting
    protected JWTClaimsSet makeClaims(String appId, String serviceUri, Map<String, Object> access, Map<String, Object> session, ConfluenceUser user, String passphrase) {
        long now = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        HashMap<String, String> revisionMeta = new HashMap<String, String>();
        if (user != null) {
            revisionMeta.put("userKey", user.getKey().getStringValue());
        }
        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
        builder.issuer(appId);
        builder.audience(serviceUri);
        builder.claim("access", access);
        builder.claim("iat", now);
        builder.claim("exp", now + TOKEN_EXPIRY_TIME);
        builder.claim("sub", user == null ? null : user.getKey().toString());
        builder.claim("session", session);
        builder.claim("revisionMeta", revisionMeta);
        if (this.synchronyConfigurationManager.isSynchronyEncryptionEnabled()) {
            builder.claim("passphrase", passphrase);
        }
        return builder.build();
    }

    private SignedJWT signedJWTFromClaims(JWTClaimsSet jwtClaims, String appSecret) throws Exception {
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), jwtClaims);
        MACSigner signer = new MACSigner(appSecret.getBytes());
        signedJWT.sign(signer);
        return signedJWT;
    }

    private JWEObject encryptSignedJWT(SignedJWT signedJWT, String publicKey) throws Exception {
        JWEObject jweObject = new JWEObject(new JWEHeader.Builder(JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A256GCM).contentType("JWT").build(), new Payload(signedJWT));
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(Base64.decodeBase64((String)publicKey));
        RSAPublicKey decodedPubKey = (RSAPublicKey)KeyFactory.getInstance("RSA").generatePublic(pubKeySpec);
        jweObject.encrypt(new RSAEncrypter(decodedPubKey));
        return jweObject;
    }

    private Map<String, Object> getAccessData(Long contentId, String appId, String permission) {
        HashMap<String, Object> access = new HashMap<String, Object>();
        access.put("/data/" + appId + "/confluence-" + contentId, permission);
        access.put("/data/" + appId + "/confluence-" + contentId + "-title", permission);
        return access;
    }

    private Map<String, Object> getSessionData(ConfluenceUser user) {
        HashMap<String, Object> session = new HashMap<String, Object>();
        session.put("fullname", user == null ? null : user.getFullName());
        session.put("name", user == null ? null : user.getName());
        session.put("avatarURL", this.userAccessor.getUserProfilePicture((User)user).getDownloadPath());
        return session;
    }
}


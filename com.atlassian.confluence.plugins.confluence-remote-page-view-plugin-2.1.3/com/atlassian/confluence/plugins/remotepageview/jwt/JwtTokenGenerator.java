/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.jwt.CanonicalHttpRequest
 *  com.atlassian.jwt.SigningAlgorithm
 *  com.atlassian.jwt.core.HttpRequestCanonicalizer
 *  com.atlassian.jwt.core.TimeUtil
 *  com.atlassian.jwt.core.writer.JsonSmartJwtJsonBuilder
 *  com.atlassian.jwt.core.writer.JwtClaimsBuilder
 *  com.atlassian.jwt.core.writer.NimbusJwtWriterFactory
 *  com.atlassian.jwt.httpclient.CanonicalHttpUriRequest
 *  com.atlassian.jwt.writer.JwtJsonBuilder
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 *  org.springframework.util.MultiValueMap
 *  org.springframework.web.util.UriComponentsBuilder
 */
package com.atlassian.confluence.plugins.remotepageview.jwt;

import com.atlassian.confluence.plugins.remotepageview.jwt.RemotePageViewJwtIssuer;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.jwt.CanonicalHttpRequest;
import com.atlassian.jwt.SigningAlgorithm;
import com.atlassian.jwt.core.HttpRequestCanonicalizer;
import com.atlassian.jwt.core.TimeUtil;
import com.atlassian.jwt.core.writer.JsonSmartJwtJsonBuilder;
import com.atlassian.jwt.core.writer.JwtClaimsBuilder;
import com.atlassian.jwt.core.writer.NimbusJwtWriterFactory;
import com.atlassian.jwt.httpclient.CanonicalHttpUriRequest;
import com.atlassian.jwt.writer.JwtJsonBuilder;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class JwtTokenGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenGenerator.class);
    private static final String JWT_EXPIRY_SECONDS_PROPERTY = "com.atlassian.remote_page_view.jwt.expiry_seconds";
    private static final int JWT_EXPIRY_WINDOW_SECONDS_DEFAULT = 10;
    static final int JWT_EXPIRY_WINDOW_SECONDS = Integer.getInteger("com.atlassian.remote_page_view.jwt.expiry_seconds", 10);
    public static final String USER_KEY = "userKey";
    private final SettingsManager settingsManager;
    private final RemotePageViewJwtIssuer jwtIssuer;

    @Autowired
    public JwtTokenGenerator(RemotePageViewJwtIssuer jwtIssuer, SettingsManager settingManager) {
        this.jwtIssuer = jwtIssuer;
        this.settingsManager = settingManager;
    }

    public String generate(String subject, String httpMethod, URI targetPath, String userKey) {
        String baseUrl = this.settingsManager.getGlobalSettings().getBaseUrl();
        return this.encodeJwt(httpMethod, targetPath, URI.create(baseUrl), this.jwtIssuer.getSharedSecret(), userKey, subject, this.jwtIssuer.getName());
    }

    private String encodeJwt(@Nonnull String httpMethod, @Nonnull URI targetPath, @Nonnull URI baseUrl, @Nonnull String secret, String userKey, String subject, String issuerId) {
        Objects.requireNonNull(httpMethod);
        Objects.requireNonNull(targetPath);
        Objects.requireNonNull(baseUrl);
        Objects.requireNonNull(secret);
        JwtJsonBuilder jsonBuilder = this.createJsonBuilder(userKey, issuerId, subject);
        try {
            CanonicalHttpUriRequest request = this.createCanonicalHttpRequest(httpMethod, targetPath, baseUrl);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Canonical request is: {}", (Object)HttpRequestCanonicalizer.canonicalize((CanonicalHttpRequest)request));
            }
            JwtClaimsBuilder.appendHttpRequestClaims((JwtJsonBuilder)jsonBuilder, (CanonicalHttpRequest)request);
        }
        catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            LOGGER.error("Error generating Jwt token for user {}", (Object)userKey, (Object)e);
            throw new RuntimeException(e);
        }
        String token = jsonBuilder.build();
        NimbusJwtWriterFactory jwtWriterFactory = new NimbusJwtWriterFactory();
        return jwtWriterFactory.macSigningWriter(SigningAlgorithm.HS256, secret).jsonToJwt(token);
    }

    private JwtJsonBuilder createJsonBuilder(String userKey, String issuerId, String subject) {
        return new JsonSmartJwtJsonBuilder().issuedAt(TimeUtil.currentTimeSeconds()).expirationTime(TimeUtil.currentTimePlusNSeconds((long)JWT_EXPIRY_WINDOW_SECONDS)).issuer(issuerId).subject(subject).claim(USER_KEY, (Object)userKey);
    }

    private CanonicalHttpUriRequest createCanonicalHttpRequest(String methodName, URI uri, URI hostBaseUri) {
        MultiValueMap queryParams = UriComponentsBuilder.fromUri((URI)uri).build().getQueryParams();
        return new CanonicalHttpUriRequest(methodName, uri.getPath(), hostBaseUri.getPath(), JwtTokenGenerator.paramsToArrayMap((MultiValueMap<String, String>)queryParams));
    }

    private static Map<String, String[]> paramsToArrayMap(MultiValueMap<String, String> queryParams) {
        HashMap<String, String[]> result = new HashMap<String, String[]>();
        for (Map.Entry entry : queryParams.entrySet()) {
            result.put((String)entry.getKey(), ((List)entry.getValue()).toArray(new String[0]));
        }
        return result;
    }
}


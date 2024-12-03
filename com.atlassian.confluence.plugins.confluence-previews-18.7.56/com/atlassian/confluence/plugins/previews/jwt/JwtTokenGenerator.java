/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.jwt.CanonicalHttpRequest
 *  com.atlassian.jwt.SigningAlgorithm
 *  com.atlassian.jwt.core.HttpRequestCanonicalizer
 *  com.atlassian.jwt.core.writer.JwtClaimsBuilder
 *  com.atlassian.jwt.core.writer.NimbusJwtWriterFactory
 *  com.atlassian.jwt.httpclient.CanonicalHttpUriRequest
 *  com.atlassian.jwt.writer.JwtJsonBuilder
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.previews.jwt;

import com.atlassian.confluence.plugins.previews.jwt.ConfluencePreviewsJwtIssuer;
import com.atlassian.confluence.plugins.previews.jwt.JwtTokenService;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.jwt.CanonicalHttpRequest;
import com.atlassian.jwt.SigningAlgorithm;
import com.atlassian.jwt.core.HttpRequestCanonicalizer;
import com.atlassian.jwt.core.writer.JwtClaimsBuilder;
import com.atlassian.jwt.core.writer.NimbusJwtWriterFactory;
import com.atlassian.jwt.httpclient.CanonicalHttpUriRequest;
import com.atlassian.jwt.writer.JwtJsonBuilder;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenGenerator {
    private static final String JWT_EXPIRY_SECONDS_PROPERTY = "com.atlassian.confluence_previews.jwt.expiry_seconds";
    private static final String JWT_SHORT_EXPIRY_SECONDS_PROPERTY = "com.atlassian.confluence_previews.jwt.short_expiry_seconds";
    private static final long JWT_EXPIRY_WINDOW_SECONDS_DEFAULT = 1209600L;
    static final long JWT_EXPIRY_WINDOW_SECONDS = Long.getLong("com.atlassian.confluence_previews.jwt.expiry_seconds", 1209600L);
    private static final long JWT_SHORT_EXPIRY_WINDOW_SECONDS_DEFAULT = 1800L;
    public static final long JWT_SHORT_EXPIRY_WINDOW_SECONDS = Long.getLong("com.atlassian.confluence_previews.jwt.short_expiry_seconds", 1800L);
    private static final Logger log = LoggerFactory.getLogger(JwtTokenGenerator.class);
    private final SettingsManager settingsManager;
    private final ConfluencePreviewsJwtIssuer jwtIssuer;
    private final JwtTokenService jwtTokenService;

    @Autowired
    public JwtTokenGenerator(ConfluencePreviewsJwtIssuer jwtIssuer, @ComponentImport SettingsManager settingManager, JwtTokenService jwtTokenService) {
        this.jwtIssuer = jwtIssuer;
        this.settingsManager = settingManager;
        this.jwtTokenService = jwtTokenService;
    }

    public String generate(String subject, String httpMethod, URI url, String userKey, String jwtId) {
        return this.encodeJwt(subject, httpMethod, url, URI.create(this.settingsManager.getGlobalSettings().getBaseUrl()), userKey, this.jwtIssuer.getName(), this.jwtIssuer.getSharedSecret(), -1L, jwtId);
    }

    public String generate(String subject, String httpMethod, URI url, String userKey, long expireTime, String jwtId) {
        return this.encodeJwt(subject, httpMethod, url, URI.create(this.settingsManager.getGlobalSettings().getBaseUrl()), userKey, this.jwtIssuer.getName(), this.jwtIssuer.getSharedSecret(), expireTime, jwtId);
    }

    private String encodeJwt(String subject, String httpMethod, URI targetPath, URI baseUrl, String userKey, String issuerId, String secret, long expireTime, String jwtId) {
        Objects.requireNonNull(httpMethod);
        Objects.requireNonNull(targetPath);
        Objects.requireNonNull(baseUrl);
        Objects.requireNonNull(secret);
        JwtJsonBuilder jsonBuilder = this.jwtTokenService.createJsonBuilder(userKey, issuerId).subject(subject).jwtId(jwtId == null ? UUID.randomUUID().toString() : jwtId);
        if (expireTime > 0L) {
            jsonBuilder.expirationTime(expireTime);
        }
        Map completeParams = Collections.emptyMap();
        try {
            if (StringUtils.isNotEmpty((CharSequence)targetPath.getQuery())) {
                completeParams = new HashMap<String, String[]>(this.constructParameterMap(targetPath));
            }
            CanonicalHttpUriRequest request = new CanonicalHttpUriRequest(httpMethod, this.extractRelativePath(targetPath, baseUrl), "", completeParams);
            log.debug("Canonical request is: " + HttpRequestCanonicalizer.canonicalize((CanonicalHttpRequest)request));
            JwtClaimsBuilder.appendHttpRequestClaims((JwtJsonBuilder)jsonBuilder, (CanonicalHttpRequest)request);
        }
        catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        NimbusJwtWriterFactory jwtWriterFactory = new NimbusJwtWriterFactory();
        String token = jsonBuilder.build();
        return jwtWriterFactory.macSigningWriter(SigningAlgorithm.HS256, secret).jsonToJwt(token);
    }

    private String extractRelativePath(URI targetUri, URI baseUri) {
        String path = targetUri.getRawPath();
        String targetString = targetUri.toString();
        String baseString = baseUri.toString();
        if (StringUtils.isEmpty((CharSequence)targetString) || StringUtils.isEmpty((CharSequence)baseString)) {
            return path;
        }
        if (targetString.startsWith(baseString)) {
            return URI.create(StringUtils.removeStart((String)targetString, (String)baseString)).getRawPath();
        }
        if (targetUri.isAbsolute()) {
            String message = String.format("Do not ask for the target URL '%s' to be signed for an add-on with a base URL of '%s': an absolute target URL should begin with the base URL.", targetString, baseString);
            throw new IllegalArgumentException(message);
        }
        return path;
    }

    private Map<String, String[]> constructParameterMap(URI uri) {
        String query = uri.getQuery();
        HashMap<String, List> queryParamsLists = new HashMap<String, List>();
        Arrays.stream(query.split("&")).map(str -> str.split("=")).forEach(pair -> {
            queryParamsLists.putIfAbsent(pair[0], new ArrayList());
            ((List)queryParamsLists.get(pair[0])).add(pair[1]);
        });
        HashMap<String, String[]> queryParamsArrays = new HashMap<String, String[]>();
        queryParamsLists.forEach((k, v) -> queryParamsArrays.put((String)k, v.toArray(new String[0])));
        return queryParamsArrays;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.fugue.Option
 *  com.atlassian.jwt.CanonicalHttpRequest
 *  com.atlassian.jwt.JwtIssuer
 *  com.atlassian.jwt.JwtService
 *  com.atlassian.jwt.core.HttpRequestCanonicalizer
 *  com.atlassian.jwt.core.TimeUtil
 *  com.atlassian.jwt.core.writer.JsonSmartJwtJsonBuilder
 *  com.atlassian.jwt.core.writer.JwtClaimsBuilder
 *  com.atlassian.jwt.httpclient.CanonicalHttpUriRequest
 *  com.atlassian.jwt.writer.JwtJsonBuilder
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.http.NameValuePair
 *  org.apache.http.message.BasicHeaderValueParser
 *  org.apache.http.message.ParserCursor
 *  org.apache.http.util.CharArrayBuffer
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.notifications;

import com.atlassian.config.ConfigurationException;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.fugue.Option;
import com.atlassian.jwt.CanonicalHttpRequest;
import com.atlassian.jwt.JwtIssuer;
import com.atlassian.jwt.JwtService;
import com.atlassian.jwt.core.HttpRequestCanonicalizer;
import com.atlassian.jwt.core.TimeUtil;
import com.atlassian.jwt.core.writer.JsonSmartJwtJsonBuilder;
import com.atlassian.jwt.core.writer.JwtClaimsBuilder;
import com.atlassian.jwt.httpclient.CanonicalHttpUriRequest;
import com.atlassian.jwt.writer.JwtJsonBuilder;
import com.google.common.base.Preconditions;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeaderValueParser;
import org.apache.http.message.ParserCursor;
import org.apache.http.util.CharArrayBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JwtTokenGenerator {
    private static final char[] QUERY_DELIMITERS = new char[]{'&'};
    private static final String JWT_EXPIRY_SECONDS_PROPERTY = "com.atlassian.confluence_notifications.jwt.expiry_seconds";
    private static final String XSRF_SUBJECT_PREFIX = "xsrf:";
    private static final int JWT_EXPIRY_WINDOW_SECONDS_DEFAULT = 604800;
    private static final int JWT_EXPIRY_WINDOW_SECONDS = Integer.getInteger("com.atlassian.confluence_notifications.jwt.expiry_seconds", 604800);
    private static final Logger log = LoggerFactory.getLogger(JwtTokenGenerator.class);
    private final JwtService jwtService;
    private final SettingsManager settingsManager;
    private final JwtIssuer jwtIssuer;

    public JwtTokenGenerator(@Nonnull JwtService jwtService, @Nonnull JwtIssuer jwtIssuer, @Nonnull SettingsManager settingManager) throws ConfigurationException {
        this.jwtIssuer = jwtIssuer;
        this.jwtService = jwtService;
        this.settingsManager = settingManager;
    }

    private static String urlDecode(String content) throws UnsupportedEncodingException {
        return null == content ? null : URLDecoder.decode(content, "UTF-8");
    }

    public Option<String> generate(String httpMethod, URI url, String userKey) {
        return Option.some((Object)this.generate(httpMethod, url, URI.create(this.settingsManager.getGlobalSettings().getBaseUrl()), userKey, this.jwtIssuer.getSharedSecret()));
    }

    public String generate(String httpMethod, URI url, URI baseUri, String userKey, String secret) {
        return this.encodeJwt(httpMethod, url, baseUri, userKey, this.jwtIssuer.getName(), this.jwtService, secret);
    }

    public String encodeJwt(String httpMethod, URI targetPath, URI baseUrl, String userKey, String issuerId, JwtService jwtService, String secret) {
        Preconditions.checkArgument((null != httpMethod ? 1 : 0) != 0, (Object)"HttpMethod argument cannot be null");
        Preconditions.checkArgument((null != targetPath ? 1 : 0) != 0, (Object)"URI argument cannot be null");
        Preconditions.checkArgument((null != baseUrl ? 1 : 0) != 0, (Object)"base URI argument cannot be null");
        Preconditions.checkArgument((null != secret ? 1 : 0) != 0, (Object)"secret argument cannot be null");
        JwtJsonBuilder jsonBuilder = new JsonSmartJwtJsonBuilder().issuedAt(TimeUtil.currentTimeSeconds()).expirationTime(TimeUtil.currentTimePlusNSeconds((long)JWT_EXPIRY_WINDOW_SECONDS)).issuer(issuerId);
        jsonBuilder.subject(XSRF_SUBJECT_PREFIX + userKey);
        Map<String, String[]> completeParams = Collections.emptyMap();
        try {
            if (!StringUtils.isEmpty((CharSequence)targetPath.getQuery())) {
                completeParams = new HashMap();
                completeParams.putAll(this.constructParameterMap(targetPath));
            }
            CanonicalHttpUriRequest request = new CanonicalHttpUriRequest(httpMethod, this.extractRelativePath(targetPath, baseUrl), "", completeParams);
            log.debug("Canonical request is: " + HttpRequestCanonicalizer.canonicalize((CanonicalHttpRequest)request));
            JwtClaimsBuilder.appendHttpRequestClaims((JwtJsonBuilder)jsonBuilder, (CanonicalHttpRequest)request);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return jwtService.issueJwt(jsonBuilder.build(), secret);
    }

    private String extractRelativePath(URI targetUri, URI baseUri) {
        String path = targetUri.getPath();
        String targetString = targetUri.toString();
        String baseString = baseUri.toString();
        if (!StringUtils.isEmpty((CharSequence)targetString) && !StringUtils.isEmpty((CharSequence)baseString)) {
            if (targetString.startsWith(baseString)) {
                path = URI.create(StringUtils.removeStart((String)targetString, (String)baseString)).getPath();
            } else if (targetUri.isAbsolute()) {
                String message = String.format("Do not ask for the target URL '%s' to be signed for an add-on with a base URL of '%s': an absolute target URL should begin with the base URL.", targetString, baseString);
                throw new IllegalArgumentException(message);
            }
        }
        return path;
    }

    private Map<String, String[]> constructParameterMap(URI uri) throws UnsupportedEncodingException {
        String query = uri.getRawQuery();
        if (query == null) {
            return Collections.emptyMap();
        }
        HashMap<String, String[]> queryParams = new HashMap<String, String[]>();
        CharArrayBuffer buffer = new CharArrayBuffer(query.length());
        buffer.append(query);
        ParserCursor cursor = new ParserCursor(0, buffer.length());
        while (!cursor.atEnd()) {
            NameValuePair nameValuePair = BasicHeaderValueParser.DEFAULT.parseNameValuePair(buffer, cursor, QUERY_DELIMITERS);
            if (StringUtils.isEmpty((CharSequence)nameValuePair.getName())) continue;
            String decodedName = JwtTokenGenerator.urlDecode(nameValuePair.getName());
            String decodedValue = JwtTokenGenerator.urlDecode(nameValuePair.getValue());
            String[] oldValues = (String[])queryParams.get(decodedName);
            String[] newValues = null == oldValues ? new String[1] : Arrays.copyOf(oldValues, oldValues.length + 1);
            newValues[newValues.length - 1] = decodedValue;
            queryParams.put(decodedName, newValues);
        }
        return queryParams;
    }
}


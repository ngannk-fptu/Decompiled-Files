/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.security.random.SecureTokenGenerator
 *  javax.xml.bind.DatatypeConverter
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.emailtracker.impl;

import com.atlassian.confluence.plugins.emailtracker.EmailUrlValidator;
import com.atlassian.confluence.plugins.emailtracker.InvalidTrackingRequestException;
import com.atlassian.confluence.plugins.emailtracker.impl.EmailTrackerServiceImpl;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.security.random.SecureTokenGenerator;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component(value="emailUrlValidator")
public class EmailUrlValidatorImpl
implements EmailUrlValidator {
    private static final Logger log = LoggerFactory.getLogger(EmailTrackerServiceImpl.class);
    static final String PLUGIN_TOKEN_SETTING_KEY = "com.atlassian.confluence.plugins.confluence-email-tracker:url-validator-token";
    static final String HASH = "hash";
    private final SecureTokenGenerator secureTokenGenerator;
    private final SettingsManager settingsManager;
    private String token;

    public EmailUrlValidatorImpl(@ComponentImport SecureTokenGenerator secureTokenGenerator, @ComponentImport SettingsManager settingsManager) {
        this.secureTokenGenerator = secureTokenGenerator;
        this.settingsManager = settingsManager;
    }

    @Override
    public Map<String, String> addValidationDataToQueryParameters(String urlToQuery, Map<String, String> queryParams) {
        HashMap<String, String> updatedParams = new HashMap<String, String>(queryParams);
        updatedParams.put(HASH, this.makeHash(urlToQuery, queryParams));
        return updatedParams;
    }

    @Override
    public Map<String, String> validateQueryParameters(String urlToQuery, Map<String, String> queryParams) throws InvalidTrackingRequestException {
        HashMap<String, String> updatedParams = new HashMap<String, String>(queryParams);
        String actualHash = (String)updatedParams.remove(HASH);
        if (StringUtils.isBlank((CharSequence)actualHash)) {
            throw new InvalidTrackingRequestException("No tracking hash included in request");
        }
        String expectedHash = this.makeHash(urlToQuery, updatedParams);
        if (!expectedHash.equals(actualHash)) {
            throw new InvalidTrackingRequestException("Invalid hash included in request");
        }
        return updatedParams;
    }

    private String makeHash(String urlToQuery, Map<String, String> queryParams) {
        String queryString = this.convertToSortedString(queryParams);
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(urlToQuery.getBytes(StandardCharsets.UTF_8));
            md5.update(queryString.getBytes(StandardCharsets.UTF_8));
            md5.update(this.getSecureToken().getBytes(StandardCharsets.UTF_8));
            return DatatypeConverter.printBase64Binary((byte[])md5.digest());
        }
        catch (NoSuchAlgorithmException e) {
            log.warn("Can't make MD5 hash for email-tracking URL: NoSuchAlgorithmException. Leaving blank.");
            return "";
        }
    }

    private String getSecureToken() {
        if (this.token == null) {
            this.token = (String)((Object)this.settingsManager.getPluginSettings(PLUGIN_TOKEN_SETTING_KEY));
            if (this.token == null) {
                this.token = this.secureTokenGenerator.generateToken();
                this.settingsManager.updatePluginSettings(PLUGIN_TOKEN_SETTING_KEY, (Serializable)((Object)this.token));
            }
        }
        return this.token;
    }

    private String convertToSortedString(Map<String, String> queryParams) {
        StringBuilder builder = new StringBuilder();
        TreeSet<String> keys = new TreeSet<String>(queryParams.keySet());
        for (String key : keys) {
            builder.append(key).append('=').append(queryParams.get(key)).append('&');
        }
        return builder.toString();
    }
}


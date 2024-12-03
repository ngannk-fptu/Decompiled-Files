/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.core.AtlassianCoreException
 *  com.atlassian.core.user.preferences.UserPreferences
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.security.random.SecureTokenGenerator
 *  com.atlassian.user.User
 *  javax.xml.bind.DatatypeConverter
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.dailysummary.components.impl;

import com.atlassian.confluence.plugins.dailysummary.components.SingleUseUnsubscribeTokenManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.core.AtlassianCoreException;
import com.atlassian.core.user.preferences.UserPreferences;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.security.random.SecureTokenGenerator;
import com.atlassian.user.User;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DefaultUnsubscribeTokenManager
implements SingleUseUnsubscribeTokenManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultUnsubscribeTokenManager.class);
    static final String USER_HASH_KEY = "confluence.prefs.plugins.dailysummary.unsubscribe.hashkey";
    static final String USER_TOKEN_KEY = "confluence.prefs.plugins.dailysummary.unsubscribe.tokenkey";
    private final UserAccessor userAccessor;
    private final SecureTokenGenerator secureTokenGenerator;

    public DefaultUnsubscribeTokenManager(@ComponentImport UserAccessor userAccessor, @ComponentImport SecureTokenGenerator secureTokenGenerator) {
        this.userAccessor = userAccessor;
        this.secureTokenGenerator = secureTokenGenerator;
    }

    @Override
    public String getUserToken(User user) {
        try {
            boolean requireNewToken;
            UserPreferences userPrefs = this.userAccessor.getUserPreferences(user);
            String token = userPrefs.getString(USER_TOKEN_KEY);
            boolean bl = requireNewToken = token == null;
            if (!requireNewToken) {
                String storedHash;
                String hash = this.hashUserDetailsAndToken(user, token);
                boolean bl2 = requireNewToken = !hash.equals(storedHash = userPrefs.getString(USER_HASH_KEY));
            }
            if (requireNewToken) {
                return this.generatePublicToken(userPrefs, user);
            }
            return token;
        }
        catch (Exception ex) {
            log.warn(String.format("Could not generate token for user : %s", user.getName()), (Throwable)ex);
            return null;
        }
    }

    private String generatePublicToken(UserPreferences prefs, User user) throws AtlassianCoreException, NoSuchAlgorithmException, UnsupportedEncodingException {
        String tokenStr = this.secureTokenGenerator.generateToken();
        prefs.setString(USER_TOKEN_KEY, tokenStr);
        prefs.setString(USER_HASH_KEY, this.hashUserDetailsAndToken(user, tokenStr));
        return tokenStr;
    }

    private String hashUserDetailsAndToken(User user, String token) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(user.getEmail().getBytes("UTF-8"));
        md5.update(user.getName().getBytes("UTF-8"));
        md5.update(token.getBytes("UTF-8"));
        return DatatypeConverter.printBase64Binary((byte[])md5.digest());
    }

    @Override
    public boolean isValidToken(User user, String token) {
        try {
            if (user == null) {
                return false;
            }
            UserPreferences userPrefs = this.userAccessor.getUserPreferences(user);
            String storedToken = userPrefs.getString(USER_TOKEN_KEY);
            if (StringUtils.isBlank((CharSequence)token)) {
                return false;
            }
            if (!token.equals(storedToken)) {
                return false;
            }
            String storedHash = userPrefs.getString(USER_HASH_KEY);
            if (StringUtils.isBlank((CharSequence)storedHash)) {
                return false;
            }
            boolean valid = this.hashUserDetailsAndToken(user, token).equals(storedHash);
            this.removeToken(user);
            return valid;
        }
        catch (Exception ex) {
            log.warn(String.format("Exception in validating unsubscribe token: %s, for user: %s", user, token), (Throwable)ex);
            return false;
        }
    }

    private void removeToken(User user) throws Exception {
        UserPreferences prefs = this.userAccessor.getUserPreferences(user);
        prefs.remove(USER_TOKEN_KEY);
        prefs.remove(USER_HASH_KEY);
    }
}


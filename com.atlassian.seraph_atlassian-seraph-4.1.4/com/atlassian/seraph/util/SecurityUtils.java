/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.seraph.util;

import com.atlassian.seraph.auth.Authenticator;
import com.atlassian.seraph.config.SecurityConfig;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityUtils {
    private static final Logger log = LoggerFactory.getLogger(SecurityUtils.class);
    private static final String BASIC_AUTHZ_TYPE_PREFIX = "Basic ";
    private static final String ALREADY_FILTERED = "loginfilter.already.filtered";

    public static Authenticator getAuthenticator(ServletContext servletContext) {
        SecurityConfig securityConfig = (SecurityConfig)servletContext.getAttribute("seraph_config");
        if (securityConfig.getAuthenticator() == null) {
            log.error("ack! Authenticator is null!!!");
        }
        return securityConfig.getAuthenticator();
    }

    public static boolean isBasicAuthorizationHeader(String header) {
        return header != null && header.startsWith(BASIC_AUTHZ_TYPE_PREFIX);
    }

    public static UserPassCredentials decodeBasicAuthorizationCredentials(String basicAuthorizationHeader) {
        String base64Token = basicAuthorizationHeader.substring(BASIC_AUTHZ_TYPE_PREFIX.length());
        byte[] bytes = Base64.getDecoder().decode(base64Token);
        String token = new String(bytes, StandardCharsets.ISO_8859_1);
        String userName = "";
        String password = "";
        int delim = token.indexOf(":");
        if (delim != -1) {
            userName = token.substring(0, delim);
            password = token.substring(delim + 1);
        }
        return new UserPassCredentials(userName, password);
    }

    public static String encodeBasicAuthorizationCredentials(String username, String password) {
        byte[] bytes = (username + ":" + password).getBytes(StandardCharsets.ISO_8859_1);
        return BASIC_AUTHZ_TYPE_PREFIX + Base64.getEncoder().encodeToString(bytes);
    }

    public static void disableSeraphFiltering(ServletRequest request) {
        request.setAttribute(ALREADY_FILTERED, (Object)true);
    }

    public static boolean isSeraphFilteringDisabled(ServletRequest request) {
        return request.getAttribute(ALREADY_FILTERED) != null;
    }

    public static class UserPassCredentials {
        private final String username;
        private final String password;

        public UserPassCredentials(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return this.username;
        }

        public String getPassword() {
            return this.password;
        }
    }
}


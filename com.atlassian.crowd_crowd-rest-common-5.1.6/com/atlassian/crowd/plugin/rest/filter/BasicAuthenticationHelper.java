/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.codec.binary.Base64
 */
package com.atlassian.crowd.plugin.rest.filter;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;

public final class BasicAuthenticationHelper {
    private BasicAuthenticationHelper() {
    }

    public static Credentials getBasicAuthCredentials(HttpServletRequest request) {
        String base64Token;
        String token;
        int delim;
        Credentials credentials = null;
        String header = request.getHeader("Authorization");
        if (header != null && header.substring(0, 5).equalsIgnoreCase("Basic") && (delim = (token = new String(Base64.decodeBase64((byte[])(base64Token = header.substring(6)).getBytes()))).indexOf(":")) != -1) {
            String name = token.substring(0, delim);
            String password = token.substring(delim + 1);
            credentials = new Credentials(name, password);
        }
        return credentials;
    }

    public static void respondWithChallenge(HttpServletResponse response, String message, String basicAuthRealm) throws IOException {
        response.setStatus(401);
        response.setHeader("WWW-Authenticate", "BASIC realm=\"" + basicAuthRealm + "\"");
        response.setHeader("Content-Type", "text/plain;charset=UTF-8");
        response.getWriter().print(message);
        response.flushBuffer();
    }

    public static class Credentials {
        private final String name;
        private final String password;

        private Credentials(String name, String password) {
            this.name = name;
            this.password = password;
        }

        public String getName() {
            return this.name;
        }

        public String getPassword() {
            return this.password;
        }
    }
}


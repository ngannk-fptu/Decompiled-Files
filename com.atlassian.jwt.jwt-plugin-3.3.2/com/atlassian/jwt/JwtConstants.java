/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.jwt;

import com.atlassian.jwt.reader.JwtClaimVerifier;
import java.util.Collections;
import java.util.Map;

public class JwtConstants {
    public static final String JWT_PARAM_NAME = "jwt";
    public static final int TIME_CLAIM_LEEWAY_SECONDS = 30;

    public static final class HttpRequests {
        public static final String ADD_ON_ID_ATTRIBUTE_NAME = "Plugin-Key";
        public static final String JWT_JSON_PAYLOAD_ATTRIBUTE_NAME = "jwt.payload";
        public static final String JWT_SUBJECT_ATTRIBUTE_NAME = "jwt.subject";
        public static final String AUTHORIZATION_HEADER = "Authorization";
        public static final String JWT_AUTH_HEADER_PREFIX = "JWT ";
    }

    public static final class AppLinks {
        public static final String ADD_ON_ID_PROPERTY_NAME = "plugin-key";
        public static final String AUTH_METHOD_PROPERTY_NAME = "atlassian.auth.method";
        public static final String SHARED_SECRET_PROPERTY_NAME = "atlassian.jwt.shared.secret";
        public static final String JWT_AUTH_METHOD_NAME = "JWT";
        public static final String ADD_ON_USER_KEY_PROPERTY_NAME = "user.key";
        public static final String SYS_PROP_ALLOW_IMPERSONATION = "atlassian.jwt.impersonation.allowed";
    }

    public static final class ClaimVerifiers {
        public static final Map<String, JwtClaimVerifier> NO_REQUIRED_CLAIMS = Collections.emptyMap();
    }

    public static final class Claims {
        public static final String QUERY_HASH = "qsh";
        public static final String SUBJECT = "sub";
    }
}


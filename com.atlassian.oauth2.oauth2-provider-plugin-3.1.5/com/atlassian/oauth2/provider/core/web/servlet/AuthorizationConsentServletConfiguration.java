/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.provider.core.web.servlet;

public interface AuthorizationConsentServletConfiguration {
    public String moduleKey();

    public String templateName();

    public String consentServletUri();

    public String productName();

    public static enum QueryParameter {
        CLIENT_ID_PARAMETER("client_id"),
        REDIRECT_URI_PARAMETER("redirect_uri"),
        RESPONSE_TYPE("response_type"),
        STATE("state"),
        SCOPE("scope"),
        CODE_CHALLENGE("code_challenge"),
        CODE_CHALLENGE_METHOD("code_challenge_method");

        public final String name;

        private QueryParameter(String name) {
            this.name = name;
        }
    }
}


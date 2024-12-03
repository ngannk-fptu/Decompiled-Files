/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.provider.core.web.servlet;

public interface AuthorizationErrorServletConfiguration {
    public String moduleKey();

    public String templateName();

    public String errorServletUri();

    public static enum QueryParameter {
        ERROR_NAME("errorName"),
        ERROR_DESCRIPTION("errorDescription");

        public final String name;

        private QueryParameter(String name) {
            this.name = name;
        }
    }
}


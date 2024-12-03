/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.common.org.springframework.util.StringUtils
 *  javax.servlet.http.HttpSession
 */
package com.atlassian.confluence.mail.archive.oauth;

import com.atlassian.extras.common.org.springframework.util.StringUtils;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import javax.servlet.http.HttpSession;

public interface OAuthManager {
    public List<OAuthProvider> getConfiguredOAuthProvider();

    public OAuthResult initialiseOAuthFlow(HttpSession var1, String var2, Function<String, String> var3);

    public String completeOAuthFlow(HttpSession var1, String var2) throws Exception;

    public static class OAuthProvider {
        private final String id;
        private final String name;
        private final String type;

        public OAuthProvider(String id, String name, String type) {
            this.id = Objects.requireNonNull(id);
            this.name = Objects.requireNonNull(name);
            this.type = Objects.requireNonNull(type);
        }

        public String toString() {
            return String.format("%s - %s - OAuth 2.0", this.name, StringUtils.capitalize((String)this.type));
        }

        public String getId() {
            return this.id;
        }
    }

    public static class OAuthResult {
        private final String flowId;
        private final String redirectUrl;

        public OAuthResult(String flowId, String redirectUrl) {
            this.flowId = Objects.requireNonNull(flowId);
            this.redirectUrl = Objects.requireNonNull(redirectUrl);
        }

        public String getFlowId() {
            return this.flowId;
        }

        public String getRedirectUrl() {
            return this.redirectUrl;
        }
    }
}


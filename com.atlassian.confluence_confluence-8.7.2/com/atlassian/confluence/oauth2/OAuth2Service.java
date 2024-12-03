/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.client.api.storage.token.ClientTokenEntity
 *  javax.servlet.http.HttpSession
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.oauth2;

import com.atlassian.confluence.oauth2.OAuth2Exception;
import com.atlassian.oauth2.client.api.storage.token.ClientTokenEntity;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;

public interface OAuth2Service {
    public List<OAuth2Provider> getConfiguredOAuth2Providers();

    public OAuth2Result initialiseOAuth2Flow(HttpSession var1, String var2, UnaryOperator<String> var3) throws IllegalArgumentException;

    public String completeOAuth2Flow(HttpSession var1, String var2) throws OAuth2Exception;

    public ClientTokenEntity getToken(String var1) throws OAuth2Exception;

    public static class OAuth2Provider {
        private final String id;
        private final String name;
        private final String type;

        public OAuth2Provider(String id, String name, String type) {
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

    public static class OAuth2Result {
        private final String flowId;
        private final String redirectUrl;

        public OAuth2Result(String flowId, String redirectUrl) {
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


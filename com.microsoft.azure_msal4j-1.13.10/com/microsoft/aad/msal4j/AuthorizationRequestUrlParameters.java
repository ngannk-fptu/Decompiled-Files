/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.oauth2.sdk.util.URLUtils
 *  lombok.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.Authority;
import com.microsoft.aad.msal4j.ClaimsRequest;
import com.microsoft.aad.msal4j.JsonHelper;
import com.microsoft.aad.msal4j.MsalClientException;
import com.microsoft.aad.msal4j.ParameterValidationUtils;
import com.microsoft.aad.msal4j.Prompt;
import com.microsoft.aad.msal4j.ResponseMode;
import com.nimbusds.oauth2.sdk.util.URLUtils;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthorizationRequestUrlParameters {
    @NonNull
    private String redirectUri;
    @NonNull
    private Set<String> scopes;
    private String codeChallenge;
    private String codeChallengeMethod;
    private String state;
    private String nonce;
    private ResponseMode responseMode;
    private String loginHint;
    private String domainHint;
    private Prompt prompt;
    private String correlationId;
    private boolean instanceAware;
    private static final String ADMIN_CONSENT_ENDPOINT = "https://login.microsoftonline.com/{tenant}/adminconsent";
    Map<String, String> extraQueryParameters;
    Map<String, List<String>> requestParameters = new HashMap<String, List<String>>();
    Logger log = LoggerFactory.getLogger(AuthorizationRequestUrlParameters.class);

    public static Builder builder(String redirectUri, Set<String> scopes) {
        ParameterValidationUtils.validateNotBlank("redirect_uri", redirectUri);
        ParameterValidationUtils.validateNotNull("scopes", scopes);
        return AuthorizationRequestUrlParameters.builder().redirectUri(redirectUri).scopes(scopes);
    }

    private static Builder builder() {
        return new Builder();
    }

    private AuthorizationRequestUrlParameters(Builder builder) {
        this.redirectUri = builder.redirectUri;
        this.requestParameters.put("redirect_uri", Collections.singletonList(this.redirectUri));
        this.scopes = builder.scopes;
        String[] commonScopes = "openid profile offline_access".split(" ");
        LinkedHashSet<String> scopesParam = new LinkedHashSet<String>(Arrays.asList(commonScopes));
        scopesParam.addAll(builder.scopes);
        if (builder.extraScopesToConsent != null) {
            scopesParam.addAll(builder.extraScopesToConsent);
        }
        this.scopes = scopesParam;
        this.requestParameters.put("scope", Collections.singletonList(String.join((CharSequence)" ", scopesParam)));
        this.requestParameters.put("response_type", Collections.singletonList("code"));
        if (builder.claims != null) {
            String claimsParam = String.join((CharSequence)" ", builder.claims);
            this.requestParameters.put("claims", Collections.singletonList(claimsParam));
        }
        if (builder.claimsChallenge != null && builder.claimsChallenge.trim().length() > 0) {
            JsonHelper.validateJsonFormat(builder.claimsChallenge);
            this.requestParameters.put("claims", Collections.singletonList(builder.claimsChallenge));
        }
        if (builder.claimsRequest != null) {
            String claimsRequest = builder.claimsRequest.formatAsJSONString();
            if (this.requestParameters.get("claims") != null) {
                claimsRequest = JsonHelper.mergeJSONString(claimsRequest, this.requestParameters.get("claims").get(0));
            }
            this.requestParameters.put("claims", Collections.singletonList(claimsRequest));
        }
        if (builder.codeChallenge != null) {
            this.codeChallenge = builder.codeChallenge;
            this.requestParameters.put("code_challenge", Collections.singletonList(builder.codeChallenge));
        }
        if (builder.codeChallengeMethod != null) {
            this.codeChallengeMethod = builder.codeChallengeMethod;
            this.requestParameters.put("code_challenge_method", Collections.singletonList(builder.codeChallengeMethod));
        }
        if (builder.state != null) {
            this.state = builder.state;
            this.requestParameters.put("state", Collections.singletonList(builder.state));
        }
        if (builder.nonce != null) {
            this.nonce = builder.nonce;
            this.requestParameters.put("nonce", Collections.singletonList(builder.nonce));
        }
        if (builder.responseMode != null) {
            this.responseMode = builder.responseMode;
            this.requestParameters.put("response_mode", Collections.singletonList(builder.responseMode.toString()));
        } else {
            this.responseMode = ResponseMode.FORM_POST;
            this.requestParameters.put("response_mode", Collections.singletonList(ResponseMode.FORM_POST.toString()));
        }
        if (builder.loginHint != null) {
            this.loginHint = this.loginHint();
            this.requestParameters.put("login_hint", Collections.singletonList(builder.loginHint));
            this.requestParameters.put("X-AnchorMailbox", Collections.singletonList(String.format("upn:%s", builder.loginHint)));
        }
        if (builder.domainHint != null) {
            this.domainHint = this.domainHint();
            this.requestParameters.put("domain_hint", Collections.singletonList(builder.domainHint));
        }
        if (builder.prompt != null) {
            this.prompt = builder.prompt;
            this.requestParameters.put("prompt", Collections.singletonList(builder.prompt.toString()));
        }
        if (builder.correlationId != null) {
            this.correlationId = builder.correlationId;
            this.requestParameters.put("correlation_id", Collections.singletonList(builder.correlationId));
        }
        if (builder.instanceAware) {
            this.instanceAware = builder.instanceAware;
            this.requestParameters.put("instance_aware", Collections.singletonList(String.valueOf(this.instanceAware)));
        }
        if (null != builder.extraQueryParameters && !builder.extraQueryParameters.isEmpty()) {
            this.extraQueryParameters = builder.extraQueryParameters;
            for (Map.Entry<String, String> entry : this.extraQueryParameters.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (this.requestParameters.containsKey(key)) {
                    this.log.warn("A query parameter {} has been provided with values multiple times.", (Object)key);
                }
                this.requestParameters.put(key, Collections.singletonList(value));
            }
        }
    }

    URL createAuthorizationURL(Authority authority, Map<String, List<String>> requestParameters) {
        URL authorizationRequestUrl;
        try {
            String authorizationCodeEndpoint = this.prompt == Prompt.ADMIN_CONSENT ? ADMIN_CONSENT_ENDPOINT.replace("{tenant}", authority.tenant) : authority.authorizationEndpoint();
            String uriString = authorizationCodeEndpoint + "?" + URLUtils.serializeParameters(requestParameters);
            authorizationRequestUrl = new URL(uriString);
        }
        catch (MalformedURLException ex) {
            throw new MsalClientException(ex);
        }
        return authorizationRequestUrl;
    }

    @NonNull
    public String redirectUri() {
        return this.redirectUri;
    }

    @NonNull
    public Set<String> scopes() {
        return this.scopes;
    }

    public String codeChallenge() {
        return this.codeChallenge;
    }

    public String codeChallengeMethod() {
        return this.codeChallengeMethod;
    }

    public String state() {
        return this.state;
    }

    public String nonce() {
        return this.nonce;
    }

    public ResponseMode responseMode() {
        return this.responseMode;
    }

    public String loginHint() {
        return this.loginHint;
    }

    public String domainHint() {
        return this.domainHint;
    }

    public Prompt prompt() {
        return this.prompt;
    }

    public String correlationId() {
        return this.correlationId;
    }

    public boolean instanceAware() {
        return this.instanceAware;
    }

    public Map<String, String> extraQueryParameters() {
        return this.extraQueryParameters;
    }

    public Map<String, List<String>> requestParameters() {
        return this.requestParameters;
    }

    public Logger log() {
        return this.log;
    }

    public static class Builder {
        private String redirectUri;
        private Set<String> scopes;
        private Set<String> extraScopesToConsent;
        private Set<String> claims;
        private String claimsChallenge;
        private ClaimsRequest claimsRequest;
        private String codeChallenge;
        private String codeChallengeMethod;
        private String state;
        private String nonce;
        private ResponseMode responseMode;
        private String loginHint;
        private String domainHint;
        private Prompt prompt;
        private String correlationId;
        private boolean instanceAware;
        private Map<String, String> extraQueryParameters;

        public AuthorizationRequestUrlParameters build() {
            return new AuthorizationRequestUrlParameters(this);
        }

        private Builder self() {
            return this;
        }

        public Builder redirectUri(String val) {
            this.redirectUri = val;
            return this.self();
        }

        public Builder scopes(Set<String> val) {
            this.scopes = val;
            return this.self();
        }

        public Builder extraScopesToConsent(Set<String> val) {
            this.extraScopesToConsent = val;
            return this.self();
        }

        public Builder claimsChallenge(String val) {
            this.claimsChallenge = val;
            return this.self();
        }

        public Builder claims(ClaimsRequest val) {
            this.claimsRequest = val;
            return this.self();
        }

        public Builder codeChallenge(String val) {
            this.codeChallenge = val;
            return this.self();
        }

        public Builder codeChallengeMethod(String val) {
            this.codeChallengeMethod = val;
            return this.self();
        }

        public Builder state(String val) {
            this.state = val;
            return this.self();
        }

        public Builder nonce(String val) {
            this.nonce = val;
            return this.self();
        }

        public Builder responseMode(ResponseMode val) {
            this.responseMode = val;
            return this.self();
        }

        public Builder loginHint(String val) {
            this.loginHint = val;
            return this.self();
        }

        public Builder domainHint(String val) {
            this.domainHint = val;
            return this.self();
        }

        public Builder prompt(Prompt val) {
            this.prompt = val;
            return this.self();
        }

        public Builder correlationId(String val) {
            this.correlationId = val;
            return this.self();
        }

        public Builder instanceAware(boolean val) {
            this.instanceAware = val;
            return this.self();
        }

        public Builder extraQueryParameters(Map<String, String> val) {
            this.extraQueryParameters = val;
            return this.self();
        }
    }
}


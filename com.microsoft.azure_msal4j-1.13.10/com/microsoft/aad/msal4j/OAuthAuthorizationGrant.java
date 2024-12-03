/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.oauth2.sdk.AuthorizationGrant
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.AbstractMsalAuthorizationGrant;
import com.microsoft.aad.msal4j.ClaimsRequest;
import com.microsoft.aad.msal4j.StringHelper;
import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class OAuthAuthorizationGrant
extends AbstractMsalAuthorizationGrant {
    private AuthorizationGrant grant;
    private final Map<String, List<String>> params = new LinkedHashMap<String, List<String>>();

    OAuthAuthorizationGrant(AuthorizationGrant grant, Set<String> scopesSet, ClaimsRequest claims) {
        this(grant, scopesSet != null ? String.join((CharSequence)" ", scopesSet) : null, claims);
    }

    String addCommonScopes(String scopes) {
        HashSet<String> allScopes = new HashSet<String>(Arrays.asList("openid profile offline_access".split(" ")));
        if (!StringHelper.isBlank(scopes)) {
            allScopes.addAll(Arrays.asList(scopes.split(" ")));
        }
        return String.join((CharSequence)" ", allScopes);
    }

    OAuthAuthorizationGrant(AuthorizationGrant grant, String scopes, ClaimsRequest claims) {
        String allScopes;
        this.grant = grant;
        this.scopes = allScopes = this.addCommonScopes(scopes);
        this.params.put("scope", Collections.singletonList(allScopes));
        if (claims != null) {
            this.claims = claims;
            this.params.put("claims", Collections.singletonList(claims.formatAsJSONString()));
        }
    }

    OAuthAuthorizationGrant(AuthorizationGrant grant, String scopes, Map<String, List<String>> extraParams) {
        String allScopes;
        this.grant = grant;
        this.scopes = allScopes = this.addCommonScopes(scopes);
        this.params.put("scope", Collections.singletonList(allScopes));
        if (extraParams != null) {
            this.params.putAll(extraParams);
        }
    }

    OAuthAuthorizationGrant(AuthorizationGrant grant, Map<String, List<String>> params) {
        this.grant = grant;
        if (params != null) {
            this.params.putAll(params);
        }
    }

    @Override
    public Map<String, List<String>> toParameters() {
        LinkedHashMap<String, List<String>> outParams = new LinkedHashMap<String, List<String>>();
        outParams.putAll(this.params);
        outParams.put("client_info", Collections.singletonList("1"));
        outParams.putAll(this.grant.toParameters());
        if (this.claims != null) {
            outParams.put("claims", Collections.singletonList(this.claims.formatAsJSONString()));
        }
        return Collections.unmodifiableMap(outParams);
    }

    AuthorizationGrant getAuthorizationGrant() {
        return this.grant;
    }

    Map<String, List<String>> getParameters() {
        return this.params;
    }
}


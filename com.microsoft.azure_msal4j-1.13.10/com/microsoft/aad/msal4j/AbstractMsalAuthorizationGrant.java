/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.ClaimsRequest;
import java.util.List;
import java.util.Map;

abstract class AbstractMsalAuthorizationGrant {
    static final String SCOPE_PARAM_NAME = "scope";
    static final String SCOPES_DELIMITER = " ";
    static final String SCOPE_OPEN_ID = "openid";
    static final String SCOPE_PROFILE = "profile";
    static final String SCOPE_OFFLINE_ACCESS = "offline_access";
    static final String COMMON_SCOPES_PARAM = "openid profile offline_access";
    String scopes;
    ClaimsRequest claims;

    AbstractMsalAuthorizationGrant() {
    }

    abstract Map<String, List<String>> toParameters();

    String getScopes() {
        return this.scopes;
    }

    ClaimsRequest getClaims() {
        return this.claims;
    }
}


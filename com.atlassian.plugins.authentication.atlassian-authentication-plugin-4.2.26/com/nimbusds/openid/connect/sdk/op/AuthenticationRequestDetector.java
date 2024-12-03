/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.op;

import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import com.nimbusds.openid.connect.sdk.OIDCScopeValue;
import java.util.List;
import java.util.Map;

public class AuthenticationRequestDetector {
    public static boolean isLikelyOpenID(Map<String, List<String>> queryParams) {
        Scope scope = Scope.parse(MultivaluedMapUtils.getFirstValue(queryParams, "scope"));
        if (scope == null) {
            return false;
        }
        return scope.contains(OIDCScopeValue.OPENID);
    }

    private AuthenticationRequestDetector() {
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk;

import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.openid.connect.sdk.OIDCResponseTypeValue;

class OIDCResponseTypeValidator {
    public static void validate(ResponseType rt) {
        if (rt.isEmpty()) {
            throw new IllegalArgumentException("The response type must contain at least one value");
        }
        if (rt.contains(ResponseType.Value.TOKEN) && rt.size() == 1) {
            throw new IllegalArgumentException("The OpenID Connect response type cannot have token as the only value");
        }
        for (ResponseType.Value rtValue : rt) {
            if (rtValue.equals(ResponseType.Value.CODE) || rtValue.equals(ResponseType.Value.TOKEN) || rtValue.equals(OIDCResponseTypeValue.ID_TOKEN)) continue;
            throw new IllegalArgumentException("Unsupported OpenID Connect response type value: " + rtValue);
        }
    }

    private OIDCResponseTypeValidator() {
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.token;

import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.AccessTokenType;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public class TypelessAccessToken
extends AccessToken {
    private static final long serialVersionUID = 2520352130331160789L;

    public TypelessAccessToken(String value) {
        super(AccessTokenType.UNKNOWN, value);
    }

    @Override
    public JSONObject toJSONObject() {
        throw new UnsupportedOperationException("Serialization not supported");
    }

    @Override
    public String toAuthorizationHeader() {
        throw new UnsupportedOperationException("Serialization not supported");
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof AccessToken && this.toString().equals(object.toString());
    }
}


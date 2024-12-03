/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.token;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.token.Token;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import java.util.HashSet;
import java.util.Set;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public final class RefreshToken
extends Token {
    private static final long serialVersionUID = 1482806259791531877L;

    public RefreshToken() {
        this(32);
    }

    public RefreshToken(int byteLength) {
        super(byteLength);
    }

    public RefreshToken(String value) {
        super(value);
    }

    @Override
    public Set<String> getParameterNames() {
        HashSet<String> paramNames = new HashSet<String>();
        paramNames.add("refresh_token");
        return paramNames;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject o = new JSONObject();
        o.put("refresh_token", this.getValue());
        return o;
    }

    public static RefreshToken parse(JSONObject jsonObject) throws ParseException {
        String value = JSONObjectUtils.getString(jsonObject, "refresh_token", null);
        if (value == null) {
            return null;
        }
        return new RefreshToken(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof RefreshToken && this.toString().equals(object.toString());
    }
}


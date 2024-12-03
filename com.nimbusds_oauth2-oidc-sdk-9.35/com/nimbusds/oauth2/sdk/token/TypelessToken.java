/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.oauth2.sdk.token;

import com.nimbusds.oauth2.sdk.token.Token;
import java.util.Collections;
import java.util.Set;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public class TypelessToken
extends Token {
    private static final long serialVersionUID = 1477117093355749547L;

    public TypelessToken(String value) {
        super(value);
    }

    @Override
    public Set<String> getParameterNames() {
        return Collections.emptySet();
    }

    @Override
    public JSONObject toJSONObject() {
        return new JSONObject();
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.token;

import com.nimbusds.oauth2.sdk.id.Identifier;
import java.util.Set;
import net.minidev.json.JSONObject;

public abstract class Token
extends Identifier {
    private static final long serialVersionUID = 1797025947209047077L;

    protected Token(String value) {
        super(value);
    }

    protected Token(int byteLength) {
        super(byteLength);
    }

    protected Token() {
    }

    public abstract Set<String> getParameterNames();

    public abstract JSONObject toJSONObject();
}


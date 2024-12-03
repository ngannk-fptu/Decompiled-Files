/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk;

import com.nimbusds.oauth2.sdk.id.Identifier;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import net.jcip.annotations.Immutable;

@Immutable
public final class Nonce
extends Identifier {
    public Nonce(String value) {
        super(value);
    }

    public Nonce(int byteLength) {
        super(byteLength);
    }

    public Nonce() {
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Nonce && this.toString().equals(object.toString());
    }

    public static Nonce parse(String s) {
        if (StringUtils.isBlank(s)) {
            return null;
        }
        return new Nonce(s);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public final class AuthorizationCode
extends Identifier {
    public AuthorizationCode(String value) {
        super(value);
    }

    public AuthorizationCode(int byteLength) {
        super(byteLength);
    }

    public AuthorizationCode() {
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof AuthorizationCode && this.toString().equals(object.toString());
    }
}


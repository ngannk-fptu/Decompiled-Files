/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.token;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public final class AccessTokenType
extends Identifier {
    public static final AccessTokenType BEARER = new AccessTokenType("Bearer");
    public static final AccessTokenType MAC = new AccessTokenType("mac");
    public static final AccessTokenType UNKNOWN = new AccessTokenType("unknown");

    public AccessTokenType(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof AccessTokenType && this.toString().equalsIgnoreCase(object.toString());
    }
}


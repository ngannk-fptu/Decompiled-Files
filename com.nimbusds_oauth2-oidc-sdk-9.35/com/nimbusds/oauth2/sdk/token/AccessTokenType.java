/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.oauth2.sdk.token;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public final class AccessTokenType
extends Identifier {
    private static final long serialVersionUID = 5636341646710940413L;
    public static final AccessTokenType BEARER = new AccessTokenType("Bearer");
    public static final AccessTokenType DPOP = new AccessTokenType("DPoP");
    public static final AccessTokenType MAC = new AccessTokenType("mac");
    public static final AccessTokenType UNKNOWN = new AccessTokenType("unknown");
    public static final AccessTokenType N_A = new AccessTokenType("N_A");

    public AccessTokenType(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof AccessTokenType && this.toString().equalsIgnoreCase(object.toString());
    }
}


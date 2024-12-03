/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.claims;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public final class AuthorizedParty
extends Identifier {
    private static final long serialVersionUID = 3112051874363693975L;

    public AuthorizedParty(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof AuthorizedParty && this.toString().equals(object.toString());
    }
}


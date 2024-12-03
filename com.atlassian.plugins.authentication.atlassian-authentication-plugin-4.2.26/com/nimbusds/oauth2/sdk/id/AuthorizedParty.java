/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.id;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Deprecated
@Immutable
public final class AuthorizedParty
extends Identifier {
    public AuthorizedParty(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof AuthorizedParty && this.toString().equals(object.toString());
    }
}


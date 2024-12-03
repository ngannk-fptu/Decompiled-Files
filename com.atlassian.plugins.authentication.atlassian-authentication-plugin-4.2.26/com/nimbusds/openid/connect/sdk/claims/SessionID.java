/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.claims;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public final class SessionID
extends Identifier {
    public SessionID(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof SessionID && this.toString().equals(object.toString());
    }
}


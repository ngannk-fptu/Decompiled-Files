/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.secevent.sdk.claims;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public final class TXN
extends Identifier {
    public TXN(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof TXN && this.toString().equals(object.toString());
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance.evidences;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public final class SignatureType
extends Identifier {
    private static final long serialVersionUID = 6412322367787224621L;

    public SignatureType(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof SignatureType && this.toString().equals(object.toString());
    }
}


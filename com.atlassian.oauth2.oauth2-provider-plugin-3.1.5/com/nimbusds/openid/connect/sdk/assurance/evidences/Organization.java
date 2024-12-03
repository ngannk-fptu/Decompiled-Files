/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance.evidences;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public final class Organization
extends Identifier {
    private static final long serialVersionUID = 148331421245095519L;

    public Organization(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Organization && this.toString().equals(object.toString());
    }
}


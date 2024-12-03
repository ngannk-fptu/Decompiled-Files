/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance.evidences;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public final class Occupation
extends Identifier {
    private static final long serialVersionUID = -7931641211954103729L;

    public Occupation(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Occupation && this.toString().equals(object.toString());
    }
}


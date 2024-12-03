/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.openid.connect.sdk.assurance.evidences;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public final class Jurisdiction
extends Identifier {
    private static final long serialVersionUID = 7101336010692838093L;

    public Jurisdiction(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Jurisdiction && this.toString().equals(object.toString());
    }
}


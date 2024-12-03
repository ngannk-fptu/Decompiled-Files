/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.claims;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public final class ACR
extends Identifier {
    private static final long serialVersionUID = 7234490015365923377L;
    public static final ACR PHR = new ACR("phr");
    public static final ACR PHRH = new ACR("phrh");

    public ACR(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof ACR && this.toString().equals(object.toString());
    }
}


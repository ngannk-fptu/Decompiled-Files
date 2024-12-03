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
public final class VouchType
extends Identifier {
    private static final long serialVersionUID = -701546295133681157L;
    public static final VouchType WRITTEN_ATTESTATION = new VouchType("written_attestation");
    public static final VouchType DIGITAL_ATTESTATION = new VouchType("digital_attestation");

    public VouchType(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof VouchType && this.toString().equals(object.toString());
    }
}


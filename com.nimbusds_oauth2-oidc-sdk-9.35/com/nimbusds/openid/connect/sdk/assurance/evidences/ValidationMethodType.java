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
public final class ValidationMethodType
extends Identifier {
    private static final long serialVersionUID = -6994497310463726386L;
    public static final ValidationMethodType VPIP = new ValidationMethodType("vpip");
    public static final ValidationMethodType VPIRUV = new ValidationMethodType("vpiruv");
    public static final ValidationMethodType VRI = new ValidationMethodType("vri");
    public static final ValidationMethodType VDIG = new ValidationMethodType("vdig");
    public static final ValidationMethodType VCRYPT = new ValidationMethodType("vcrypt");
    public static final ValidationMethodType DATA = new ValidationMethodType("data");

    public ValidationMethodType(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof ValidationMethodType && this.toString().equals(object.toString());
    }
}


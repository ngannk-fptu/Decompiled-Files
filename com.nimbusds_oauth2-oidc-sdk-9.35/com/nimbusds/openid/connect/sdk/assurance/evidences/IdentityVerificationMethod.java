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
public final class IdentityVerificationMethod
extends Identifier {
    private static final long serialVersionUID = -1448312497675040627L;
    public static final IdentityVerificationMethod PIPP = new IdentityVerificationMethod("pipp");
    public static final IdentityVerificationMethod SRIPP = new IdentityVerificationMethod("sripp");
    public static final IdentityVerificationMethod EID = new IdentityVerificationMethod("eid");
    public static final IdentityVerificationMethod URIPP = new IdentityVerificationMethod("uripp");
    public static final IdentityVerificationMethod ONSITE = new IdentityVerificationMethod("onsite");

    public IdentityVerificationMethod(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof IdentityVerificationMethod && this.toString().equals(object.toString());
    }
}


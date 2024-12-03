/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance.evidences;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public final class VerificationMethodType
extends Identifier {
    private static final long serialVersionUID = 93318875234167356L;
    public static final VerificationMethodType AUTH = new VerificationMethodType("auth");
    public static final VerificationMethodType TOKEN = new VerificationMethodType("token");
    public static final VerificationMethodType KBV = new VerificationMethodType("kbv");
    public static final VerificationMethodType PVP = new VerificationMethodType("pvp");
    public static final VerificationMethodType PVR = new VerificationMethodType("pvr");
    public static final VerificationMethodType BVP = new VerificationMethodType("bvp");
    public static final VerificationMethodType BVR = new VerificationMethodType("bvr");

    public VerificationMethodType(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof VerificationMethodType && this.toString().equals(object.toString());
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.claims;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public final class AMR
extends Identifier {
    private static final long serialVersionUID = -6833651441441953910L;
    public static final AMR FACE = new AMR("face");
    public static final AMR FPT = new AMR("fpt");
    public static final AMR GEO = new AMR("geo");
    public static final AMR HWK = new AMR("hwk");
    public static final AMR IRIS = new AMR("iris");
    @Deprecated
    public static final AMR EYE = new AMR("eye");
    public static final AMR KBA = new AMR("kba");
    public static final AMR MCA = new AMR("mca");
    public static final AMR MFA = new AMR("mfa");
    public static final AMR OTP = new AMR("otp");
    public static final AMR PIN = new AMR("pin");
    @Deprecated
    public static final AMR POP = new AMR("pop");
    public static final AMR PWD = new AMR("pwd");
    public static final AMR RBA = new AMR("rba");
    public static final AMR SC = new AMR("sc");
    public static final AMR SMS = new AMR("sms");
    public static final AMR SWK = new AMR("swk");
    public static final AMR TEL = new AMR("tel");
    public static final AMR USER = new AMR("user");
    public static final AMR VBM = new AMR("vbm");
    public static final AMR WIA = new AMR("wia");

    public AMR(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof AMR && this.toString().equals(object.toString());
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public final class IdentityTrustFramework
extends Identifier {
    public static final IdentityTrustFramework DE_AML = new IdentityTrustFramework("de_aml");
    public static final IdentityTrustFramework EIDAS_IAL_SUBSTANTIAL = new IdentityTrustFramework("eidas_ial_substantial");
    public static final IdentityTrustFramework EIDAS_IAL_HIGH = new IdentityTrustFramework("eidas_ial_high");
    public static final IdentityTrustFramework NIST_800_63A_IAL_2 = new IdentityTrustFramework("nist_800_63A_ial_2");
    public static final IdentityTrustFramework NIST_800_63A_IAL_3 = new IdentityTrustFramework("nist_800_63A_ial_3");
    public static final IdentityTrustFramework JP_AML = new IdentityTrustFramework("jp_aml");
    public static final IdentityTrustFramework JP_MPIUPA = new IdentityTrustFramework("jp_mpiupa");

    public IdentityTrustFramework(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof IdentityTrustFramework && this.toString().equals(object.toString());
    }
}


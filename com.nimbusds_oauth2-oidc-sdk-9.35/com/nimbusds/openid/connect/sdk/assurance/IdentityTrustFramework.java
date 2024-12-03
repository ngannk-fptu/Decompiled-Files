/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.openid.connect.sdk.assurance;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public final class IdentityTrustFramework
extends Identifier {
    private static final long serialVersionUID = 378614456182831323L;
    public static final IdentityTrustFramework DE_AML = new IdentityTrustFramework("de_aml");
    public static final IdentityTrustFramework EIDAS = new IdentityTrustFramework("eidas");
    @Deprecated
    public static final IdentityTrustFramework EIDAS_IAL_SUBSTANTIAL = new IdentityTrustFramework("eidas_ial_substantial");
    @Deprecated
    public static final IdentityTrustFramework EIDAS_IAL_HIGH = new IdentityTrustFramework("eidas_ial_high");
    public static final IdentityTrustFramework NIST_800_63A = new IdentityTrustFramework("nist_800_63A");
    @Deprecated
    public static final IdentityTrustFramework NIST_800_63A_IAL_2 = new IdentityTrustFramework("nist_800_63A_ial_2");
    @Deprecated
    public static final IdentityTrustFramework NIST_800_63A_IAL_3 = new IdentityTrustFramework("nist_800_63A_ial_3");
    public static final IdentityTrustFramework JP_AML = new IdentityTrustFramework("jp_aml");
    public static final IdentityTrustFramework JP_MPIUPA = new IdentityTrustFramework("jp_mpiupa");
    public static final IdentityTrustFramework CZ_AML = new IdentityTrustFramework("cz_aml");
    public static final IdentityTrustFramework DE_TKG111 = new IdentityTrustFramework("de_tkg111");
    public static final IdentityTrustFramework BE_ITSME = new IdentityTrustFramework("be_itsme");
    public static final IdentityTrustFramework SE_BANKID = new IdentityTrustFramework("se_bankid");
    public static final IdentityTrustFramework IT_SPID = new IdentityTrustFramework("it_spid");
    public static final IdentityTrustFramework NL_EHERKENNING = new IdentityTrustFramework("nl_eHerkenning");
    public static final IdentityTrustFramework UK_TFIDA = new IdentityTrustFramework("uk_tfida");
    public static final IdentityTrustFramework AU_TDIF = new IdentityTrustFramework("au_tdif");

    public IdentityTrustFramework(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof IdentityTrustFramework && this.toString().equals(object.toString());
    }
}


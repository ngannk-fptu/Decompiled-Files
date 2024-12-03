/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public final class IdentityAssuranceLevel
extends Identifier {
    private static final long serialVersionUID = 378614456182831323L;
    public static final IdentityAssuranceLevel VERY_LOW = new IdentityAssuranceLevel("very_low");
    public static final IdentityAssuranceLevel LOW = new IdentityAssuranceLevel("low");
    public static final IdentityAssuranceLevel MEDIUM = new IdentityAssuranceLevel("medium");
    public static final IdentityAssuranceLevel SUBSTANTIAL = new IdentityAssuranceLevel("substantial");
    public static final IdentityAssuranceLevel HIGH = new IdentityAssuranceLevel("high");
    public static final IdentityAssuranceLevel VERY_HIGH = new IdentityAssuranceLevel("very_high");
    public static final IdentityAssuranceLevel IAL1 = new IdentityAssuranceLevel("ial1");
    public static final IdentityAssuranceLevel IAL2 = new IdentityAssuranceLevel("ial2");
    public static final IdentityAssuranceLevel IAL3 = new IdentityAssuranceLevel("ial3");
    public static final IdentityAssuranceLevel AL2 = new IdentityAssuranceLevel("al2");
    public static final IdentityAssuranceLevel AL3 = new IdentityAssuranceLevel("al3");

    public IdentityAssuranceLevel(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof IdentityAssuranceLevel && this.toString().equals(object.toString());
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance.evidences;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public final class IdentityEvidenceType
extends Identifier {
    public static final IdentityEvidenceType ID_DOCUMENT = new IdentityEvidenceType("id_document");
    public static final IdentityEvidenceType UTILITY_BILL = new IdentityEvidenceType("utility_bill");
    public static final IdentityEvidenceType QES = new IdentityEvidenceType("qes");

    public IdentityEvidenceType(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof IdentityEvidenceType && this.toString().equals(object.toString());
    }
}


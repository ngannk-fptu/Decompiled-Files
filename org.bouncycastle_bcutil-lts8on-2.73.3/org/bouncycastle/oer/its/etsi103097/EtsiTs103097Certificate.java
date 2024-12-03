/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Sequence
 */
package org.bouncycastle.oer.its.etsi103097;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ieee1609dot2.ExplicitCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.IssuerIdentifier;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Signature;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

public class EtsiTs103097Certificate
extends ExplicitCertificate {
    public EtsiTs103097Certificate(UINT8 version, IssuerIdentifier issuer, ToBeSignedCertificate toBeSignedCertificate, Signature signature) {
        super(version, issuer, toBeSignedCertificate, signature);
    }

    protected EtsiTs103097Certificate(ASN1Sequence instance) {
        super(instance);
    }

    public static EtsiTs103097Certificate getInstance(Object src) {
        if (src instanceof EtsiTs103097Certificate) {
            return (EtsiTs103097Certificate)((Object)src);
        }
        if (src != null) {
            return new EtsiTs103097Certificate(ASN1Sequence.getInstance((Object)src));
        }
        return null;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateBase;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateType;
import org.bouncycastle.oer.its.ieee1609dot2.IssuerIdentifier;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Signature;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

public class ExplicitCertificate
extends CertificateBase {
    public ExplicitCertificate(CertificateBase base) {
        this(base.getVersion(), base.getIssuer(), base.getToBeSigned(), base.getSignature());
    }

    public ExplicitCertificate(UINT8 version, IssuerIdentifier issuer, ToBeSignedCertificate toBeSigned, Signature signature) {
        super(version, CertificateType.explicit, issuer, toBeSigned, signature);
    }

    protected ExplicitCertificate(ASN1Sequence seq) {
        super(seq);
        if (!this.getType().equals((ASN1Primitive)CertificateType.explicit)) {
            throw new IllegalArgumentException("object was certificate base but the type was not explicit");
        }
    }

    public static ExplicitCertificate getInstance(Object o) {
        if (o instanceof ExplicitCertificate) {
            return (ExplicitCertificate)((Object)o);
        }
        if (o != null) {
            return new ExplicitCertificate(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }
}


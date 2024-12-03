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

public class ImplicitCertificate
extends CertificateBase {
    public ImplicitCertificate(CertificateBase base) {
        this(base.getVersion(), base.getIssuer(), base.getToBeSigned(), base.getSignature());
    }

    public ImplicitCertificate(UINT8 version, IssuerIdentifier issuer, ToBeSignedCertificate toBeSignedCertificate, Signature signature) {
        super(version, CertificateType.implicit, issuer, toBeSignedCertificate, signature);
    }

    private ImplicitCertificate(ASN1Sequence sequence) {
        super(sequence);
        if (!this.getType().equals((ASN1Primitive)CertificateType.implicit)) {
            throw new IllegalArgumentException("object was certificate base but the type was not implicit");
        }
    }

    public static ImplicitCertificate getInstance(Object o) {
        if (o instanceof ImplicitCertificate) {
            return (ImplicitCertificate)((Object)o);
        }
        if (o != null) {
            return new ImplicitCertificate(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }
}


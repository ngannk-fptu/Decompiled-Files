/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Sequence
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateBase;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateType;
import org.bouncycastle.oer.its.ieee1609dot2.IssuerIdentifier;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Signature;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

public class Certificate
extends CertificateBase {
    public Certificate(UINT8 version, CertificateType type, IssuerIdentifier issuer, ToBeSignedCertificate toBeSignedCertificate, Signature signature) {
        super(version, type, issuer, toBeSignedCertificate, signature);
    }

    public Certificate(CertificateBase base) {
        this(base.getVersion(), base.getType(), base.getIssuer(), base.getToBeSigned(), base.getSignature());
    }

    protected Certificate(ASN1Sequence seq) {
        super(seq);
    }

    public static Certificate getInstance(Object value) {
        if (value instanceof Certificate) {
            return (Certificate)((Object)value);
        }
        if (value != null) {
            return new Certificate(ASN1Sequence.getInstance((Object)value));
        }
        return null;
    }
}


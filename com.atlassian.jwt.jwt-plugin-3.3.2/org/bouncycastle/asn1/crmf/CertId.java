/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.crmf;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.GeneralName;

public class CertId
extends ASN1Object {
    private GeneralName issuer;
    private ASN1Integer serialNumber;

    private CertId(ASN1Sequence aSN1Sequence) {
        this.issuer = GeneralName.getInstance(aSN1Sequence.getObjectAt(0));
        this.serialNumber = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(1));
    }

    public static CertId getInstance(Object object) {
        if (object instanceof CertId) {
            return (CertId)object;
        }
        if (object != null) {
            return new CertId(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public static CertId getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return CertId.getInstance(ASN1Sequence.getInstance(aSN1TaggedObject, bl));
    }

    public CertId(GeneralName generalName, BigInteger bigInteger) {
        this(generalName, new ASN1Integer(bigInteger));
    }

    public CertId(GeneralName generalName, ASN1Integer aSN1Integer) {
        this.issuer = generalName;
        this.serialNumber = aSN1Integer;
    }

    public GeneralName getIssuer() {
        return this.issuer;
    }

    public ASN1Integer getSerialNumber() {
        return this.serialNumber;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(2);
        aSN1EncodableVector.add(this.issuer);
        aSN1EncodableVector.add(this.serialNumber);
        return new DERSequence(aSN1EncodableVector);
    }
}


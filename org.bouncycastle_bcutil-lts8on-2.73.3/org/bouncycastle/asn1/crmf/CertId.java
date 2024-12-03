/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.x509.GeneralName
 */
package org.bouncycastle.asn1.crmf;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
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

    private CertId(ASN1Sequence seq) {
        this.issuer = GeneralName.getInstance((Object)seq.getObjectAt(0));
        this.serialNumber = ASN1Integer.getInstance((Object)seq.getObjectAt(1));
    }

    public static CertId getInstance(Object o) {
        if (o instanceof CertId) {
            return (CertId)((Object)o);
        }
        if (o != null) {
            return new CertId(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public static CertId getInstance(ASN1TaggedObject obj, boolean isExplicit) {
        return CertId.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)obj, (boolean)isExplicit));
    }

    public CertId(GeneralName issuer, BigInteger serialNumber) {
        this(issuer, new ASN1Integer(serialNumber));
    }

    public CertId(GeneralName issuer, ASN1Integer serialNumber) {
        this.issuer = issuer;
        this.serialNumber = serialNumber;
    }

    public GeneralName getIssuer() {
        return this.issuer;
    }

    public ASN1Integer getSerialNumber() {
        return this.serialNumber;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)this.issuer);
        v.add((ASN1Encodable)this.serialNumber);
        return new DERSequence(v);
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1BitString
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1GeneralizedTime
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.asn1.x509.GeneralName
 *  org.bouncycastle.asn1.x509.ReasonFlags
 */
package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.ReasonFlags;

public class GetCRL
extends ASN1Object {
    private final X500Name issuerName;
    private GeneralName cRLName;
    private ASN1GeneralizedTime time;
    private ReasonFlags reasons;

    public GetCRL(X500Name issuerName, GeneralName cRLName, ASN1GeneralizedTime time, ReasonFlags reasons) {
        this.issuerName = issuerName;
        this.cRLName = cRLName;
        this.time = time;
        this.reasons = reasons;
    }

    private GetCRL(ASN1Sequence seq) {
        if (seq.size() < 1 || seq.size() > 4) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.issuerName = X500Name.getInstance((Object)seq.getObjectAt(0));
        int index = 1;
        if (seq.size() > index && seq.getObjectAt(index).toASN1Primitive() instanceof ASN1TaggedObject) {
            this.cRLName = GeneralName.getInstance((Object)seq.getObjectAt(index++));
        }
        if (seq.size() > index && seq.getObjectAt(index).toASN1Primitive() instanceof ASN1GeneralizedTime) {
            this.time = ASN1GeneralizedTime.getInstance((Object)seq.getObjectAt(index++));
        }
        if (seq.size() > index && seq.getObjectAt(index).toASN1Primitive() instanceof ASN1BitString) {
            this.reasons = new ReasonFlags(ASN1BitString.getInstance((Object)seq.getObjectAt(index)));
        }
    }

    public static GetCRL getInstance(Object o) {
        if (o instanceof GetCRL) {
            return (GetCRL)((Object)o);
        }
        if (o != null) {
            return new GetCRL(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public X500Name getIssuerName() {
        return this.issuerName;
    }

    public GeneralName getcRLName() {
        return this.cRLName;
    }

    public ASN1GeneralizedTime getTime() {
        return this.time;
    }

    public ReasonFlags getReasons() {
        return this.reasons;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(4);
        v.add((ASN1Encodable)this.issuerName);
        if (this.cRLName != null) {
            v.add((ASN1Encodable)this.cRLName);
        }
        if (this.time != null) {
            v.add((ASN1Encodable)this.time);
        }
        if (this.reasons != null) {
            v.add((ASN1Encodable)this.reasons);
        }
        return new DERSequence(v);
    }
}


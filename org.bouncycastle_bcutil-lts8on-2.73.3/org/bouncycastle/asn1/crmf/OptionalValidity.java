/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.x509.Time
 */
package org.bouncycastle.asn1.crmf;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.Time;

public class OptionalValidity
extends ASN1Object {
    private Time notBefore;
    private Time notAfter;

    private OptionalValidity(ASN1Sequence seq) {
        Enumeration en = seq.getObjects();
        while (en.hasMoreElements()) {
            ASN1TaggedObject tObj = (ASN1TaggedObject)en.nextElement();
            if (tObj.getTagNo() == 0) {
                this.notBefore = Time.getInstance((ASN1TaggedObject)tObj, (boolean)true);
                continue;
            }
            this.notAfter = Time.getInstance((ASN1TaggedObject)tObj, (boolean)true);
        }
    }

    public static OptionalValidity getInstance(Object o) {
        if (o instanceof OptionalValidity) {
            return (OptionalValidity)((Object)o);
        }
        if (o != null) {
            return new OptionalValidity(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public OptionalValidity(Time notBefore, Time notAfter) {
        if (notBefore == null && notAfter == null) {
            throw new IllegalArgumentException("at least one of notBefore/notAfter must not be null.");
        }
        this.notBefore = notBefore;
        this.notAfter = notAfter;
    }

    public Time getNotBefore() {
        return this.notBefore;
    }

    public Time getNotAfter() {
        return this.notAfter;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        if (this.notBefore != null) {
            v.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)this.notBefore));
        }
        if (this.notAfter != null) {
            v.add((ASN1Encodable)new DERTaggedObject(true, 1, (ASN1Encodable)this.notAfter));
        }
        return new DERSequence(v);
    }
}


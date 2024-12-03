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
 *  org.bouncycastle.asn1.x509.Attribute
 *  org.bouncycastle.asn1.x509.AttributeCertificate
 */
package org.bouncycastle.asn1.esf;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.Attribute;
import org.bouncycastle.asn1.x509.AttributeCertificate;

public class SignerAttribute
extends ASN1Object {
    private Object[] values;

    public static SignerAttribute getInstance(Object o) {
        if (o instanceof SignerAttribute) {
            return (SignerAttribute)((Object)o);
        }
        if (o != null) {
            return new SignerAttribute(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    private SignerAttribute(ASN1Sequence seq) {
        int index = 0;
        this.values = new Object[seq.size()];
        Enumeration e = seq.getObjects();
        while (e.hasMoreElements()) {
            ASN1TaggedObject taggedObject = ASN1TaggedObject.getInstance(e.nextElement());
            if (taggedObject.getTagNo() == 0) {
                ASN1Sequence attrs = ASN1Sequence.getInstance((ASN1TaggedObject)taggedObject, (boolean)true);
                Attribute[] attributes = new Attribute[attrs.size()];
                for (int i = 0; i != attributes.length; ++i) {
                    attributes[i] = Attribute.getInstance((Object)attrs.getObjectAt(i));
                }
                this.values[index] = attributes;
            } else if (taggedObject.getTagNo() == 1) {
                this.values[index] = AttributeCertificate.getInstance((Object)ASN1Sequence.getInstance((ASN1TaggedObject)taggedObject, (boolean)true));
            } else {
                throw new IllegalArgumentException("illegal tag: " + taggedObject.getTagNo());
            }
            ++index;
        }
    }

    public SignerAttribute(Attribute[] claimedAttributes) {
        this.values = new Object[1];
        this.values[0] = claimedAttributes;
    }

    public SignerAttribute(AttributeCertificate certifiedAttributes) {
        this.values = new Object[1];
        this.values[0] = certifiedAttributes;
    }

    public Object[] getValues() {
        Object[] rv = new Object[this.values.length];
        System.arraycopy(this.values, 0, rv, 0, rv.length);
        return rv;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(this.values.length);
        for (int i = 0; i != this.values.length; ++i) {
            if (this.values[i] instanceof Attribute[]) {
                v.add((ASN1Encodable)new DERTaggedObject(0, (ASN1Encodable)new DERSequence((ASN1Encodable[])((Attribute[])this.values[i]))));
                continue;
            }
            v.add((ASN1Encodable)new DERTaggedObject(1, (ASN1Encodable)((AttributeCertificate)this.values[i])));
        }
        return new DERSequence(v);
    }
}


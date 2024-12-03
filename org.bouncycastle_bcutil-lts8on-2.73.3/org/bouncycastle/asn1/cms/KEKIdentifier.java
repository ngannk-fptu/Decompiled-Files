/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1GeneralizedTime
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cms.OtherKeyAttribute;

public class KEKIdentifier
extends ASN1Object {
    private ASN1OctetString keyIdentifier;
    private ASN1GeneralizedTime date;
    private OtherKeyAttribute other;

    public KEKIdentifier(byte[] keyIdentifier, ASN1GeneralizedTime date, OtherKeyAttribute other) {
        this.keyIdentifier = new DEROctetString(keyIdentifier);
        this.date = date;
        this.other = other;
    }

    private KEKIdentifier(ASN1Sequence seq) {
        this.keyIdentifier = (ASN1OctetString)seq.getObjectAt(0);
        switch (seq.size()) {
            case 1: {
                break;
            }
            case 2: {
                if (seq.getObjectAt(1) instanceof ASN1GeneralizedTime) {
                    this.date = (ASN1GeneralizedTime)seq.getObjectAt(1);
                    break;
                }
                this.other = OtherKeyAttribute.getInstance(seq.getObjectAt(1));
                break;
            }
            case 3: {
                this.date = (ASN1GeneralizedTime)seq.getObjectAt(1);
                this.other = OtherKeyAttribute.getInstance(seq.getObjectAt(2));
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid KEKIdentifier");
            }
        }
    }

    public static KEKIdentifier getInstance(ASN1TaggedObject obj, boolean explicit) {
        return KEKIdentifier.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)obj, (boolean)explicit));
    }

    public static KEKIdentifier getInstance(Object obj) {
        if (obj == null || obj instanceof KEKIdentifier) {
            return (KEKIdentifier)((Object)obj);
        }
        if (obj instanceof ASN1Sequence) {
            return new KEKIdentifier((ASN1Sequence)obj);
        }
        throw new IllegalArgumentException("Invalid KEKIdentifier: " + obj.getClass().getName());
    }

    public ASN1OctetString getKeyIdentifier() {
        return this.keyIdentifier;
    }

    public ASN1GeneralizedTime getDate() {
        return this.date;
    }

    public OtherKeyAttribute getOther() {
        return this.other;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add((ASN1Encodable)this.keyIdentifier);
        if (this.date != null) {
            v.add((ASN1Encodable)this.date);
        }
        if (this.other != null) {
            v.add((ASN1Encodable)this.other);
        }
        return new DERSequence(v);
    }
}


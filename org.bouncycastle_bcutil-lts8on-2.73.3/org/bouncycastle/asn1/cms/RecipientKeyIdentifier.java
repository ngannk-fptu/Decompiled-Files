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

public class RecipientKeyIdentifier
extends ASN1Object {
    private ASN1OctetString subjectKeyIdentifier;
    private ASN1GeneralizedTime date;
    private OtherKeyAttribute other;

    public RecipientKeyIdentifier(ASN1OctetString subjectKeyIdentifier, ASN1GeneralizedTime date, OtherKeyAttribute other) {
        this.subjectKeyIdentifier = subjectKeyIdentifier;
        this.date = date;
        this.other = other;
    }

    public RecipientKeyIdentifier(byte[] subjectKeyIdentifier, ASN1GeneralizedTime date, OtherKeyAttribute other) {
        this.subjectKeyIdentifier = new DEROctetString(subjectKeyIdentifier);
        this.date = date;
        this.other = other;
    }

    public RecipientKeyIdentifier(byte[] subjectKeyIdentifier) {
        this(subjectKeyIdentifier, null, null);
    }

    private RecipientKeyIdentifier(ASN1Sequence seq) {
        this.subjectKeyIdentifier = ASN1OctetString.getInstance((Object)seq.getObjectAt(0));
        switch (seq.size()) {
            case 1: {
                break;
            }
            case 2: {
                if (seq.getObjectAt(1) instanceof ASN1GeneralizedTime) {
                    this.date = ASN1GeneralizedTime.getInstance((Object)seq.getObjectAt(1));
                    break;
                }
                this.other = OtherKeyAttribute.getInstance(seq.getObjectAt(2));
                break;
            }
            case 3: {
                this.date = ASN1GeneralizedTime.getInstance((Object)seq.getObjectAt(1));
                this.other = OtherKeyAttribute.getInstance(seq.getObjectAt(2));
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid RecipientKeyIdentifier");
            }
        }
    }

    public static RecipientKeyIdentifier getInstance(ASN1TaggedObject ato, boolean isExplicit) {
        return RecipientKeyIdentifier.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)ato, (boolean)isExplicit));
    }

    public static RecipientKeyIdentifier getInstance(Object obj) {
        if (obj instanceof RecipientKeyIdentifier) {
            return (RecipientKeyIdentifier)((Object)obj);
        }
        if (obj != null) {
            return new RecipientKeyIdentifier(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    public ASN1OctetString getSubjectKeyIdentifier() {
        return this.subjectKeyIdentifier;
    }

    public ASN1GeneralizedTime getDate() {
        return this.date;
    }

    public OtherKeyAttribute getOtherKeyAttribute() {
        return this.other;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add((ASN1Encodable)this.subjectKeyIdentifier);
        if (this.date != null) {
            v.add((ASN1Encodable)this.date);
        }
        if (this.other != null) {
            v.add((ASN1Encodable)this.other);
        }
        return new DERSequence(v);
    }
}


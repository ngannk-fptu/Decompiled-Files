/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.tsp;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class Accuracy
extends ASN1Object {
    ASN1Integer seconds;
    ASN1Integer millis;
    ASN1Integer micros;
    protected static final int MIN_MILLIS = 1;
    protected static final int MAX_MILLIS = 999;
    protected static final int MIN_MICROS = 1;
    protected static final int MAX_MICROS = 999;

    protected Accuracy() {
    }

    public Accuracy(ASN1Integer aSN1Integer, ASN1Integer aSN1Integer2, ASN1Integer aSN1Integer3) {
        int n;
        if (null != aSN1Integer2 && ((n = aSN1Integer2.intValueExact()) < 1 || n > 999)) {
            throw new IllegalArgumentException("Invalid millis field : not in (1..999)");
        }
        if (null != aSN1Integer3 && ((n = aSN1Integer3.intValueExact()) < 1 || n > 999)) {
            throw new IllegalArgumentException("Invalid micros field : not in (1..999)");
        }
        this.seconds = aSN1Integer;
        this.millis = aSN1Integer2;
        this.micros = aSN1Integer3;
    }

    private Accuracy(ASN1Sequence aSN1Sequence) {
        this.seconds = null;
        this.millis = null;
        this.micros = null;
        block4: for (int i = 0; i < aSN1Sequence.size(); ++i) {
            if (aSN1Sequence.getObjectAt(i) instanceof ASN1Integer) {
                this.seconds = (ASN1Integer)aSN1Sequence.getObjectAt(i);
                continue;
            }
            if (!(aSN1Sequence.getObjectAt(i) instanceof ASN1TaggedObject)) continue;
            ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject)aSN1Sequence.getObjectAt(i);
            switch (aSN1TaggedObject.getTagNo()) {
                case 0: {
                    this.millis = ASN1Integer.getInstance(aSN1TaggedObject, false);
                    int n = this.millis.intValueExact();
                    if (n >= 1 && n <= 999) continue block4;
                    throw new IllegalArgumentException("Invalid millis field : not in (1..999)");
                }
                case 1: {
                    this.micros = ASN1Integer.getInstance(aSN1TaggedObject, false);
                    int n = this.micros.intValueExact();
                    if (n >= 1 && n <= 999) continue block4;
                    throw new IllegalArgumentException("Invalid micros field : not in (1..999)");
                }
                default: {
                    throw new IllegalArgumentException("Invalid tag number");
                }
            }
        }
    }

    public static Accuracy getInstance(Object object) {
        if (object instanceof Accuracy) {
            return (Accuracy)object;
        }
        if (object != null) {
            return new Accuracy(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public ASN1Integer getSeconds() {
        return this.seconds;
    }

    public ASN1Integer getMillis() {
        return this.millis;
    }

    public ASN1Integer getMicros() {
        return this.micros;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(3);
        if (this.seconds != null) {
            aSN1EncodableVector.add(this.seconds);
        }
        if (this.millis != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 0, this.millis));
        }
        if (this.micros != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 1, this.micros));
        }
        return new DERSequence(aSN1EncodableVector);
    }
}


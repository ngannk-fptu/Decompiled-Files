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
 *  org.bouncycastle.asn1.DERTaggedObject
 */
package org.bouncycastle.asn1.tsp;

import org.bouncycastle.asn1.ASN1Encodable;
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

    public Accuracy(ASN1Integer seconds, ASN1Integer millis, ASN1Integer micros) {
        int microsValue;
        int millisValue;
        if (null != millis && ((millisValue = millis.intValueExact()) < 1 || millisValue > 999)) {
            throw new IllegalArgumentException("Invalid millis field : not in (1..999)");
        }
        if (null != micros && ((microsValue = micros.intValueExact()) < 1 || microsValue > 999)) {
            throw new IllegalArgumentException("Invalid micros field : not in (1..999)");
        }
        this.seconds = seconds;
        this.millis = millis;
        this.micros = micros;
    }

    private Accuracy(ASN1Sequence seq) {
        this.seconds = null;
        this.millis = null;
        this.micros = null;
        block4: for (int i = 0; i < seq.size(); ++i) {
            if (seq.getObjectAt(i) instanceof ASN1Integer) {
                this.seconds = (ASN1Integer)seq.getObjectAt(i);
                continue;
            }
            if (!(seq.getObjectAt(i) instanceof ASN1TaggedObject)) continue;
            ASN1TaggedObject extra = (ASN1TaggedObject)seq.getObjectAt(i);
            switch (extra.getTagNo()) {
                case 0: {
                    this.millis = ASN1Integer.getInstance((ASN1TaggedObject)extra, (boolean)false);
                    int millisValue = this.millis.intValueExact();
                    if (millisValue >= 1 && millisValue <= 999) continue block4;
                    throw new IllegalArgumentException("Invalid millis field : not in (1..999)");
                }
                case 1: {
                    this.micros = ASN1Integer.getInstance((ASN1TaggedObject)extra, (boolean)false);
                    int microsValue = this.micros.intValueExact();
                    if (microsValue >= 1 && microsValue <= 999) continue block4;
                    throw new IllegalArgumentException("Invalid micros field : not in (1..999)");
                }
                default: {
                    throw new IllegalArgumentException("Invalid tag number");
                }
            }
        }
    }

    public static Accuracy getInstance(Object o) {
        if (o instanceof Accuracy) {
            return (Accuracy)((Object)o);
        }
        if (o != null) {
            return new Accuracy(ASN1Sequence.getInstance((Object)o));
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
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        if (this.seconds != null) {
            v.add((ASN1Encodable)this.seconds);
        }
        if (this.millis != null) {
            v.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.millis));
        }
        if (this.micros != null) {
            v.add((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)this.micros));
        }
        return new DERSequence(v);
    }
}


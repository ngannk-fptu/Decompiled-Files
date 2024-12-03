/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Choice
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERTaggedObject
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EcdsaP256Signature;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EcdsaP384Signature;

public class Signature
extends ASN1Object
implements ASN1Choice {
    public static final int ecdsaNistP256Signature = 0;
    public static final int ecdsaBrainpoolP256r1Signature = 1;
    public static final int ecdsaBrainpoolP384r1Signature = 2;
    private final int choice;
    private final ASN1Encodable signature;

    public Signature(int choice, ASN1Encodable value) {
        this.choice = choice;
        this.signature = value;
    }

    public static Signature ecdsaNistP256Signature(EcdsaP256Signature v) {
        return new Signature(0, (ASN1Encodable)v);
    }

    public static Signature ecdsaBrainpoolP256r1Signature(EcdsaP256Signature v) {
        return new Signature(1, (ASN1Encodable)v);
    }

    public static Signature ecdsaBrainpoolP384r1Signature(EcdsaP384Signature v) {
        return new Signature(2, (ASN1Encodable)v);
    }

    private Signature(ASN1TaggedObject ato) {
        this.choice = ato.getTagNo();
        switch (this.choice) {
            case 0: 
            case 1: {
                this.signature = EcdsaP256Signature.getInstance(ato.getExplicitBaseObject());
                break;
            }
            case 2: {
                this.signature = EcdsaP384Signature.getInstance(ato.getExplicitBaseObject());
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid choice value " + ato.getTagNo());
            }
        }
    }

    public static Signature getInstance(Object objectAt) {
        if (objectAt instanceof Signature) {
            return (Signature)((Object)objectAt);
        }
        if (objectAt != null) {
            return new Signature(ASN1TaggedObject.getInstance((Object)objectAt, (int)128));
        }
        return null;
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getSignature() {
        return this.signature;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.signature);
    }
}


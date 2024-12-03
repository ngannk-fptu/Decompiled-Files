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
package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP256CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicVerificationKey;

public class VerificationKeyIndicator
extends ASN1Object
implements ASN1Choice {
    public static final int verificationKey = 0;
    public static final int reconstructionValue = 1;
    private final int choice;
    private final ASN1Encodable verificationKeyIndicator;

    public VerificationKeyIndicator(int choice, ASN1Encodable value) {
        this.choice = choice;
        this.verificationKeyIndicator = value;
    }

    private VerificationKeyIndicator(ASN1TaggedObject ato) {
        this.choice = ato.getTagNo();
        switch (this.choice) {
            case 0: {
                this.verificationKeyIndicator = PublicVerificationKey.getInstance(ato.getExplicitBaseObject());
                break;
            }
            case 1: {
                this.verificationKeyIndicator = EccP256CurvePoint.getInstance(ato.getExplicitBaseObject());
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid choice value " + this.choice);
            }
        }
    }

    public static VerificationKeyIndicator verificationKey(PublicVerificationKey value) {
        return new VerificationKeyIndicator(0, (ASN1Encodable)value);
    }

    public static VerificationKeyIndicator reconstructionValue(EccP256CurvePoint value) {
        return new VerificationKeyIndicator(1, (ASN1Encodable)value);
    }

    public static VerificationKeyIndicator getInstance(Object src) {
        if (src instanceof VerificationKeyIndicator) {
            return (VerificationKeyIndicator)((Object)src);
        }
        if (src != null) {
            return new VerificationKeyIndicator(ASN1TaggedObject.getInstance((Object)src, (int)128));
        }
        return null;
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getVerificationKeyIndicator() {
        return this.verificationKeyIndicator;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.verificationKeyIndicator);
    }
}


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
package org.bouncycastle.oer.its.ieee1609dot2dot1;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicEncryptionKey;
import org.bouncycastle.oer.its.ieee1609dot2dot1.ButterflyExpansion;
import org.bouncycastle.oer.its.ieee1609dot2dot1.ButterflyParamsOriginal;

public class AdditionalParams
extends ASN1Object
implements ASN1Choice {
    public static final int original = 0;
    public static final int unified = 1;
    public static final int compactUnified = 2;
    public static final int encryptionKey = 3;
    protected final int choice;
    protected final ASN1Encodable additionalParams;

    private AdditionalParams(int choice, ASN1Encodable additionalParams) {
        switch (choice) {
            case 0: {
                this.additionalParams = ButterflyParamsOriginal.getInstance(additionalParams);
                break;
            }
            case 1: 
            case 2: {
                this.additionalParams = ButterflyExpansion.getInstance(additionalParams);
                break;
            }
            case 3: {
                this.additionalParams = PublicEncryptionKey.getInstance(additionalParams);
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid choice value " + choice);
            }
        }
        this.choice = choice;
    }

    private AdditionalParams(ASN1TaggedObject ato) {
        this(ato.getTagNo(), (ASN1Encodable)ato.getExplicitBaseObject());
    }

    public static AdditionalParams getInstance(Object o) {
        if (o instanceof AdditionalParams) {
            return (AdditionalParams)((Object)o);
        }
        if (o != null) {
            return new AdditionalParams(ASN1TaggedObject.getInstance((Object)o, (int)128));
        }
        return null;
    }

    public static AdditionalParams original(ButterflyParamsOriginal value) {
        return new AdditionalParams(0, (ASN1Encodable)value);
    }

    public static AdditionalParams unified(ButterflyExpansion exp) {
        return new AdditionalParams(1, (ASN1Encodable)exp);
    }

    public static AdditionalParams compactUnified(ButterflyExpansion exp) {
        return new AdditionalParams(2, (ASN1Encodable)exp);
    }

    public static AdditionalParams encryptionKey(PublicEncryptionKey pek) {
        return new AdditionalParams(3, (ASN1Encodable)pek);
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getAdditionalParams() {
        return this.additionalParams;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.additionalParams);
    }
}


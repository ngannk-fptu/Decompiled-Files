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
import org.bouncycastle.oer.its.ieee1609dot2.PKRecipientInfo;
import org.bouncycastle.oer.its.ieee1609dot2.PreSharedKeyRecipientInfo;
import org.bouncycastle.oer.its.ieee1609dot2.SymmRecipientInfo;

public class RecipientInfo
extends ASN1Object
implements ASN1Choice {
    public static final int pskRecipInfo = 0;
    public static final int symmRecipInfo = 1;
    public static final int certRecipInfo = 2;
    public static final int signedDataRecipInfo = 3;
    public static final int rekRecipInfo = 4;
    private final int choice;
    private final ASN1Encodable recipientInfo;

    public RecipientInfo(int choice, ASN1Encodable value) {
        this.choice = choice;
        this.recipientInfo = value;
    }

    private RecipientInfo(ASN1TaggedObject instance) {
        this.choice = instance.getTagNo();
        switch (this.choice) {
            case 0: {
                this.recipientInfo = PreSharedKeyRecipientInfo.getInstance(instance.getExplicitBaseObject());
                break;
            }
            case 1: {
                this.recipientInfo = SymmRecipientInfo.getInstance(instance.getExplicitBaseObject());
                break;
            }
            case 2: 
            case 3: 
            case 4: {
                this.recipientInfo = PKRecipientInfo.getInstance(instance.getExplicitBaseObject());
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid choice value " + this.choice);
            }
        }
    }

    public static RecipientInfo getInstance(Object object) {
        if (object instanceof RecipientInfo) {
            return (RecipientInfo)((Object)object);
        }
        if (object != null) {
            return new RecipientInfo(ASN1TaggedObject.getInstance((Object)object, (int)128));
        }
        return null;
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getRecipientInfo() {
        return this.recipientInfo;
    }

    public static RecipientInfo pskRecipInfo(PreSharedKeyRecipientInfo info) {
        return new RecipientInfo(0, (ASN1Encodable)info);
    }

    public static RecipientInfo symmRecipInfo(SymmRecipientInfo info) {
        return new RecipientInfo(1, (ASN1Encodable)info);
    }

    public static RecipientInfo certRecipInfo(PKRecipientInfo info) {
        return new RecipientInfo(2, (ASN1Encodable)info);
    }

    public static RecipientInfo signedDataRecipInfo(PKRecipientInfo info) {
        return new RecipientInfo(3, (ASN1Encodable)info);
    }

    public static RecipientInfo rekRecipInfo(PKRecipientInfo info) {
        return new RecipientInfo(4, (ASN1Encodable)info);
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.recipientInfo);
    }
}


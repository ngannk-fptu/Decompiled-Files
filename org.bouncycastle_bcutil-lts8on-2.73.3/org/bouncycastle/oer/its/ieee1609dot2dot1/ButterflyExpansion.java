/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Choice
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERTaggedObject
 */
package org.bouncycastle.oer.its.ieee1609dot2dot1;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;

public class ButterflyExpansion
extends ASN1Object
implements ASN1Choice {
    public static final int aes128 = 0;
    protected final int choice;
    protected final ASN1Encodable butterflyExpansion;

    ButterflyExpansion(int choice, ASN1Encodable butterflyExpansion) {
        this.choice = choice;
        this.butterflyExpansion = butterflyExpansion;
    }

    private ButterflyExpansion(ASN1TaggedObject ato) {
        this.choice = ato.getTagNo();
        switch (this.choice) {
            case 0: {
                this.butterflyExpansion = DEROctetString.getInstance((Object)ato.getExplicitBaseObject());
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid choice value " + this.choice);
            }
        }
    }

    public static ButterflyExpansion getInstance(Object o) {
        if (o instanceof ButterflyExpansion) {
            return (ButterflyExpansion)((Object)o);
        }
        if (o != null) {
            return new ButterflyExpansion(ASN1TaggedObject.getInstance((Object)o, (int)128));
        }
        return null;
    }

    public static ButterflyExpansion aes128(byte[] value) {
        if (value.length != 16) {
            throw new IllegalArgumentException("length must be 16");
        }
        return new ButterflyExpansion(0, (ASN1Encodable)new DEROctetString(value));
    }

    public static ButterflyExpansion aes128(ASN1OctetString value) {
        return ButterflyExpansion.aes128(value.getOctets());
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.butterflyExpansion);
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getButterflyExpansion() {
        return this.butterflyExpansion;
    }
}


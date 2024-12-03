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
import org.bouncycastle.oer.its.ieee1609dot2.AesCcmCiphertext;

public class SymmetricCiphertext
extends ASN1Object
implements ASN1Choice {
    public static final int aes128ccm = 0;
    private final int choice;
    private final ASN1Encodable symmetricCiphertext;

    public SymmetricCiphertext(int choice, ASN1Encodable value) {
        this.choice = choice;
        this.symmetricCiphertext = value;
    }

    private SymmetricCiphertext(ASN1TaggedObject ato) {
        this.choice = ato.getTagNo();
        switch (this.choice) {
            case 0: {
                this.symmetricCiphertext = AesCcmCiphertext.getInstance(ato.getExplicitBaseObject());
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid choice value " + this.choice);
            }
        }
    }

    public static SymmetricCiphertext aes128ccm(AesCcmCiphertext ciphertext) {
        return new SymmetricCiphertext(0, (ASN1Encodable)ciphertext);
    }

    public static SymmetricCiphertext getInstance(Object o) {
        if (o instanceof SymmetricCiphertext) {
            return (SymmetricCiphertext)((Object)o);
        }
        if (o != null) {
            return new SymmetricCiphertext(ASN1TaggedObject.getInstance((Object)o, (int)128));
        }
        return null;
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getSymmetricCiphertext() {
        return this.symmetricCiphertext;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.symmetricCiphertext);
    }
}


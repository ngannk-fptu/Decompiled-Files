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
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;

public class SymmetricEncryptionKey
extends ASN1Object
implements ASN1Choice {
    public static final int aes128ccm = 0;
    private final int choice;
    private final ASN1Encodable symmetricEncryptionKey;

    public SymmetricEncryptionKey(int choice, ASN1Encodable value) {
        this.choice = choice;
        this.symmetricEncryptionKey = value;
    }

    private SymmetricEncryptionKey(ASN1TaggedObject instance) {
        ASN1OctetString str;
        this.choice = instance.getTagNo();
        if (this.choice == 0) {
            str = DEROctetString.getInstance((Object)instance.getExplicitBaseObject());
            if (str.getOctets().length != 16) {
                throw new IllegalArgumentException("aes128ccm string not 16 bytes");
            }
        } else {
            throw new IllegalArgumentException("invalid choice value " + this.choice);
        }
        this.symmetricEncryptionKey = str;
    }

    public static SymmetricEncryptionKey getInstance(Object o) {
        if (o instanceof SymmetricEncryptionKey) {
            return (SymmetricEncryptionKey)((Object)o);
        }
        if (o != null) {
            return new SymmetricEncryptionKey(ASN1TaggedObject.getInstance((Object)o, (int)128));
        }
        return null;
    }

    public static SymmetricEncryptionKey aes128ccm(byte[] octetString) {
        return new SymmetricEncryptionKey(0, (ASN1Encodable)new DEROctetString(octetString));
    }

    public static SymmetricEncryptionKey aes128ccm(ASN1OctetString octetString) {
        return new SymmetricEncryptionKey(0, (ASN1Encodable)octetString);
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getSymmetricEncryptionKey() {
        return this.symmetricEncryptionKey;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.symmetricEncryptionKey);
    }
}


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
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EciesP256EncryptedKey;

public class EncryptedDataEncryptionKey
extends ASN1Object
implements ASN1Choice {
    public static final int eciesNistP256 = 0;
    public static final int eciesBrainpoolP256r1 = 1;
    private final int choice;
    private final ASN1Encodable encryptedDataEncryptionKey;

    public EncryptedDataEncryptionKey(int choice, ASN1Encodable value) {
        this.choice = choice;
        this.encryptedDataEncryptionKey = value;
    }

    private EncryptedDataEncryptionKey(ASN1TaggedObject ato) {
        this.choice = ato.getTagNo();
        switch (ato.getTagNo()) {
            case 0: 
            case 1: {
                this.encryptedDataEncryptionKey = EciesP256EncryptedKey.getInstance(ato.getExplicitBaseObject());
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid choice value " + ato.getTagNo());
            }
        }
    }

    public static EncryptedDataEncryptionKey getInstance(Object o) {
        if (o instanceof EncryptedDataEncryptionKey) {
            return (EncryptedDataEncryptionKey)((Object)o);
        }
        if (o != null) {
            return new EncryptedDataEncryptionKey(ASN1TaggedObject.getInstance((Object)o, (int)128));
        }
        return null;
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getEncryptedDataEncryptionKey() {
        return this.encryptedDataEncryptionKey;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.encryptedDataEncryptionKey);
    }

    public static EncryptedDataEncryptionKey eciesNistP256(EciesP256EncryptedKey value) {
        return new EncryptedDataEncryptionKey(0, (ASN1Encodable)value);
    }

    public static EncryptedDataEncryptionKey eciesBrainpoolP256r1(EciesP256EncryptedKey value) {
        return new EncryptedDataEncryptionKey(1, (ASN1Encodable)value);
    }
}


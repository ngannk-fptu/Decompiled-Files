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
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicEncryptionKey;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SymmetricEncryptionKey;

public class EncryptionKey
extends ASN1Object
implements ASN1Choice {
    public static final int publicOption = 0;
    public static final int symmetric = 1;
    private final int choice;
    private final ASN1Encodable encryptionKey;

    public static EncryptionKey getInstance(Object o) {
        if (o instanceof EncryptionKey) {
            return (EncryptionKey)((Object)o);
        }
        if (o != null) {
            return new EncryptionKey(ASN1TaggedObject.getInstance((Object)o, (int)128));
        }
        return null;
    }

    public EncryptionKey(int choice, ASN1Encodable value) {
        this.choice = choice;
        switch (choice) {
            case 0: 
            case 1: {
                this.encryptionKey = value;
                return;
            }
        }
        throw new IllegalArgumentException("invalid choice value " + choice);
    }

    public static EncryptionKey publicOption(PublicEncryptionKey key) {
        return new EncryptionKey(0, (ASN1Encodable)key);
    }

    public static EncryptionKey symmetric(SymmetricEncryptionKey key) {
        return new EncryptionKey(1, (ASN1Encodable)key);
    }

    private EncryptionKey(ASN1TaggedObject value) {
        this(value.getTagNo(), (ASN1Encodable)value.getExplicitBaseObject());
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getEncryptionKey() {
        return this.encryptionKey;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.encryptionKey);
    }
}


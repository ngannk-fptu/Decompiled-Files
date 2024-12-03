/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DLSequence
 */
package org.bouncycastle.asn1.tsp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DLSequence;

public class EncryptionInfo
extends ASN1Object {
    private ASN1ObjectIdentifier encryptionInfoType;
    private ASN1Encodable encryptionInfoValue;

    public static EncryptionInfo getInstance(Object obj) {
        if (obj instanceof EncryptionInfo) {
            return (EncryptionInfo)((Object)obj);
        }
        if (obj != null) {
            return new EncryptionInfo(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    public static EncryptionInfo getInstance(ASN1TaggedObject obj, boolean explicit) {
        return EncryptionInfo.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)obj, (boolean)explicit));
    }

    private EncryptionInfo(ASN1Sequence sequence) {
        if (sequence.size() != 2) {
            throw new IllegalArgumentException("wrong sequence size in constructor: " + sequence.size());
        }
        this.encryptionInfoType = ASN1ObjectIdentifier.getInstance((Object)sequence.getObjectAt(0));
        this.encryptionInfoValue = sequence.getObjectAt(1);
    }

    public EncryptionInfo(ASN1ObjectIdentifier encryptionInfoType, ASN1Encodable encryptionInfoValue) {
        this.encryptionInfoType = encryptionInfoType;
        this.encryptionInfoValue = encryptionInfoValue;
    }

    public ASN1ObjectIdentifier getEncryptionInfoType() {
        return this.encryptionInfoType;
    }

    public ASN1Encodable getEncryptionInfoValue() {
        return this.encryptionInfoValue;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)this.encryptionInfoType);
        v.add(this.encryptionInfoValue);
        return new DLSequence(v);
    }
}


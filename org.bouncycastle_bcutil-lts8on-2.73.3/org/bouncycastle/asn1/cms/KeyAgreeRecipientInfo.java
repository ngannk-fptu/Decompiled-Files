/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.OriginatorIdentifierOrKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class KeyAgreeRecipientInfo
extends ASN1Object {
    private ASN1Integer version;
    private OriginatorIdentifierOrKey originator;
    private ASN1OctetString ukm;
    private AlgorithmIdentifier keyEncryptionAlgorithm;
    private ASN1Sequence recipientEncryptedKeys;

    public KeyAgreeRecipientInfo(OriginatorIdentifierOrKey originator, ASN1OctetString ukm, AlgorithmIdentifier keyEncryptionAlgorithm, ASN1Sequence recipientEncryptedKeys) {
        this.version = new ASN1Integer(3L);
        this.originator = originator;
        this.ukm = ukm;
        this.keyEncryptionAlgorithm = keyEncryptionAlgorithm;
        this.recipientEncryptedKeys = recipientEncryptedKeys;
    }

    private KeyAgreeRecipientInfo(ASN1Sequence seq) {
        int index = 0;
        this.version = (ASN1Integer)seq.getObjectAt(index++);
        this.originator = OriginatorIdentifierOrKey.getInstance((ASN1TaggedObject)seq.getObjectAt(index++), true);
        if (seq.getObjectAt(index) instanceof ASN1TaggedObject) {
            this.ukm = ASN1OctetString.getInstance((ASN1TaggedObject)((ASN1TaggedObject)seq.getObjectAt(index++)), (boolean)true);
        }
        this.keyEncryptionAlgorithm = AlgorithmIdentifier.getInstance((Object)seq.getObjectAt(index++));
        this.recipientEncryptedKeys = (ASN1Sequence)seq.getObjectAt(index++);
    }

    public static KeyAgreeRecipientInfo getInstance(ASN1TaggedObject obj, boolean explicit) {
        return KeyAgreeRecipientInfo.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)obj, (boolean)explicit));
    }

    public static KeyAgreeRecipientInfo getInstance(Object obj) {
        if (obj instanceof KeyAgreeRecipientInfo) {
            return (KeyAgreeRecipientInfo)((Object)obj);
        }
        if (obj != null) {
            return new KeyAgreeRecipientInfo(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public OriginatorIdentifierOrKey getOriginator() {
        return this.originator;
    }

    public ASN1OctetString getUserKeyingMaterial() {
        return this.ukm;
    }

    public AlgorithmIdentifier getKeyEncryptionAlgorithm() {
        return this.keyEncryptionAlgorithm;
    }

    public ASN1Sequence getRecipientEncryptedKeys() {
        return this.recipientEncryptedKeys;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(5);
        v.add((ASN1Encodable)this.version);
        v.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)this.originator));
        if (this.ukm != null) {
            v.add((ASN1Encodable)new DERTaggedObject(true, 1, (ASN1Encodable)this.ukm));
        }
        v.add((ASN1Encodable)this.keyEncryptionAlgorithm);
        v.add((ASN1Encodable)this.recipientEncryptedKeys);
        return new DERSequence(v);
    }
}


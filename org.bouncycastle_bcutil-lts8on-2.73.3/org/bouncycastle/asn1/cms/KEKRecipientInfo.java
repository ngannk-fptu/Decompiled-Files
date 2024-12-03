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
import org.bouncycastle.asn1.cms.KEKIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class KEKRecipientInfo
extends ASN1Object {
    private ASN1Integer version;
    private KEKIdentifier kekid;
    private AlgorithmIdentifier keyEncryptionAlgorithm;
    private ASN1OctetString encryptedKey;

    public KEKRecipientInfo(KEKIdentifier kekid, AlgorithmIdentifier keyEncryptionAlgorithm, ASN1OctetString encryptedKey) {
        this.version = new ASN1Integer(4L);
        this.kekid = kekid;
        this.keyEncryptionAlgorithm = keyEncryptionAlgorithm;
        this.encryptedKey = encryptedKey;
    }

    public KEKRecipientInfo(ASN1Sequence seq) {
        this.version = (ASN1Integer)seq.getObjectAt(0);
        this.kekid = KEKIdentifier.getInstance(seq.getObjectAt(1));
        this.keyEncryptionAlgorithm = AlgorithmIdentifier.getInstance((Object)seq.getObjectAt(2));
        this.encryptedKey = (ASN1OctetString)seq.getObjectAt(3);
    }

    public static KEKRecipientInfo getInstance(ASN1TaggedObject obj, boolean explicit) {
        return KEKRecipientInfo.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)obj, (boolean)explicit));
    }

    public static KEKRecipientInfo getInstance(Object obj) {
        if (obj instanceof KEKRecipientInfo) {
            return (KEKRecipientInfo)((Object)obj);
        }
        if (obj != null) {
            return new KEKRecipientInfo(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public KEKIdentifier getKekid() {
        return this.kekid;
    }

    public AlgorithmIdentifier getKeyEncryptionAlgorithm() {
        return this.keyEncryptionAlgorithm;
    }

    public ASN1OctetString getEncryptedKey() {
        return this.encryptedKey;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(4);
        v.add((ASN1Encodable)this.version);
        v.add((ASN1Encodable)this.kekid);
        v.add((ASN1Encodable)this.keyEncryptionAlgorithm);
        v.add((ASN1Encodable)this.encryptedKey);
        return new DERSequence(v);
    }
}


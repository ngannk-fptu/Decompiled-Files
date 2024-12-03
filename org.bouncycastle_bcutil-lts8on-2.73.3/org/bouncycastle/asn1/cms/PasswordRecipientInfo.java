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
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class PasswordRecipientInfo
extends ASN1Object {
    private ASN1Integer version;
    private AlgorithmIdentifier keyDerivationAlgorithm;
    private AlgorithmIdentifier keyEncryptionAlgorithm;
    private ASN1OctetString encryptedKey;

    public PasswordRecipientInfo(AlgorithmIdentifier keyEncryptionAlgorithm, ASN1OctetString encryptedKey) {
        this.version = new ASN1Integer(0L);
        this.keyEncryptionAlgorithm = keyEncryptionAlgorithm;
        this.encryptedKey = encryptedKey;
    }

    public PasswordRecipientInfo(AlgorithmIdentifier keyDerivationAlgorithm, AlgorithmIdentifier keyEncryptionAlgorithm, ASN1OctetString encryptedKey) {
        this.version = new ASN1Integer(0L);
        this.keyDerivationAlgorithm = keyDerivationAlgorithm;
        this.keyEncryptionAlgorithm = keyEncryptionAlgorithm;
        this.encryptedKey = encryptedKey;
    }

    private PasswordRecipientInfo(ASN1Sequence seq) {
        this.version = (ASN1Integer)seq.getObjectAt(0);
        if (seq.getObjectAt(1) instanceof ASN1TaggedObject) {
            this.keyDerivationAlgorithm = AlgorithmIdentifier.getInstance((ASN1TaggedObject)((ASN1TaggedObject)seq.getObjectAt(1)), (boolean)false);
            this.keyEncryptionAlgorithm = AlgorithmIdentifier.getInstance((Object)seq.getObjectAt(2));
            this.encryptedKey = (ASN1OctetString)seq.getObjectAt(3);
        } else {
            this.keyEncryptionAlgorithm = AlgorithmIdentifier.getInstance((Object)seq.getObjectAt(1));
            this.encryptedKey = (ASN1OctetString)seq.getObjectAt(2);
        }
    }

    public static PasswordRecipientInfo getInstance(ASN1TaggedObject obj, boolean explicit) {
        return PasswordRecipientInfo.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)obj, (boolean)explicit));
    }

    public static PasswordRecipientInfo getInstance(Object obj) {
        if (obj instanceof PasswordRecipientInfo) {
            return (PasswordRecipientInfo)((Object)obj);
        }
        if (obj != null) {
            return new PasswordRecipientInfo(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public AlgorithmIdentifier getKeyDerivationAlgorithm() {
        return this.keyDerivationAlgorithm;
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
        if (this.keyDerivationAlgorithm != null) {
            v.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.keyDerivationAlgorithm));
        }
        v.add((ASN1Encodable)this.keyEncryptionAlgorithm);
        v.add((ASN1Encodable)this.encryptedKey);
        return new DERSequence(v);
    }
}


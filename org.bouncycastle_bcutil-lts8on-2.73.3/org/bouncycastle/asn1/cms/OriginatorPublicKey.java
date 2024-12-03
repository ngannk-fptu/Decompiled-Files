/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1BitString
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERBitString
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class OriginatorPublicKey
extends ASN1Object {
    private AlgorithmIdentifier algorithm;
    private ASN1BitString publicKey;

    public OriginatorPublicKey(AlgorithmIdentifier algorithm, byte[] publicKey) {
        this.algorithm = algorithm;
        this.publicKey = new DERBitString(publicKey);
    }

    public OriginatorPublicKey(AlgorithmIdentifier algorithm, ASN1BitString publicKey) {
        this.algorithm = algorithm;
        this.publicKey = publicKey;
    }

    private OriginatorPublicKey(ASN1Sequence seq) {
        this.algorithm = AlgorithmIdentifier.getInstance((Object)seq.getObjectAt(0));
        this.publicKey = (DERBitString)seq.getObjectAt(1);
    }

    public static OriginatorPublicKey getInstance(ASN1TaggedObject obj, boolean explicit) {
        return new OriginatorPublicKey(ASN1Sequence.getInstance((ASN1TaggedObject)obj, (boolean)explicit));
    }

    public static OriginatorPublicKey getInstance(Object obj) {
        if (obj instanceof OriginatorPublicKey) {
            return (OriginatorPublicKey)((Object)obj);
        }
        if (obj != null) {
            return new OriginatorPublicKey(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    public AlgorithmIdentifier getAlgorithm() {
        return this.algorithm;
    }

    public DERBitString getPublicKey() {
        return DERBitString.convert((ASN1BitString)this.publicKey);
    }

    public ASN1BitString getPublicKeyData() {
        return this.publicKey;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)this.algorithm);
        v.add((ASN1Encodable)this.publicKey);
        return new DERSequence(v);
    }
}


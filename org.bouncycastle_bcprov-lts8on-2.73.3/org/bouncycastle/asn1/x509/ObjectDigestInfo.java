/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class ObjectDigestInfo
extends ASN1Object {
    public static final int publicKey = 0;
    public static final int publicKeyCert = 1;
    public static final int otherObjectDigest = 2;
    ASN1Enumerated digestedObjectType;
    ASN1ObjectIdentifier otherObjectTypeID;
    AlgorithmIdentifier digestAlgorithm;
    ASN1BitString objectDigest;

    public static ObjectDigestInfo getInstance(Object obj) {
        if (obj instanceof ObjectDigestInfo) {
            return (ObjectDigestInfo)obj;
        }
        if (obj != null) {
            return new ObjectDigestInfo(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public static ObjectDigestInfo getInstance(ASN1TaggedObject obj, boolean explicit) {
        return ObjectDigestInfo.getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public ObjectDigestInfo(int digestedObjectType, ASN1ObjectIdentifier otherObjectTypeID, AlgorithmIdentifier digestAlgorithm, byte[] objectDigest) {
        this.digestedObjectType = new ASN1Enumerated(digestedObjectType);
        if (digestedObjectType == 2) {
            this.otherObjectTypeID = otherObjectTypeID;
        }
        this.digestAlgorithm = digestAlgorithm;
        this.objectDigest = new DERBitString(objectDigest);
    }

    private ObjectDigestInfo(ASN1Sequence seq) {
        if (seq.size() > 4 || seq.size() < 3) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        this.digestedObjectType = ASN1Enumerated.getInstance(seq.getObjectAt(0));
        int offset = 0;
        if (seq.size() == 4) {
            this.otherObjectTypeID = ASN1ObjectIdentifier.getInstance(seq.getObjectAt(1));
            ++offset;
        }
        this.digestAlgorithm = AlgorithmIdentifier.getInstance(seq.getObjectAt(1 + offset));
        this.objectDigest = ASN1BitString.getInstance(seq.getObjectAt(2 + offset));
    }

    public ASN1Enumerated getDigestedObjectType() {
        return this.digestedObjectType;
    }

    public ASN1ObjectIdentifier getOtherObjectTypeID() {
        return this.otherObjectTypeID;
    }

    public AlgorithmIdentifier getDigestAlgorithm() {
        return this.digestAlgorithm;
    }

    public ASN1BitString getObjectDigest() {
        return this.objectDigest;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(4);
        v.add(this.digestedObjectType);
        if (this.otherObjectTypeID != null) {
            v.add(this.otherObjectTypeID);
        }
        v.add(this.digestAlgorithm);
        v.add(this.objectDigest);
        return new DERSequence(v);
    }
}


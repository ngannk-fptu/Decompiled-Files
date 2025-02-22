/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.sec;

import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.util.BigIntegers;

public class ECPrivateKey
extends ASN1Object {
    private ASN1Sequence seq;

    private ECPrivateKey(ASN1Sequence seq) {
        this.seq = seq;
    }

    public static ECPrivateKey getInstance(Object obj) {
        if (obj instanceof ECPrivateKey) {
            return (ECPrivateKey)obj;
        }
        if (obj != null) {
            return new ECPrivateKey(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public ECPrivateKey(int orderBitLength, BigInteger key) {
        byte[] bytes = BigIntegers.asUnsignedByteArray((orderBitLength + 7) / 8, key);
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add(new ASN1Integer(1L));
        v.add(new DEROctetString(bytes));
        this.seq = new DERSequence(v);
    }

    public ECPrivateKey(int orderBitLength, BigInteger key, ASN1Encodable parameters) {
        this(orderBitLength, key, null, parameters);
    }

    public ECPrivateKey(int orderBitLength, BigInteger key, ASN1BitString publicKey, ASN1Encodable parameters) {
        byte[] bytes = BigIntegers.asUnsignedByteArray((orderBitLength + 7) / 8, key);
        ASN1EncodableVector v = new ASN1EncodableVector(4);
        v.add(new ASN1Integer(1L));
        v.add(new DEROctetString(bytes));
        if (parameters != null) {
            v.add(new DERTaggedObject(true, 0, parameters));
        }
        if (publicKey != null) {
            v.add(new DERTaggedObject(true, 1, (ASN1Encodable)publicKey));
        }
        this.seq = new DERSequence(v);
    }

    public BigInteger getKey() {
        ASN1OctetString octs = (ASN1OctetString)this.seq.getObjectAt(1);
        return new BigInteger(1, octs.getOctets());
    }

    public ASN1BitString getPublicKey() {
        return (ASN1BitString)this.getObjectInTag(1, 3);
    }

    public ASN1Object getParametersObject() {
        return this.getObjectInTag(0, -1);
    }

    private ASN1Object getObjectInTag(int tagNo, int baseTagNo) {
        Enumeration e = this.seq.getObjects();
        while (e.hasMoreElements()) {
            ASN1TaggedObject tag;
            ASN1Encodable obj = (ASN1Encodable)e.nextElement();
            if (!(obj instanceof ASN1TaggedObject) || !(tag = (ASN1TaggedObject)obj).hasContextTag(tagNo)) continue;
            return baseTagNo < 0 ? tag.getExplicitBaseObject().toASN1Primitive() : tag.getBaseUniversal(true, baseTagNo);
        }
        return null;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.seq;
    }
}


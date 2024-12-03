/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.asn1.cms;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class RsaKemParameters
extends ASN1Object {
    private final AlgorithmIdentifier keyDerivationFunction;
    private final BigInteger keyLength;

    private RsaKemParameters(ASN1Sequence sequence) {
        if (sequence.size() != 2) {
            throw new IllegalArgumentException("ASN.1 SEQUENCE should be of length 2");
        }
        this.keyDerivationFunction = AlgorithmIdentifier.getInstance((Object)sequence.getObjectAt(0));
        this.keyLength = ASN1Integer.getInstance((Object)sequence.getObjectAt(1)).getValue();
    }

    public static RsaKemParameters getInstance(Object o) {
        if (o instanceof RsaKemParameters) {
            return (RsaKemParameters)((Object)o);
        }
        if (o != null) {
            return new RsaKemParameters(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public RsaKemParameters(AlgorithmIdentifier keyDerivationFunction, int keyLength) {
        this.keyDerivationFunction = keyDerivationFunction;
        this.keyLength = BigInteger.valueOf(keyLength);
    }

    public AlgorithmIdentifier getKeyDerivationFunction() {
        return this.keyDerivationFunction;
    }

    public BigInteger getKeyLength() {
        return this.keyLength;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)this.keyDerivationFunction);
        v.add((ASN1Encodable)new ASN1Integer(this.keyLength));
        return new DERSequence(v);
    }
}


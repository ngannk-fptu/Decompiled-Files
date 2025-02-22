/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.cms;

import java.math.BigInteger;
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

    private RsaKemParameters(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("ASN.1 SEQUENCE should be of length 2");
        }
        this.keyDerivationFunction = AlgorithmIdentifier.getInstance(aSN1Sequence.getObjectAt(0));
        this.keyLength = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(1)).getValue();
    }

    public static RsaKemParameters getInstance(Object object) {
        if (object instanceof RsaKemParameters) {
            return (RsaKemParameters)object;
        }
        if (object != null) {
            return new RsaKemParameters(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public RsaKemParameters(AlgorithmIdentifier algorithmIdentifier, int n) {
        this.keyDerivationFunction = algorithmIdentifier;
        this.keyLength = BigInteger.valueOf(n);
    }

    public AlgorithmIdentifier getKeyDerivationFunction() {
        return this.keyDerivationFunction;
    }

    public BigInteger getKeyLength() {
        return this.keyLength;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(2);
        aSN1EncodableVector.add(this.keyDerivationFunction);
        aSN1EncodableVector.add(new ASN1Integer(this.keyLength));
        return new DERSequence(aSN1EncodableVector);
    }
}


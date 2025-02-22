/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.misc;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class ScryptParams
extends ASN1Object {
    private final byte[] salt;
    private final BigInteger costParameter;
    private final BigInteger blockSize;
    private final BigInteger parallelizationParameter;
    private final BigInteger keyLength;

    public ScryptParams(byte[] salt, int costParameter, int blockSize, int parallelizationParameter) {
        this(salt, BigInteger.valueOf(costParameter), BigInteger.valueOf(blockSize), BigInteger.valueOf(parallelizationParameter), null);
    }

    public ScryptParams(byte[] salt, int costParameter, int blockSize, int parallelizationParameter, int keyLength) {
        this(salt, BigInteger.valueOf(costParameter), BigInteger.valueOf(blockSize), BigInteger.valueOf(parallelizationParameter), BigInteger.valueOf(keyLength));
    }

    public ScryptParams(byte[] salt, BigInteger costParameter, BigInteger blockSize, BigInteger parallelizationParameter, BigInteger keyLength) {
        this.salt = Arrays.clone(salt);
        this.costParameter = costParameter;
        this.blockSize = blockSize;
        this.parallelizationParameter = parallelizationParameter;
        this.keyLength = keyLength;
    }

    public static ScryptParams getInstance(Object o) {
        if (o instanceof ScryptParams) {
            return (ScryptParams)o;
        }
        if (o != null) {
            return new ScryptParams(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    private ScryptParams(ASN1Sequence seq) {
        if (seq.size() != 4 && seq.size() != 5) {
            throw new IllegalArgumentException("invalid sequence: size = " + seq.size());
        }
        this.salt = Arrays.clone(ASN1OctetString.getInstance(seq.getObjectAt(0)).getOctets());
        this.costParameter = ASN1Integer.getInstance(seq.getObjectAt(1)).getValue();
        this.blockSize = ASN1Integer.getInstance(seq.getObjectAt(2)).getValue();
        this.parallelizationParameter = ASN1Integer.getInstance(seq.getObjectAt(3)).getValue();
        this.keyLength = seq.size() == 5 ? ASN1Integer.getInstance(seq.getObjectAt(4)).getValue() : null;
    }

    public byte[] getSalt() {
        return Arrays.clone(this.salt);
    }

    public BigInteger getCostParameter() {
        return this.costParameter;
    }

    public BigInteger getBlockSize() {
        return this.blockSize;
    }

    public BigInteger getParallelizationParameter() {
        return this.parallelizationParameter;
    }

    public BigInteger getKeyLength() {
        return this.keyLength;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(5);
        v.add(new DEROctetString(this.salt));
        v.add(new ASN1Integer(this.costParameter));
        v.add(new ASN1Integer(this.blockSize));
        v.add(new ASN1Integer(this.parallelizationParameter));
        if (this.keyLength != null) {
            v.add(new ASN1Integer(this.keyLength));
        }
        return new DERSequence(v);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.signers;

import java.io.IOException;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.crypto.signers.DSAEncoding;
import org.bouncycastle.util.Arrays;

public class StandardDSAEncoding
implements DSAEncoding {
    public static final StandardDSAEncoding INSTANCE = new StandardDSAEncoding();

    @Override
    public byte[] encode(BigInteger n, BigInteger r, BigInteger s) throws IOException {
        ASN1EncodableVector v = new ASN1EncodableVector();
        this.encodeValue(n, v, r);
        this.encodeValue(n, v, s);
        return new DERSequence(v).getEncoded("DER");
    }

    @Override
    public BigInteger[] decode(BigInteger n, byte[] encoding) throws IOException {
        BigInteger s;
        BigInteger r;
        byte[] expectedEncoding;
        ASN1Sequence seq = (ASN1Sequence)ASN1Primitive.fromByteArray(encoding);
        if (seq.size() == 2 && Arrays.areEqual(expectedEncoding = this.encode(n, r = this.decodeValue(n, seq, 0), s = this.decodeValue(n, seq, 1)), encoding)) {
            return new BigInteger[]{r, s};
        }
        throw new IllegalArgumentException("Malformed signature");
    }

    protected BigInteger checkValue(BigInteger n, BigInteger x) {
        if (x.signum() < 0 || null != n && x.compareTo(n) >= 0) {
            throw new IllegalArgumentException("Value out of range");
        }
        return x;
    }

    protected BigInteger decodeValue(BigInteger n, ASN1Sequence s, int pos) {
        return this.checkValue(n, ((ASN1Integer)s.getObjectAt(pos)).getValue());
    }

    protected void encodeValue(BigInteger n, ASN1EncodableVector v, BigInteger x) {
        v.add(new ASN1Integer(this.checkValue(n, x)));
    }
}


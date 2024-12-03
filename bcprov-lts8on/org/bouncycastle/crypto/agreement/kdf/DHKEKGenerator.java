/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.agreement.kdf;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.agreement.kdf.DHKDFParameters;
import org.bouncycastle.util.Pack;

public class DHKEKGenerator
implements DerivationFunction {
    private final Digest digest;
    private ASN1ObjectIdentifier algorithm;
    private int keySize;
    private byte[] z;
    private byte[] partyAInfo;

    public DHKEKGenerator(Digest digest) {
        this.digest = digest;
    }

    @Override
    public void init(DerivationParameters param) {
        DHKDFParameters params = (DHKDFParameters)param;
        this.algorithm = params.getAlgorithm();
        this.keySize = params.getKeySize();
        this.z = params.getZ();
        this.partyAInfo = params.getExtraInfo();
    }

    public Digest getDigest() {
        return this.digest;
    }

    @Override
    public int generateBytes(byte[] out, int outOff, int len) throws DataLengthException, IllegalArgumentException {
        if (out.length - len < outOff) {
            throw new OutputLengthException("output buffer too small");
        }
        long oBytes = len;
        int outLen = this.digest.getDigestSize();
        if (oBytes > 0x1FFFFFFFFL) {
            throw new IllegalArgumentException("Output length too large");
        }
        int cThreshold = (int)((oBytes + (long)outLen - 1L) / (long)outLen);
        byte[] dig = new byte[this.digest.getDigestSize()];
        int counter = 1;
        for (int i = 0; i < cThreshold; ++i) {
            this.digest.update(this.z, 0, this.z.length);
            ASN1EncodableVector v1 = new ASN1EncodableVector();
            ASN1EncodableVector v2 = new ASN1EncodableVector();
            v2.add(this.algorithm);
            v2.add(new DEROctetString(Pack.intToBigEndian(counter)));
            v1.add(new DERSequence(v2));
            if (this.partyAInfo != null) {
                v1.add(new DERTaggedObject(true, 0, (ASN1Encodable)new DEROctetString(this.partyAInfo)));
            }
            v1.add(new DERTaggedObject(true, 2, (ASN1Encodable)new DEROctetString(Pack.intToBigEndian(this.keySize))));
            try {
                byte[] other = new DERSequence(v1).getEncoded("DER");
                this.digest.update(other, 0, other.length);
            }
            catch (IOException e) {
                throw new IllegalArgumentException("unable to encode parameter info: " + e.getMessage());
            }
            this.digest.doFinal(dig, 0);
            if (len > outLen) {
                System.arraycopy(dig, 0, out, outOff, outLen);
                outOff += outLen;
                len -= outLen;
            } else {
                System.arraycopy(dig, 0, out, outOff, len);
            }
            ++counter;
        }
        this.digest.reset();
        return (int)oBytes;
    }
}


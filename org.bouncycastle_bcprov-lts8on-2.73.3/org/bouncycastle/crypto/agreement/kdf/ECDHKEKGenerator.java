/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.agreement.kdf;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.DigestDerivationFunction;
import org.bouncycastle.crypto.agreement.kdf.DHKDFParameters;
import org.bouncycastle.crypto.generators.KDF2BytesGenerator;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.util.Pack;

public class ECDHKEKGenerator
implements DigestDerivationFunction {
    private DigestDerivationFunction kdf;
    private ASN1ObjectIdentifier algorithm;
    private int keySize;
    private byte[] z;

    public ECDHKEKGenerator(Digest digest) {
        this.kdf = new KDF2BytesGenerator(digest);
    }

    @Override
    public void init(DerivationParameters param) {
        DHKDFParameters params = (DHKDFParameters)param;
        this.algorithm = params.getAlgorithm();
        this.keySize = params.getKeySize();
        this.z = params.getZ();
    }

    @Override
    public Digest getDigest() {
        return this.kdf.getDigest();
    }

    @Override
    public int generateBytes(byte[] out, int outOff, int len) throws DataLengthException, IllegalArgumentException {
        if (outOff + len > out.length) {
            throw new DataLengthException("output buffer too small");
        }
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new AlgorithmIdentifier(this.algorithm, DERNull.INSTANCE));
        v.add(new DERTaggedObject(true, 2, (ASN1Encodable)new DEROctetString(Pack.intToBigEndian(this.keySize))));
        try {
            this.kdf.init(new KDFParameters(this.z, new DERSequence(v).getEncoded("DER")));
        }
        catch (IOException e) {
            throw new IllegalArgumentException("unable to initialise kdf: " + e.getMessage());
        }
        return this.kdf.generateBytes(out, outOff, len);
    }
}


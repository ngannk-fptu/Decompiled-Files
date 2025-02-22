/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.agreement.kdf;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.crypto.DerivationParameters;

public class DHKDFParameters
implements DerivationParameters {
    private ASN1ObjectIdentifier algorithm;
    private int keySize;
    private byte[] z;
    private byte[] extraInfo;

    public DHKDFParameters(ASN1ObjectIdentifier algorithm, int keySize, byte[] z) {
        this(algorithm, keySize, z, null);
    }

    public DHKDFParameters(ASN1ObjectIdentifier algorithm, int keySize, byte[] z, byte[] extraInfo) {
        this.algorithm = algorithm;
        this.keySize = keySize;
        this.z = z;
        this.extraInfo = extraInfo;
    }

    public ASN1ObjectIdentifier getAlgorithm() {
        return this.algorithm;
    }

    public int getKeySize() {
        return this.keySize;
    }

    public byte[] getZ() {
        return this.z;
    }

    public byte[] getExtraInfo() {
        return this.extraInfo;
    }
}


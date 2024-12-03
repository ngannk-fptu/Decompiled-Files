/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.modes.KGCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class KGMac
implements Mac {
    private final KGCMBlockCipher cipher;
    private final int macSizeBits;

    public KGMac(KGCMBlockCipher cipher) {
        this.cipher = cipher;
        this.macSizeBits = cipher.getUnderlyingCipher().getBlockSize() * 8;
    }

    public KGMac(KGCMBlockCipher cipher, int macSizeBits) {
        this.cipher = cipher;
        this.macSizeBits = macSizeBits;
    }

    @Override
    public void init(CipherParameters params) throws IllegalArgumentException {
        if (!(params instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("KGMAC requires ParametersWithIV");
        }
        ParametersWithIV param = (ParametersWithIV)params;
        byte[] iv = param.getIV();
        KeyParameter keyParam = (KeyParameter)param.getParameters();
        this.cipher.init(true, new AEADParameters(keyParam, this.macSizeBits, iv));
    }

    @Override
    public String getAlgorithmName() {
        return this.cipher.getUnderlyingCipher().getAlgorithmName() + "-KGMAC";
    }

    @Override
    public int getMacSize() {
        return this.macSizeBits / 8;
    }

    @Override
    public void update(byte in) throws IllegalStateException {
        this.cipher.processAADByte(in);
    }

    @Override
    public void update(byte[] in, int inOff, int len) throws DataLengthException, IllegalStateException {
        this.cipher.processAADBytes(in, inOff, len);
    }

    @Override
    public int doFinal(byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        try {
            return this.cipher.doFinal(out, outOff);
        }
        catch (InvalidCipherTextException e) {
            throw new IllegalStateException(e.toString());
        }
    }

    @Override
    public void reset() {
        this.cipher.reset();
    }
}


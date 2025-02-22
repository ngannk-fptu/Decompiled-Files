/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.modes.GCMModeCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class GMac
implements Mac {
    private final GCMModeCipher cipher;
    private final int macSizeBits;

    public GMac(GCMModeCipher cipher) {
        this.cipher = cipher;
        this.macSizeBits = 128;
    }

    public GMac(GCMModeCipher cipher, int macSizeBits) {
        this.cipher = cipher;
        this.macSizeBits = macSizeBits;
    }

    @Override
    public void init(CipherParameters params) throws IllegalArgumentException {
        if (!(params instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("GMAC requires ParametersWithIV");
        }
        ParametersWithIV param = (ParametersWithIV)params;
        byte[] iv = param.getIV();
        KeyParameter keyParam = (KeyParameter)param.getParameters();
        this.cipher.init(true, new AEADParameters(keyParam, this.macSizeBits, iv));
    }

    @Override
    public String getAlgorithmName() {
        return this.cipher.getUnderlyingCipher().getAlgorithmName() + "-GMAC";
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


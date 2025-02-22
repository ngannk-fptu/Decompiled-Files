/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.MacDerivationFunction;
import org.bouncycastle.crypto.params.KDFDoublePipelineIterationParameters;
import org.bouncycastle.crypto.params.KeyParameter;

public class KDFDoublePipelineIterationBytesGenerator
implements MacDerivationFunction {
    private static final BigInteger INTEGER_MAX = BigInteger.valueOf(Integer.MAX_VALUE);
    private static final BigInteger TWO = BigInteger.valueOf(2L);
    private final Mac prf;
    private final int h;
    private byte[] fixedInputData;
    private int maxSizeExcl;
    private byte[] ios;
    private boolean useCounter;
    private int generatedBytes;
    private byte[] a;
    private byte[] k;

    public KDFDoublePipelineIterationBytesGenerator(Mac prf) {
        this.prf = prf;
        this.h = prf.getMacSize();
        this.a = new byte[this.h];
        this.k = new byte[this.h];
    }

    @Override
    public void init(DerivationParameters params) {
        BigInteger maxSize;
        if (!(params instanceof KDFDoublePipelineIterationParameters)) {
            throw new IllegalArgumentException("Wrong type of arguments given");
        }
        KDFDoublePipelineIterationParameters dpiParams = (KDFDoublePipelineIterationParameters)params;
        this.prf.init(new KeyParameter(dpiParams.getKI()));
        this.fixedInputData = dpiParams.getFixedInputData();
        int r = dpiParams.getR();
        this.ios = new byte[r / 8];
        this.maxSizeExcl = dpiParams.useCounter() ? ((maxSize = TWO.pow(r).multiply(BigInteger.valueOf(this.h))).compareTo(INTEGER_MAX) == 1 ? Integer.MAX_VALUE : maxSize.intValue()) : Integer.MAX_VALUE;
        this.useCounter = dpiParams.useCounter();
        this.generatedBytes = 0;
    }

    @Override
    public Mac getMac() {
        return this.prf;
    }

    @Override
    public int generateBytes(byte[] out, int outOff, int len) throws DataLengthException, IllegalArgumentException {
        int generatedBytesAfter = this.generatedBytes + len;
        if (generatedBytesAfter < 0 || generatedBytesAfter >= this.maxSizeExcl) {
            throw new DataLengthException("Current KDFCTR may only be used for " + this.maxSizeExcl + " bytes");
        }
        if (this.generatedBytes % this.h == 0) {
            this.generateNext();
        }
        int toGenerate = len;
        int posInK = this.generatedBytes % this.h;
        int leftInK = this.h - this.generatedBytes % this.h;
        int toCopy = Math.min(leftInK, toGenerate);
        System.arraycopy(this.k, posInK, out, outOff, toCopy);
        this.generatedBytes += toCopy;
        toGenerate -= toCopy;
        outOff += toCopy;
        while (toGenerate > 0) {
            this.generateNext();
            toCopy = Math.min(this.h, toGenerate);
            System.arraycopy(this.k, 0, out, outOff, toCopy);
            this.generatedBytes += toCopy;
            toGenerate -= toCopy;
            outOff += toCopy;
        }
        return len;
    }

    private void generateNext() {
        if (this.generatedBytes == 0) {
            this.prf.update(this.fixedInputData, 0, this.fixedInputData.length);
            this.prf.doFinal(this.a, 0);
        } else {
            this.prf.update(this.a, 0, this.a.length);
            this.prf.doFinal(this.a, 0);
        }
        this.prf.update(this.a, 0, this.a.length);
        if (this.useCounter) {
            int i = this.generatedBytes / this.h + 1;
            switch (this.ios.length) {
                case 4: {
                    this.ios[0] = (byte)(i >>> 24);
                }
                case 3: {
                    this.ios[this.ios.length - 3] = (byte)(i >>> 16);
                }
                case 2: {
                    this.ios[this.ios.length - 2] = (byte)(i >>> 8);
                }
                case 1: {
                    this.ios[this.ios.length - 1] = (byte)i;
                    break;
                }
                default: {
                    throw new IllegalStateException("Unsupported size of counter i");
                }
            }
            this.prf.update(this.ios, 0, this.ios.length);
        }
        this.prf.update(this.fixedInputData, 0, this.fixedInputData.length);
        this.prf.doFinal(this.k, 0);
    }
}


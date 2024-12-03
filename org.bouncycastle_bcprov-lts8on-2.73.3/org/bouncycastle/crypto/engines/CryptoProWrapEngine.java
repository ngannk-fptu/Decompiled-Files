/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.GOST28147Engine;
import org.bouncycastle.crypto.engines.GOST28147WrapEngine;
import org.bouncycastle.crypto.modes.GCFBBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.ParametersWithSBox;
import org.bouncycastle.crypto.params.ParametersWithUKM;
import org.bouncycastle.util.Pack;

public class CryptoProWrapEngine
extends GOST28147WrapEngine {
    @Override
    public void init(boolean forWrapping, CipherParameters param) {
        KeyParameter kParam;
        if (param instanceof ParametersWithRandom) {
            ParametersWithRandom pr = (ParametersWithRandom)param;
            param = pr.getParameters();
        }
        ParametersWithUKM pU = (ParametersWithUKM)param;
        byte[] sBox = null;
        if (pU.getParameters() instanceof ParametersWithSBox) {
            kParam = (KeyParameter)((ParametersWithSBox)pU.getParameters()).getParameters();
            sBox = ((ParametersWithSBox)pU.getParameters()).getSBox();
        } else {
            kParam = (KeyParameter)pU.getParameters();
        }
        kParam = new KeyParameter(CryptoProWrapEngine.cryptoProDiversify(kParam.getKey(), pU.getUKM(), sBox));
        if (sBox != null) {
            super.init(forWrapping, new ParametersWithUKM(new ParametersWithSBox(kParam, sBox), pU.getUKM()));
        } else {
            super.init(forWrapping, new ParametersWithUKM(kParam, pU.getUKM()));
        }
    }

    private static byte[] cryptoProDiversify(byte[] K, byte[] ukm, byte[] sBox) {
        for (int i = 0; i != 8; ++i) {
            int sOn = 0;
            int sOff = 0;
            for (int j = 0; j != 8; ++j) {
                int kj = Pack.littleEndianToInt(K, j * 4);
                if (CryptoProWrapEngine.bitSet(ukm[i], j)) {
                    sOn += kj;
                    continue;
                }
                sOff += kj;
            }
            byte[] s = new byte[8];
            Pack.intToLittleEndian(sOn, s, 0);
            Pack.intToLittleEndian(sOff, s, 4);
            GCFBBlockCipher c = new GCFBBlockCipher(new GOST28147Engine());
            c.init(true, new ParametersWithIV(new ParametersWithSBox(new KeyParameter(K), sBox), s));
            c.processBlock(K, 0, K, 0);
            c.processBlock(K, 8, K, 8);
            c.processBlock(K, 16, K, 16);
            c.processBlock(K, 24, K, 24);
        }
        return K;
    }

    private static boolean bitSet(byte v, int bitNo) {
        return (v & 1 << bitNo) != 0;
    }
}


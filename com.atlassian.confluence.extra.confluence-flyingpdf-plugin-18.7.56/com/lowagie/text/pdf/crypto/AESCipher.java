/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.crypto.BlockCipher
 *  org.bouncycastle.crypto.CipherParameters
 *  org.bouncycastle.crypto.engines.AESEngine
 *  org.bouncycastle.crypto.modes.CBCBlockCipher
 *  org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher
 *  org.bouncycastle.crypto.params.KeyParameter
 *  org.bouncycastle.crypto.params.ParametersWithIV
 */
package com.lowagie.text.pdf.crypto;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class AESCipher {
    private final PaddedBufferedBlockCipher bp;

    public AESCipher(boolean forEncryption, byte[] key, byte[] iv) {
        AESEngine aes = new AESEngine();
        CBCBlockCipher cbc = new CBCBlockCipher((BlockCipher)aes);
        this.bp = new PaddedBufferedBlockCipher((BlockCipher)cbc);
        KeyParameter kp = new KeyParameter(key);
        ParametersWithIV piv = new ParametersWithIV((CipherParameters)kp, iv);
        this.bp.init(forEncryption, (CipherParameters)piv);
    }

    public byte[] update(byte[] inp, int inpOff, int inpLen) {
        int neededLen = this.bp.getUpdateOutputSize(inpLen);
        byte[] outp = null;
        if (neededLen > 0) {
            outp = new byte[neededLen];
        } else {
            neededLen = 0;
        }
        this.bp.processBytes(inp, inpOff, inpLen, outp, 0);
        return outp;
    }

    public byte[] doFinal() {
        int neededLen = this.bp.getOutputSize(0);
        byte[] outp = new byte[neededLen];
        int n = 0;
        try {
            n = this.bp.doFinal(outp, 0);
        }
        catch (Exception ex) {
            return outp;
        }
        if (n != outp.length) {
            byte[] outp2 = new byte[n];
            System.arraycopy(outp, 0, outp2, 0, n);
            return outp2;
        }
        return outp;
    }
}


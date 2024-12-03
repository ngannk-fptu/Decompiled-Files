/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.util;

import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class DESEngine {
    protected static final int BLOCK_SIZE = 8;
    private Cipher cf;

    public DESEngine() {
    }

    public DESEngine(boolean encrypting, byte[] key) {
        this.init(encrypting, key);
    }

    public void init(boolean encrypting, byte[] key) {
        try {
            DESKeySpec ks = new DESKeySpec(key);
            SecretKeyFactory kf = SecretKeyFactory.getInstance("DES");
            SecretKey ky = kf.generateSecret(ks);
            this.cf = Cipher.getInstance("DES/ECB/NoPadding");
            this.cf.init(encrypting ? 1 : 2, ky);
        }
        catch (GeneralSecurityException e) {
            throw new RuntimeException("Error initializing DESEngine", e);
        }
    }

    public String getAlgorithmName() {
        return "DES";
    }

    public int getBlockSize() {
        return 8;
    }

    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) {
        if (this.cf == null) {
            throw new IllegalStateException("DES engine not initialised");
        }
        if (inOff + 8 > in.length) {
            throw new IllegalArgumentException("input buffer too short");
        }
        if (outOff + 8 > out.length) {
            throw new IllegalArgumentException("output buffer too short");
        }
        try {
            int len = this.cf.doFinal(in, inOff, 8, out, outOff);
            return len;
        }
        catch (GeneralSecurityException e) {
            throw new RuntimeException("Error processing data block in DESEngine", e);
        }
    }

    public void reset() {
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.random;

import com.opensymphony.module.random.Rijndael_Algorithm;
import java.security.InvalidKeyException;

public class Rijndael {
    private Object sessionKey;
    private int blocksize;
    private int keysize;

    public Rijndael(int keysize, int blocksize) {
        if (keysize != 128 && keysize != 192 && keysize != 256) {
            throw new RuntimeException("Invalid keysize");
        }
        if (blocksize != 128 && blocksize != 192 && blocksize != 256) {
            throw new RuntimeException("Invalid blocksize");
        }
        this.keysize = keysize;
        this.blocksize = blocksize;
    }

    public Rijndael() {
        this(128, 128);
    }

    public int getBlockSize() {
        return this.blocksize;
    }

    public int getKeySize() {
        return this.keysize;
    }

    public byte[] decipher(byte[] block) {
        return Rijndael_Algorithm.blockDecrypt(block, 0, this.sessionKey);
    }

    public byte[] encipher(byte[] block) {
        return Rijndael_Algorithm.blockEncrypt(block, 0, this.sessionKey);
    }

    public void initialize(byte[] key) {
        byte[] nkey = new byte[this.keysize >> 3];
        System.arraycopy(key, 0, nkey, 0, nkey.length);
        try {
            this.sessionKey = Rijndael_Algorithm.makeKey(nkey);
        }
        catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }
}


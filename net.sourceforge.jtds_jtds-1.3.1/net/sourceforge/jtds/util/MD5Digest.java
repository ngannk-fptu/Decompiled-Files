/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.util;

import java.security.DigestException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

public class MD5Digest {
    private static final int DIGEST_LENGTH = 16;
    MessageDigest md;

    public MD5Digest() {
        try {
            this.md = MessageDigest.getInstance("MD5");
        }
        catch (GeneralSecurityException e) {
            throw new RuntimeException("Error initializing MD5Digest", e);
        }
        this.reset();
    }

    public String getAlgorithmName() {
        return "MD5";
    }

    public int getDigestSize() {
        return 16;
    }

    public int doFinal(byte[] out, int outOff) {
        try {
            this.md.digest(out, outOff, 16);
        }
        catch (DigestException e) {
            throw new RuntimeException("Error processing data for MD5Digest", e);
        }
        return 16;
    }

    public void reset() {
        this.md.reset();
    }

    public void update(byte in) {
        this.md.update(in);
    }

    public void update(byte[] in, int inOff, int len) {
        this.md.update(in, inOff, len);
    }

    public void finish() {
        byte[] digTmp = new byte[16];
        this.doFinal(digTmp, 0);
    }
}


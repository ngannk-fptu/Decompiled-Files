/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.util.Arrays;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class KeyParameter
implements CipherParameters {
    private byte[] key;

    public KeyParameter(byte[] key) {
        this(key, 0, key.length);
    }

    public KeyParameter(byte[] key, int keyOff, int keyLen) {
        this(keyLen);
        System.arraycopy(key, keyOff, this.key, 0, keyLen);
    }

    private KeyParameter(int length) {
        this.key = new byte[length];
    }

    public void copyTo(byte[] buf, int off, int len) {
        if (this.key.length != len) {
            throw new IllegalArgumentException("len");
        }
        System.arraycopy(this.key, 0, buf, off, len);
    }

    public byte[] getKey() {
        return this.key;
    }

    public int getKeyLength() {
        return this.key.length;
    }

    public KeyParameter reverse() {
        KeyParameter reversed = new KeyParameter(this.key.length);
        Arrays.reverse(this.key, reversed.key);
        return reversed;
    }
}


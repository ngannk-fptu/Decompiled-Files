/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.spec;

import java.security.spec.KeySpec;
import org.bouncycastle.util.Arrays;

public class ScryptKeySpec
implements KeySpec {
    private final char[] password;
    private final byte[] salt;
    private final int costParameter;
    private final int blockSize;
    private final int parallelizationParameter;
    private final int keySize;

    public ScryptKeySpec(char[] password, byte[] salt, int costParameter, int blockSize, int parallelizationParameter, int keySize) {
        this.password = password;
        this.salt = Arrays.clone(salt);
        this.costParameter = costParameter;
        this.blockSize = blockSize;
        this.parallelizationParameter = parallelizationParameter;
        this.keySize = keySize;
    }

    public char[] getPassword() {
        return this.password;
    }

    public byte[] getSalt() {
        return Arrays.clone(this.salt);
    }

    public int getCostParameter() {
        return this.costParameter;
    }

    public int getBlockSize() {
        return this.blockSize;
    }

    public int getParallelizationParameter() {
        return this.parallelizationParameter;
    }

    public int getKeyLength() {
        return this.keySize;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.util;

import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.crypto.util.PBKDFConfig;

public class ScryptConfig
extends PBKDFConfig {
    private final int costParameter;
    private final int blockSize;
    private final int parallelizationParameter;
    private final int saltLength;

    private ScryptConfig(Builder builder) {
        super(MiscObjectIdentifiers.id_scrypt);
        this.costParameter = builder.costParameter;
        this.blockSize = builder.blockSize;
        this.parallelizationParameter = builder.parallelizationParameter;
        this.saltLength = builder.saltLength;
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

    public int getSaltLength() {
        return this.saltLength;
    }

    public static class Builder {
        private final int costParameter;
        private final int blockSize;
        private final int parallelizationParameter;
        private int saltLength = 16;

        public Builder(int costParameter, int blockSize, int parallelizationParameter) {
            if (costParameter <= 1 || !Builder.isPowerOf2(costParameter)) {
                throw new IllegalArgumentException("Cost parameter N must be > 1 and a power of 2");
            }
            this.costParameter = costParameter;
            this.blockSize = blockSize;
            this.parallelizationParameter = parallelizationParameter;
        }

        public Builder withSaltLength(int saltLength) {
            this.saltLength = saltLength;
            return this;
        }

        public ScryptConfig build() {
            return new ScryptConfig(this);
        }

        private static boolean isPowerOf2(int x) {
            return (x & x - 1) == 0;
        }
    }
}


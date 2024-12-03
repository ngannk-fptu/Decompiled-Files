/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CharToByteConverter;
import org.bouncycastle.crypto.PasswordConverter;
import org.bouncycastle.util.Arrays;

public class Argon2Parameters {
    public static final int ARGON2_d = 0;
    public static final int ARGON2_i = 1;
    public static final int ARGON2_id = 2;
    public static final int ARGON2_VERSION_10 = 16;
    public static final int ARGON2_VERSION_13 = 19;
    private static final int DEFAULT_ITERATIONS = 3;
    private static final int DEFAULT_MEMORY_COST = 12;
    private static final int DEFAULT_LANES = 1;
    private static final int DEFAULT_TYPE = 1;
    private static final int DEFAULT_VERSION = 19;
    private final byte[] salt;
    private final byte[] secret;
    private final byte[] additional;
    private final int iterations;
    private final int memory;
    private final int lanes;
    private final int version;
    private final int type;
    private final CharToByteConverter converter;

    private Argon2Parameters(int type, byte[] salt, byte[] secret, byte[] additional, int iterations, int memory, int lanes, int version, CharToByteConverter converter) {
        this.salt = Arrays.clone(salt);
        this.secret = Arrays.clone(secret);
        this.additional = Arrays.clone(additional);
        this.iterations = iterations;
        this.memory = memory;
        this.lanes = lanes;
        this.version = version;
        this.type = type;
        this.converter = converter;
    }

    public byte[] getSalt() {
        return Arrays.clone(this.salt);
    }

    public byte[] getSecret() {
        return Arrays.clone(this.secret);
    }

    public byte[] getAdditional() {
        return Arrays.clone(this.additional);
    }

    public int getIterations() {
        return this.iterations;
    }

    public int getMemory() {
        return this.memory;
    }

    public int getLanes() {
        return this.lanes;
    }

    public int getVersion() {
        return this.version;
    }

    public int getType() {
        return this.type;
    }

    public CharToByteConverter getCharToByteConverter() {
        return this.converter;
    }

    public void clear() {
        Arrays.clear(this.salt);
        Arrays.clear(this.secret);
        Arrays.clear(this.additional);
    }

    public static class Builder {
        private byte[] salt;
        private byte[] secret;
        private byte[] additional;
        private int iterations;
        private int memory;
        private int lanes;
        private int version;
        private final int type;
        private CharToByteConverter converter = PasswordConverter.UTF8;

        public Builder() {
            this(1);
        }

        public Builder(int type) {
            this.type = type;
            this.lanes = 1;
            this.memory = 4096;
            this.iterations = 3;
            this.version = 19;
        }

        public Builder withParallelism(int parallelism) {
            this.lanes = parallelism;
            return this;
        }

        public Builder withSalt(byte[] salt) {
            this.salt = Arrays.clone(salt);
            return this;
        }

        public Builder withSecret(byte[] secret) {
            this.secret = Arrays.clone(secret);
            return this;
        }

        public Builder withAdditional(byte[] additional) {
            this.additional = Arrays.clone(additional);
            return this;
        }

        public Builder withIterations(int iterations) {
            this.iterations = iterations;
            return this;
        }

        public Builder withMemoryAsKB(int memory) {
            this.memory = memory;
            return this;
        }

        public Builder withMemoryPowOfTwo(int memory) {
            this.memory = 1 << memory;
            return this;
        }

        public Builder withVersion(int version) {
            this.version = version;
            return this;
        }

        public Builder withCharToByteConverter(CharToByteConverter converter) {
            this.converter = converter;
            return this;
        }

        public Argon2Parameters build() {
            return new Argon2Parameters(this.type, this.salt, this.secret, this.additional, this.iterations, this.memory, this.lanes, this.version, this.converter);
        }

        public void clear() {
            Arrays.clear(this.salt);
            Arrays.clear(this.secret);
            Arrays.clear(this.additional);
        }
    }
}


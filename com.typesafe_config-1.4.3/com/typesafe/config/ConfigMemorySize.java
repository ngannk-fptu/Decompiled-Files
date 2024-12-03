/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config;

import java.math.BigInteger;

public final class ConfigMemorySize {
    private BigInteger bytes;

    private ConfigMemorySize(BigInteger bytes) {
        if (bytes.signum() < 0) {
            throw new IllegalArgumentException("Attempt to construct ConfigMemorySize with negative number: " + bytes);
        }
        this.bytes = bytes;
    }

    public static ConfigMemorySize ofBytes(BigInteger bytes) {
        return new ConfigMemorySize(bytes);
    }

    public static ConfigMemorySize ofBytes(long bytes) {
        return new ConfigMemorySize(BigInteger.valueOf(bytes));
    }

    public long toBytes() {
        if (this.bytes.bitLength() < 64) {
            return this.bytes.longValue();
        }
        throw new IllegalArgumentException("size-in-bytes value is out of range for a 64-bit long: '" + this.bytes + "'");
    }

    public BigInteger toBytesBigInteger() {
        return this.bytes;
    }

    public String toString() {
        return "ConfigMemorySize(" + this.bytes + ")";
    }

    public boolean equals(Object other) {
        if (other instanceof ConfigMemorySize) {
            return ((ConfigMemorySize)other).bytes.equals(this.bytes);
        }
        return false;
    }

    public int hashCode() {
        return this.bytes.hashCode();
    }
}


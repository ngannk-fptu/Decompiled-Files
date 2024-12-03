/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.cryptography;

import aQute.lib.hex.Hex;
import java.util.Arrays;

public abstract class Digest {
    final byte[] digest;

    protected Digest(byte[] checksum, int width) {
        this.digest = checksum;
        if (this.digest.length != width) {
            throw new IllegalArgumentException("Invalid width for digest: " + this.digest.length + " expected " + width);
        }
    }

    public byte[] digest() {
        return this.digest;
    }

    public String asHex() {
        return Hex.toHexString(this.digest());
    }

    public String toString() {
        return String.format("%s(d=%s)", this.getAlgorithm(), Hex.toHexString(this.digest));
    }

    public abstract String getAlgorithm();

    public boolean equals(Object other) {
        if (!(other instanceof Digest)) {
            return false;
        }
        Digest d = (Digest)other;
        return Arrays.equals(d.digest, this.digest);
    }

    public int hashCode() {
        return Arrays.hashCode(this.digest);
    }

    public byte[] toByteArray() {
        return this.digest();
    }
}


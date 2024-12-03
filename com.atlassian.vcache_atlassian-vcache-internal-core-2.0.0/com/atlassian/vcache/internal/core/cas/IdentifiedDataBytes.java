/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.vcache.internal.core.cas;

import com.atlassian.vcache.internal.core.cas.IdentifiedData;
import java.util.Arrays;
import java.util.Objects;

public class IdentifiedDataBytes
extends IdentifiedData {
    private final byte[] bytes;

    public IdentifiedDataBytes(byte[] bytes) {
        this.bytes = Objects.requireNonNull(bytes);
    }

    public byte[] getBytes() {
        return this.bytes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IdentifiedDataBytes)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        IdentifiedDataBytes that = (IdentifiedDataBytes)o;
        return Arrays.equals(this.bytes, that.bytes);
    }

    public int hashCode() {
        return Arrays.hashCode(this.bytes);
    }
}


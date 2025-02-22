/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.memory;

import com.hazelcast.memory.MemoryUnit;
import com.hazelcast.util.Preconditions;

public final class MemorySize {
    private static final int PRETTY_FORMAT_LIMIT = 10;
    private final long value;
    private final MemoryUnit unit;

    public MemorySize(long value) {
        this(value, MemoryUnit.BYTES);
    }

    public MemorySize(long value, MemoryUnit unit) {
        if (value < 0L) {
            throw new IllegalArgumentException("Memory size cannot be negative! -> " + value);
        }
        this.value = value;
        this.unit = Preconditions.checkNotNull(unit, "MemoryUnit is required!");
    }

    public long getValue() {
        return this.value;
    }

    public MemoryUnit getUnit() {
        return this.unit;
    }

    public long bytes() {
        return this.unit.toBytes(this.value);
    }

    public long kiloBytes() {
        return this.unit.toKiloBytes(this.value);
    }

    public long megaBytes() {
        return this.unit.toMegaBytes(this.value);
    }

    public long gigaBytes() {
        return this.unit.toGigaBytes(this.value);
    }

    public static MemorySize parse(String value) {
        return MemorySize.parse(value, MemoryUnit.BYTES);
    }

    public static MemorySize parse(String value, MemoryUnit defaultUnit) {
        if (value == null || value.length() == 0) {
            return new MemorySize(0L, MemoryUnit.BYTES);
        }
        MemoryUnit unit = defaultUnit;
        char last = value.charAt(value.length() - 1);
        if (!Character.isDigit(last)) {
            value = value.substring(0, value.length() - 1);
            switch (last) {
                case 'G': 
                case 'g': {
                    unit = MemoryUnit.GIGABYTES;
                    break;
                }
                case 'M': 
                case 'm': {
                    unit = MemoryUnit.MEGABYTES;
                    break;
                }
                case 'K': 
                case 'k': {
                    unit = MemoryUnit.KILOBYTES;
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Could not determine memory unit of " + value + last);
                }
            }
        }
        return new MemorySize(Long.parseLong(value), unit);
    }

    public String toPrettyString() {
        return MemorySize.toPrettyString(this.value, this.unit);
    }

    public String toString() {
        return this.value + " " + this.unit.toString();
    }

    public static String toPrettyString(long size) {
        return MemorySize.toPrettyString(size, MemoryUnit.BYTES);
    }

    public static String toPrettyString(long size, MemoryUnit unit) {
        if (unit.toGigaBytes(size) >= 10L) {
            return unit.toGigaBytes(size) + " GB";
        }
        if (unit.toMegaBytes(size) >= 10L) {
            return unit.toMegaBytes(size) + " MB";
        }
        if (unit.toKiloBytes(size) >= 10L) {
            return unit.toKiloBytes(size) + " KB";
        }
        if (size % 1024L == 0L) {
            return unit.toKiloBytes(size) + " KB";
        }
        return size + " bytes";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MemorySize that = (MemorySize)o;
        if (this.value != that.value) {
            return false;
        }
        return this.unit == that.unit;
    }

    public int hashCode() {
        int result = (int)(this.value ^ this.value >>> 32);
        result = 31 * result + (this.unit != null ? this.unit.hashCode() : 0);
        return result;
    }
}


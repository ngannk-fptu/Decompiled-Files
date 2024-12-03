/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.util.concurrent;

import java.util.Comparator;

public final class Memory {
    private final long number;
    private final Unit unit;

    public static Memory of(long number, Unit unit) {
        return new Memory(number, unit);
    }

    public static Memory bytes(long number) {
        return Memory.of(number, Unit.Bytes);
    }

    public static Memory kilobytes(long number) {
        return Memory.of(number, Unit.KB);
    }

    public static Memory megabytes(long number) {
        return new Memory(number, Unit.MB);
    }

    public static Memory gigabytes(long number) {
        return new Memory(number, Unit.GB);
    }

    public static Memory terabytes(long number) {
        return new Memory(number, Unit.TB);
    }

    Memory(long number, Unit unit) {
        this.number = number;
        this.unit = unit;
    }

    public long number() {
        return this.number;
    }

    public Unit unit() {
        return this.unit;
    }

    public long bytes() {
        return this.number * this.unit.bytes;
    }

    public Memory to(Unit unit) {
        return Memory.of(this.bytes() / unit.bytes, unit);
    }

    public String toString() {
        return this.number + " " + (Object)((Object)this.unit);
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (int)(this.number ^ this.number >>> 32);
        result = 31 * result + (this.unit == null ? 0 : this.unit.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        Memory other = (Memory)obj;
        if (this.number != other.number) {
            return false;
        }
        return this.unit == other.unit;
    }

    private static int cmp(long diff) {
        return diff < 0L ? -1 : (diff > 0L ? 1 : 0);
    }

    public static enum UnitComparator implements Comparator<Unit>
    {
        INSTANCE;


        @Override
        public int compare(Unit o1, Unit o2) {
            return Memory.cmp(o1.bytes - o2.bytes);
        }
    }

    public static enum MemoryComparator implements Comparator<Memory>
    {
        INSTANCE;


        @Override
        public int compare(Memory o1, Memory o2) {
            return Memory.cmp(o1.bytes() - o2.bytes());
        }
    }

    public static enum Unit {
        Bytes(0),
        KB(1),
        MB(2),
        GB(3),
        TB(4);

        final long bytes;

        private Unit(int pow) {
            this.bytes = Unit.pow(1L, pow);
        }

        private static final long pow(long amt, int power) {
            if (power < 1) {
                return amt;
            }
            return Unit.pow(amt * 1024L, power - 1);
        }
    }
}


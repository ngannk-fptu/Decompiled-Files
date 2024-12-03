/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.memory;

import com.hazelcast.util.QuickMath;

public enum MemoryUnit {
    BYTES{

        @Override
        public long convert(long value, MemoryUnit m) {
            return m.toBytes(value);
        }

        @Override
        public long toBytes(long value) {
            return value;
        }

        @Override
        public long toKiloBytes(long value) {
            return QuickMath.divideByAndRoundToInt(value, 1024);
        }

        @Override
        public long toMegaBytes(long value) {
            return QuickMath.divideByAndRoundToInt(value, 0x100000);
        }

        @Override
        public long toGigaBytes(long value) {
            return QuickMath.divideByAndRoundToInt(value, 0x40000000);
        }
    }
    ,
    KILOBYTES{

        @Override
        public long convert(long value, MemoryUnit m) {
            return m.toKiloBytes(value);
        }

        @Override
        public long toBytes(long value) {
            return value * 1024L;
        }

        @Override
        public long toKiloBytes(long value) {
            return value;
        }

        @Override
        public long toMegaBytes(long value) {
            return QuickMath.divideByAndRoundToInt(value, 1024);
        }

        @Override
        public long toGigaBytes(long value) {
            return QuickMath.divideByAndRoundToInt(value, 0x100000);
        }
    }
    ,
    MEGABYTES{

        @Override
        public long convert(long value, MemoryUnit m) {
            return m.toMegaBytes(value);
        }

        @Override
        public long toBytes(long value) {
            return value * 0x100000L;
        }

        @Override
        public long toKiloBytes(long value) {
            return value * 1024L;
        }

        @Override
        public long toMegaBytes(long value) {
            return value;
        }

        @Override
        public long toGigaBytes(long value) {
            return QuickMath.divideByAndRoundToInt(value, 1024);
        }
    }
    ,
    GIGABYTES{

        @Override
        public long convert(long value, MemoryUnit m) {
            return m.toGigaBytes(value);
        }

        @Override
        public long toBytes(long value) {
            return value * 0x40000000L;
        }

        @Override
        public long toKiloBytes(long value) {
            return value * 0x100000L;
        }

        @Override
        public long toMegaBytes(long value) {
            return value * 1024L;
        }

        @Override
        public long toGigaBytes(long value) {
            return value;
        }
    };

    static final int POWER = 10;
    static final int K = 1024;
    static final int M = 0x100000;
    static final int G = 0x40000000;

    public abstract long convert(long var1, MemoryUnit var3);

    public abstract long toBytes(long var1);

    public abstract long toKiloBytes(long var1);

    public abstract long toMegaBytes(long var1);

    public abstract long toGigaBytes(long var1);
}


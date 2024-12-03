/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config;

public enum MemoryUnit {
    BYTES(0, 'b'){

        @Override
        public long toBytes(long amount) {
            return amount;
        }

        @Override
        public long toKiloBytes(long amount) {
            return MemoryUnit.safeShift(amount, 1.KILOBYTES.offset - 1.BYTES.offset);
        }

        @Override
        public long toMegaBytes(long amount) {
            return MemoryUnit.safeShift(amount, 1.MEGABYTES.offset - 1.BYTES.offset);
        }

        @Override
        public long toGigaBytes(long amount) {
            return MemoryUnit.safeShift(amount, 1.GIGABYTES.offset - 1.BYTES.offset);
        }
    }
    ,
    KILOBYTES(MemoryUnit.BYTES.offset + 10, 'k'){

        @Override
        public long toBytes(long amount) {
            return MemoryUnit.safeShift(amount, 2.BYTES.offset - 2.KILOBYTES.offset);
        }

        @Override
        public long toKiloBytes(long amount) {
            return amount;
        }

        @Override
        public long toMegaBytes(long amount) {
            return MemoryUnit.safeShift(amount, 2.MEGABYTES.offset - 2.KILOBYTES.offset);
        }

        @Override
        public long toGigaBytes(long amount) {
            return MemoryUnit.safeShift(amount, 2.GIGABYTES.offset - 2.KILOBYTES.offset);
        }
    }
    ,
    MEGABYTES(MemoryUnit.KILOBYTES.offset + 10, 'm'){

        @Override
        public long toBytes(long amount) {
            return MemoryUnit.safeShift(amount, 3.BYTES.offset - 3.MEGABYTES.offset);
        }

        @Override
        public long toKiloBytes(long amount) {
            return MemoryUnit.safeShift(amount, 3.KILOBYTES.offset - 3.MEGABYTES.offset);
        }

        @Override
        public long toMegaBytes(long amount) {
            return amount;
        }

        @Override
        public long toGigaBytes(long amount) {
            return MemoryUnit.safeShift(amount, 3.GIGABYTES.offset - 3.MEGABYTES.offset);
        }
    }
    ,
    GIGABYTES(MemoryUnit.MEGABYTES.offset + 10, 'g'){

        @Override
        public long toBytes(long amount) {
            return MemoryUnit.safeShift(amount, 4.BYTES.offset - 4.GIGABYTES.offset);
        }

        @Override
        public long toKiloBytes(long amount) {
            return MemoryUnit.safeShift(amount, 4.KILOBYTES.offset - 4.GIGABYTES.offset);
        }

        @Override
        public long toMegaBytes(long amount) {
            return MemoryUnit.safeShift(amount, 4.MEGABYTES.offset - 4.GIGABYTES.offset);
        }

        @Override
        public long toGigaBytes(long amount) {
            return amount;
        }
    };

    private static final int OFFSET = 10;
    private final int offset;
    private final char unit;

    private MemoryUnit(int offset, char unit) {
        this.offset = offset;
        this.unit = unit;
    }

    public char getUnit() {
        return this.unit;
    }

    public abstract long toBytes(long var1);

    public abstract long toKiloBytes(long var1);

    public abstract long toMegaBytes(long var1);

    public abstract long toGigaBytes(long var1);

    public String toString(long amount) {
        return amount + Character.toString(this.unit);
    }

    public static MemoryUnit forUnit(char unit) throws IllegalArgumentException {
        for (MemoryUnit memoryUnit : MemoryUnit.values()) {
            if (memoryUnit.unit != unit) continue;
            return memoryUnit;
        }
        throw new IllegalArgumentException("'" + unit + "' suffix doesn't match any SizeUnit");
    }

    public static MemoryUnit parseUnit(String value) {
        if (MemoryUnit.hasUnit(value)) {
            return MemoryUnit.forUnit(Character.toLowerCase(value.charAt(value.length() - 1)));
        }
        return BYTES;
    }

    public static long parseAmount(String value) throws NumberFormatException {
        if (value == null) {
            throw new NullPointerException("Value can't be null!");
        }
        if (value.length() == 0) {
            throw new IllegalArgumentException("Value can't be an empty string!");
        }
        if (MemoryUnit.hasUnit(value)) {
            return Long.parseLong(value.substring(0, value.length() - 1).trim());
        }
        return Long.parseLong(value);
    }

    public static long parseSizeInBytes(String value) throws NumberFormatException, IllegalArgumentException {
        if (value.length() == 0) {
            throw new IllegalArgumentException("Value can't be an empty string!");
        }
        MemoryUnit memoryUnit = MemoryUnit.parseUnit(value);
        return memoryUnit.toBytes(MemoryUnit.parseAmount(value));
    }

    private static boolean hasUnit(String value) {
        if (value.length() > 0) {
            char potentialUnit = value.charAt(value.length() - 1);
            return potentialUnit < '0' || potentialUnit > '9';
        }
        return false;
    }

    private static long safeShift(long unit, long shift) {
        if (shift > 0L) {
            return unit >>> (int)shift;
        }
        if (shift <= (long)(-1 * Long.numberOfLeadingZeros(unit))) {
            return Long.MAX_VALUE;
        }
        return unit << (int)(-shift);
    }
}


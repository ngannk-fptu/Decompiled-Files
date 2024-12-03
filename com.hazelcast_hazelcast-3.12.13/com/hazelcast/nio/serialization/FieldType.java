/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.serialization;

public enum FieldType {
    PORTABLE(0, Integer.MAX_VALUE),
    BYTE(1, 1),
    BOOLEAN(2, 1),
    CHAR(3, 2),
    SHORT(4, 2),
    INT(5, 4),
    LONG(6, 8),
    FLOAT(7, 4),
    DOUBLE(8, 8),
    UTF(9, Integer.MAX_VALUE),
    PORTABLE_ARRAY(10, Integer.MAX_VALUE),
    BYTE_ARRAY(11, Integer.MAX_VALUE),
    BOOLEAN_ARRAY(12, Integer.MAX_VALUE),
    CHAR_ARRAY(13, Integer.MAX_VALUE),
    SHORT_ARRAY(14, Integer.MAX_VALUE),
    INT_ARRAY(15, Integer.MAX_VALUE),
    LONG_ARRAY(16, Integer.MAX_VALUE),
    FLOAT_ARRAY(17, Integer.MAX_VALUE),
    DOUBLE_ARRAY(18, Integer.MAX_VALUE),
    UTF_ARRAY(19, Integer.MAX_VALUE);

    private static final FieldType[] ALL;
    private static final int TYPES_COUNT = 10;
    private final byte type;
    private final int elementSize;

    private FieldType(int type, int elementSize) {
        this.type = (byte)type;
        this.elementSize = elementSize;
    }

    public byte getId() {
        return this.type;
    }

    public static FieldType get(byte type) {
        return ALL[type];
    }

    public boolean isArrayType() {
        return this.type >= FieldType.PORTABLE_ARRAY.type;
    }

    public FieldType getSingleType() {
        if (this.isArrayType()) {
            return FieldType.get((byte)(this.getId() % 10));
        }
        return this;
    }

    public boolean hasDefiniteSize() {
        return this.elementSize != Integer.MAX_VALUE;
    }

    public int getTypeSize() throws IllegalArgumentException {
        if (this.elementSize == Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Unsupported type - the size is variable or unknown!");
        }
        return this.elementSize;
    }

    static {
        ALL = FieldType.values();
    }
}


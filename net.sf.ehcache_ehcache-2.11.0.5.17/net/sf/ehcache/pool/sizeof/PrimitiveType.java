/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.pool.sizeof;

import net.sf.ehcache.pool.sizeof.JvmInformation;

enum PrimitiveType {
    BOOLEAN(Boolean.TYPE, 1),
    BYTE(Byte.TYPE, 1),
    CHAR(Character.TYPE, 2),
    SHORT(Short.TYPE, 2),
    INT(Integer.TYPE, 4),
    FLOAT(Float.TYPE, 4),
    DOUBLE(Double.TYPE, 8),
    LONG(Long.TYPE, 8);

    private Class<?> type;
    private int size;

    private PrimitiveType(Class<?> type, int size) {
        this.type = type;
        this.size = size;
    }

    public int getSize() {
        return this.size;
    }

    public Class<?> getType() {
        return this.type;
    }

    public static int getReferenceSize() {
        return JvmInformation.CURRENT_JVM_INFORMATION.getJavaPointerSize();
    }

    public static long getArraySize() {
        return JvmInformation.CURRENT_JVM_INFORMATION.getObjectHeaderSize() + INT.getSize();
    }

    public static PrimitiveType forType(Class<?> type) {
        for (PrimitiveType primitiveType : PrimitiveType.values()) {
            if (primitiveType.getType() != type) continue;
            return primitiveType;
        }
        return null;
    }
}


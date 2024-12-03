/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.expression.spel.ast;

public enum TypeCode {
    OBJECT(Object.class),
    BOOLEAN(Boolean.TYPE),
    BYTE(Byte.TYPE),
    CHAR(Character.TYPE),
    DOUBLE(Double.TYPE),
    FLOAT(Float.TYPE),
    INT(Integer.TYPE),
    LONG(Long.TYPE),
    SHORT(Short.TYPE);

    private Class<?> type;

    private TypeCode(Class<?> type) {
        this.type = type;
    }

    public Class<?> getType() {
        return this.type;
    }

    public static TypeCode forName(String name) {
        TypeCode[] tcs = TypeCode.values();
        for (int i2 = 1; i2 < tcs.length; ++i2) {
            if (!tcs[i2].name().equalsIgnoreCase(name)) continue;
            return tcs[i2];
        }
        return OBJECT;
    }

    public static TypeCode forClass(Class<?> clazz) {
        TypeCode[] allValues;
        for (TypeCode typeCode : allValues = TypeCode.values()) {
            if (clazz != typeCode.getType()) continue;
            return typeCode;
        }
        return OBJECT;
    }
}


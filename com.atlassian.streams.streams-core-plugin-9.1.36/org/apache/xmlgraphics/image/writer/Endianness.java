/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.writer;

public enum Endianness {
    DEFAULT,
    LITTLE_ENDIAN,
    BIG_ENDIAN;


    public static Endianness getEndianType(String value) {
        if (value != null) {
            for (Endianness endianValue : Endianness.values()) {
                if (!endianValue.toString().equalsIgnoreCase(value)) continue;
                return endianValue;
            }
        }
        return null;
    }
}


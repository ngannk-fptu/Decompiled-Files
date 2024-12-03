/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ddf;

public class EscherPropertyMetaData {
    public static final byte TYPE_UNKNOWN = 0;
    public static final byte TYPE_BOOLEAN = 1;
    public static final byte TYPE_RGB = 2;
    public static final byte TYPE_SHAPEPATH = 3;
    public static final byte TYPE_SIMPLE = 4;
    public static final byte TYPE_ARRAY = 5;
    private String description;
    private byte type;

    public EscherPropertyMetaData(String description) {
        this.description = description;
    }

    public EscherPropertyMetaData(String description, byte type) {
        this.description = description;
        this.type = type;
    }

    public String getDescription() {
        return this.description;
    }

    public byte getType() {
        return this.type;
    }
}


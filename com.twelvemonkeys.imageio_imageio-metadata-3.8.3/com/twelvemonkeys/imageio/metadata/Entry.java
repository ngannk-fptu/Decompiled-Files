/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.metadata;

public interface Entry {
    public Object getIdentifier();

    public String getFieldName();

    public Object getValue();

    public String getValueAsString();

    public String getTypeName();

    public int valueCount();
}


/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.util;

public interface EncodingScheme {
    public String encodeAsString(byte[] var1);

    public byte[] decode(String var1);
}


/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util;

import com.mchange.util.Base64FormatException;

public interface Base64Encoder {
    public String encode(byte[] var1);

    public byte[] decode(String var1) throws Base64FormatException;
}


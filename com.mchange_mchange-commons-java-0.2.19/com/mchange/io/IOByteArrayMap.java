/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.io;

import com.mchange.io.IOByteArrayEnumeration;
import java.io.IOException;

public interface IOByteArrayMap {
    public byte[] get(byte[] var1) throws IOException;

    public void put(byte[] var1, byte[] var2) throws IOException;

    public boolean putNoReplace(byte[] var1, byte[] var2) throws IOException;

    public boolean remove(byte[] var1) throws IOException;

    public boolean containsKey(byte[] var1) throws IOException;

    public IOByteArrayEnumeration keys() throws IOException;
}


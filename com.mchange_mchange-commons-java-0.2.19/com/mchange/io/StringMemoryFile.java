/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.io;

import com.mchange.io.ReadOnlyMemoryFile;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public interface StringMemoryFile
extends ReadOnlyMemoryFile {
    public String asString() throws IOException;

    public String asString(String var1) throws IOException, UnsupportedEncodingException;
}


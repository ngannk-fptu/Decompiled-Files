/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.io;

import com.mchange.io.IOEnumeration;
import java.io.IOException;

public interface IOByteArrayEnumeration
extends IOEnumeration {
    public byte[] nextBytes() throws IOException;

    public boolean hasMoreBytes() throws IOException;

    @Override
    public Object nextElement() throws IOException;

    @Override
    public boolean hasMoreElements() throws IOException;
}


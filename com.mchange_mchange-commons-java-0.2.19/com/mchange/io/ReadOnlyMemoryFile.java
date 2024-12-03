/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.io;

import java.io.File;
import java.io.IOException;

public interface ReadOnlyMemoryFile {
    public File getFile() throws IOException;

    public byte[] getBytes() throws IOException;
}


/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.io;

import com.mchange.io.IOEnumeration;
import java.io.File;
import java.io.IOException;

public interface FileEnumeration
extends IOEnumeration {
    public boolean hasMoreFiles() throws IOException;

    public File nextFile() throws IOException;
}


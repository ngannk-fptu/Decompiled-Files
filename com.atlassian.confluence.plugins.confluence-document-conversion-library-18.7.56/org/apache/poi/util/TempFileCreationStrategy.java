/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.util;

import java.io.File;
import java.io.IOException;

public interface TempFileCreationStrategy {
    public File createTempFile(String var1, String var2) throws IOException;

    public File createTempDirectory(String var1) throws IOException;
}


/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.util.zip;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.ZipEntry;

public interface Unzipper {
    public void unzip() throws IOException;

    public void conditionalUnzip() throws IOException;

    public File unzipFileInArchive(String var1) throws IOException, FileNotFoundException;

    public ZipEntry[] entries() throws IOException;
}


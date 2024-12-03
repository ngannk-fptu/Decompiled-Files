/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.spool;

import com.atlassian.core.spool.FileFactory;
import java.io.File;
import java.io.IOException;

public class DefaultSpoolFileFactory
implements FileFactory {
    private static final DefaultSpoolFileFactory INSTANCE = new DefaultSpoolFileFactory();
    private String spoolPrefix = "spool-";
    private String spoolSuffix = ".tmp";
    private File spoolDirectory = null;

    public static DefaultSpoolFileFactory getInstance() {
        return INSTANCE;
    }

    public void setSpoolPrefix(String spoolPrefix) {
        this.spoolPrefix = spoolPrefix;
    }

    public void setSpoolSuffix(String spoolSuffix) {
        this.spoolSuffix = spoolSuffix;
    }

    public void setSpoolDirectory(File spoolDirectory) {
        this.spoolDirectory = spoolDirectory;
    }

    @Override
    public File createNewFile() throws IOException {
        File file = File.createTempFile(this.spoolPrefix, this.spoolSuffix, this.spoolDirectory);
        file.deleteOnExit();
        return file;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.diagnostics.internal.platform.monitor.operatingsystem.directory;

import com.atlassian.diagnostics.internal.platform.monitor.operatingsystem.directory.DirectoryType;
import java.io.File;

public class Directory {
    private final File file;
    private final DirectoryType type;

    public Directory(File file, DirectoryType type) {
        this.file = file;
        this.type = type;
    }

    File getFile() {
        return this.file;
    }

    DirectoryType getType() {
        return this.type;
    }
}


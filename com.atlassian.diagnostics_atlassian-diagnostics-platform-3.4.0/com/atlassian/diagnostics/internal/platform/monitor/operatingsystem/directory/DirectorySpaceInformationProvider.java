/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.diagnostics.internal.platform.monitor.operatingsystem.directory;

import com.atlassian.diagnostics.internal.platform.monitor.operatingsystem.MemoryInformation;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;

public class DirectorySpaceInformationProvider
extends MemoryInformation {
    private final File directory;

    public DirectorySpaceInformationProvider(File directory) {
        this.directory = directory;
    }

    long freeSpace() {
        try {
            FileStore fileStore = Files.getFileStore(this.directory.toPath());
            return this.asMegaBytes(fileStore.getUsableSpace());
        }
        catch (IOException ignore) {
            return -1L;
        }
    }

    long totalSpace() {
        try {
            FileStore fileStore = Files.getFileStore(this.directory.toPath());
            return this.asMegaBytes(fileStore.getTotalSpace());
        }
        catch (IOException ignore) {
            return -1L;
        }
    }
}


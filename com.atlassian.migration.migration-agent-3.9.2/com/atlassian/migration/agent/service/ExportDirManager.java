/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service;

import java.nio.file.Path;

public interface ExportDirManager {
    public Path getExportFilePath(String var1);

    public long getExportSize(String var1);

    public String copyExportedFileToSharedHome(String var1, String var2);

    public void moveCompressedFilesToSharedHome(String var1, String var2);

    public void cleanupExportFile(Path var1);
}


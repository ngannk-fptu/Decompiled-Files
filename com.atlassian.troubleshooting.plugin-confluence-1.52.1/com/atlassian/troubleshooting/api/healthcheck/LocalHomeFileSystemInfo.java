/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.api.healthcheck;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Path;

public interface LocalHomeFileSystemInfo {
    public Path getLocalApplicationHomePath();

    public FileStore getLocalHomeFileStore() throws IOException;

    public int getRecommendedThresholdPercentage();

    public long getRecommendedThresholdGB();
}


/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.jfr.service;

import java.nio.file.Path;

public interface JfrEventExtractorService {
    public Path extractThreadDumps(Path var1);

    public Path extractThreadCpuLoadDumps(Path var1);
}


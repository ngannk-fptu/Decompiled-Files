/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.service;

import java.nio.file.Path;
import java.util.Objects;
import javax.annotation.Nonnull;

public class ExportPathHolder {
    private final Path rootExportPath;
    private final boolean isCustom;

    public ExportPathHolder(@Nonnull Path rootExportPath, boolean isCustom) {
        this.rootExportPath = Objects.requireNonNull(rootExportPath, "rootExportPath must not be null");
        this.isCustom = isCustom;
    }

    @Nonnull
    public Path getRootExportPath() {
        return this.rootExportPath;
    }

    public boolean isCustom() {
        return this.isCustom;
    }

    public String getAbsolutePathString() {
        return this.rootExportPath.toAbsolutePath().toString();
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.business.insights.core.service.api;

import com.atlassian.business.insights.core.service.ExportPathHolder;
import java.nio.file.NotDirectoryException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ConfigService {
    @Nonnull
    public ExportPathHolder getRootExportPathHolder();

    public void setCustomExportPath(@Nullable String var1) throws NotDirectoryException;
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.troubleshooting.api.healthcheck;

import java.io.File;
import javax.annotation.Nullable;

public interface LogFileHelper {
    public File getCurrentGCLog(File var1);

    public File getGCLogDir();

    @Nullable
    public File getCurrentCompilationLog();

    @Nullable
    public File getCurrentCatalinaOut();
}


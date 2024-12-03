/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 */
package com.atlassian.confluence.impl.backuprestore.converters;

import com.atlassian.confluence.backuprestore.BackupRestoreJobResult;
import com.google.gson.Gson;
import java.util.function.Function;

public class BackupRestoreJobResultToJsonConverter
implements Function<BackupRestoreJobResult, String> {
    private final Gson gson = new Gson();

    @Override
    public String apply(BackupRestoreJobResult internalJob) {
        return this.gson.toJson((Object)internalJob);
    }
}


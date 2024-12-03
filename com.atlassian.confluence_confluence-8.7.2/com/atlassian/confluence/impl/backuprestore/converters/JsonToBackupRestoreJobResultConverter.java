/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.reflect.TypeToken
 */
package com.atlassian.confluence.impl.backuprestore.converters;

import com.atlassian.confluence.backuprestore.BackupRestoreJobResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.function.Function;

public class JsonToBackupRestoreJobResultConverter
implements Function<String, BackupRestoreJobResult> {
    private final Gson gson = new Gson();

    @Override
    public BackupRestoreJobResult apply(String jobResultAsJson) {
        return (BackupRestoreJobResult)this.gson.fromJson(jobResultAsJson, new TypeToken<BackupRestoreJobResult>(){}.getType());
    }
}


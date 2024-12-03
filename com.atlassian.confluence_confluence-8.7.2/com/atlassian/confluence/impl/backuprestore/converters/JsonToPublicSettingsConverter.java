/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.reflect.TypeToken
 */
package com.atlassian.confluence.impl.backuprestore.converters;

import com.atlassian.confluence.backuprestore.BackupRestoreSettings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.function.Function;

public class JsonToPublicSettingsConverter
implements Function<String, BackupRestoreSettings> {
    private final Gson gson = new Gson();

    @Override
    public BackupRestoreSettings apply(String settings) {
        return (BackupRestoreSettings)this.gson.fromJson(settings, new TypeToken<BackupRestoreSettings>(){}.getType());
    }
}


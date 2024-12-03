/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 */
package com.atlassian.confluence.impl.backuprestore.converters;

import com.atlassian.confluence.backuprestore.BackupRestoreSettings;
import com.google.gson.Gson;
import java.util.function.Function;

public class PublicSettingsToJsonConverter
implements Function<BackupRestoreSettings, String> {
    private final Gson gson = new Gson();

    @Override
    public String apply(BackupRestoreSettings internalJobSettings) {
        return this.gson.toJson((Object)internalJobSettings);
    }
}


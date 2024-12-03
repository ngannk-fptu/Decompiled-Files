/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheSettingsDefaultsProvider
 *  com.atlassian.fugue.Option
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.cache;

import com.atlassian.cache.CacheSettingsDefaultsProvider;
import com.atlassian.confluence.util.FugueConversionUtil;
import com.atlassian.fugue.Option;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface CacheSettingsManager
extends CacheSettingsDefaultsProvider {
    @Deprecated
    default public Option<Integer> updateMaxEntries(@NonNull String name, int newValue) {
        return FugueConversionUtil.toComOption(this.changeMaxEntries(name, newValue));
    }

    public Optional<Integer> changeMaxEntries(@NonNull String var1, int var2);

    public boolean saveSettings();

    default public void reloadSettings() {
    }
}


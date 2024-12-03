/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.types;

import com.atlassian.confluence.languages.LocaleInfo;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface Viewed {
    default public @Nullable LocaleInfo getLocaleInfo() {
        return null;
    }

    default public @NonNull Map<String, Object> getProperties() {
        LocaleInfo localeInfo = this.getLocaleInfo();
        if (localeInfo == null) {
            return Collections.emptyMap();
        }
        Locale requestedLocale = localeInfo.getRequestedLocale();
        return ImmutableMap.of((Object)"requestedLocale", (Object)(requestedLocale != null ? requestedLocale.toLanguageTag() : ""), (Object)"selectedLocale", (Object)localeInfo.getSelectedLocale().toLanguageTag(), (Object)"localeSelectionReason", (Object)localeInfo.getSelectionReason().name());
    }
}


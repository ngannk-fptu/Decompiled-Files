/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.languages;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class LocaleInfo
implements Serializable {
    private final Locale requestedLocale;
    private final Locale selectedLocale;
    private final SelectionReason selectionReason;

    public LocaleInfo(@Nullable Locale requestedLocale, @NonNull Locale selectedLocale, @NonNull SelectionReason selectionReason) {
        this.requestedLocale = requestedLocale;
        this.selectedLocale = Objects.requireNonNull(selectedLocale);
        this.selectionReason = Objects.requireNonNull(selectionReason);
    }

    public @Nullable Locale getRequestedLocale() {
        return this.requestedLocale;
    }

    public @NonNull Locale getSelectedLocale() {
        return this.selectedLocale;
    }

    public @NonNull SelectionReason getSelectionReason() {
        return this.selectionReason;
    }

    @Deprecated
    static class CacheKey {
        public static final String DEFAULT_LOCALE_INFO_CACHE_KEY = "confluence.locale.info.default";
        private final String userName;

        public CacheKey(@NonNull String userName) {
            this.userName = Objects.requireNonNull(userName);
        }

        public boolean equals(Object other) {
            return this == other || other != null && other.getClass() == this.getClass() && this.userName.equals(((CacheKey)other).userName);
        }

        public int hashCode() {
            return this.userName.hashCode();
        }
    }

    public static enum SelectionReason {
        GLOBAL,
        BROWSER,
        PROFILE,
        OVERRIDE;

    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.notifications;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.plugin.ModuleCompleteKey;
import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nonnull;

@ExperimentalApi
public final class NotificationContentCacheKey {
    private final UUID id;
    private final ModuleCompleteKey key;
    private final Locale locale;
    private final ContentId contentId;
    private final Expansion[] expansions;

    public NotificationContentCacheKey(@Nonnull UUID id, @Nonnull ModuleCompleteKey key, @Nonnull Locale locale, @Nonnull ContentId contentId, @Nonnull Expansion[] expansions) {
        Preconditions.checkNotNull((Object)id, (Object)"id");
        Preconditions.checkNotNull((Object)key, (Object)"key");
        Preconditions.checkNotNull((Object)locale, (Object)"locale");
        Preconditions.checkNotNull((Object)contentId, (Object)"contentId");
        Preconditions.checkNotNull((Object)expansions, (Object)"expansions");
        this.id = id;
        this.key = key;
        this.locale = locale;
        this.contentId = contentId;
        this.expansions = expansions;
    }

    public UUID getId() {
        return this.id;
    }

    public ContentId getContentId() {
        return this.contentId;
    }

    public ModuleCompleteKey getKey() {
        return this.key;
    }

    public Expansion[] getExpansions() {
        return this.expansions;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        NotificationContentCacheKey that = (NotificationContentCacheKey)o;
        if (!this.id.equals(that.id)) {
            return false;
        }
        if (!this.key.equals((Object)that.key)) {
            return false;
        }
        if (!this.locale.equals(that.locale)) {
            return false;
        }
        if (!this.contentId.equals((Object)that.contentId)) {
            return false;
        }
        return Objects.deepEquals(this.expansions, that.expansions);
    }

    public int hashCode() {
        int result = this.id.hashCode();
        result = 31 * result + this.key.hashCode();
        result = 31 * result + this.locale.hashCode();
        result = 31 * result + this.contentId.hashCode();
        result = 31 * result + Arrays.deepHashCode(this.expansions);
        return result;
    }
}


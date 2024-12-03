/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.ModuleCompleteKey
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.notifications;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.ModuleCompleteKey;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

@ExperimentalApi
public interface CachedContentFinder {
    @Deprecated
    public Option<Content> getContent(@Nonnull UUID var1, @Nonnull ModuleCompleteKey var2, @Nonnull Locale var3, @Nonnull ContentId var4, Expansion ... var5);

    default public Optional<Content> optionalContent(@Nonnull UUID notificationId, @Nonnull ModuleCompleteKey key, @Nonnull Locale locale, @Nonnull ContentId contentId, Expansion ... expansions) {
        return Optional.ofNullable((Content)this.getContent(notificationId, key, locale, contentId, expansions).getOrNull());
    }

    public Expansion exportBody();

    public ContentRepresentation exportRepresentation();
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.webhooks.WebhookScope
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.internal.webhooks;

import com.atlassian.confluence.spaces.Space;
import com.atlassian.webhooks.WebhookScope;
import java.util.Optional;
import javax.annotation.Nonnull;

public class SpaceScope
implements WebhookScope {
    private static final String TYPE_SPACE = "space";
    private final String spaceId;

    public SpaceScope(Space space) {
        this.spaceId = Long.toString(space.getId());
    }

    public SpaceScope(long spaceId) {
        this.spaceId = Long.toString(spaceId);
    }

    @Nonnull
    public Optional<String> getId() {
        return Optional.empty();
    }

    public String getType() {
        return TYPE_SPACE;
    }
}


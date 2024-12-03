/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Label
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.content.SpaceStatus
 *  com.atlassian.confluence.api.model.content.SpaceType
 *  com.atlassian.confluence.api.service.content.SpaceService$SpaceFinder
 */
package com.atlassian.confluence.impl.service.finder.content;

import com.atlassian.confluence.api.model.content.Label;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.content.SpaceStatus;
import com.atlassian.confluence.api.model.content.SpaceType;
import com.atlassian.confluence.api.service.content.SpaceService;
import com.atlassian.confluence.impl.service.finder.NoopFetcher;

public class NoopSpaceFinder
extends NoopFetcher<Space>
implements SpaceService.SpaceFinder {
    public SpaceService.SpaceFinder withKeys(String ... spaceKeys) {
        return this;
    }

    public SpaceService.SpaceFinder withType(SpaceType type) {
        return this;
    }

    public SpaceService.SpaceFinder withStatus(SpaceStatus status) {
        return this;
    }

    public SpaceService.SpaceFinder withLabels(Label ... labels) {
        return this;
    }

    public SpaceService.SpaceFinder withIsFavourited(boolean isFavourited) {
        return this;
    }

    public SpaceService.SpaceFinder withHasRetentionPolicy(boolean hasRetentionPolicy) {
        return this;
    }
}


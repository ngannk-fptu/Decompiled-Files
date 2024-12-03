/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.service.space;

import com.atlassian.confluence.content.service.SpaceService;
import com.atlassian.confluence.content.service.space.KeySpaceLocator;
import com.atlassian.confluence.content.service.space.SpaceLocator;
import com.atlassian.confluence.spaces.SpaceManager;

public class DefaultSpaceService
implements SpaceService {
    private SpaceManager spaceManager;

    @Override
    public SpaceLocator getKeySpaceLocator(String spaceKey) {
        return new KeySpaceLocator(this.spaceManager, spaceKey);
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public DefaultSpaceService(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }
}


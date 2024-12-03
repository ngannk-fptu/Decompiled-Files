/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.service.space;

import com.atlassian.confluence.content.service.space.SpaceLocator;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.service.AbstractSingleEntityLocator;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;

public class KeySpaceLocator
extends AbstractSingleEntityLocator
implements SpaceLocator {
    private final String spaceKey;
    private final SpaceManager spaceManager;

    public KeySpaceLocator(SpaceManager spaceManager, String spaceKey) {
        this.spaceManager = spaceManager;
        this.spaceKey = spaceKey;
    }

    @Override
    public Space getSpace() {
        return this.spaceManager.getSpace(this.spaceKey);
    }

    @Override
    public ConfluenceEntityObject getEntity() {
        return this.getSpace();
    }
}


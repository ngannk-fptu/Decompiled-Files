/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.streams.spi.StreamsValidator
 */
package com.atlassian.streams.confluence;

import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.streams.spi.StreamsValidator;

public class ConfluenceStreamsValidator
implements StreamsValidator {
    private final SpaceManager spaceManager;
    private final SpacePermissionManager spacePermissionManager;

    public ConfluenceStreamsValidator(SpaceManager spaceManager, SpacePermissionManager spacePermissionManager) {
        this.spaceManager = spaceManager;
        this.spacePermissionManager = spacePermissionManager;
    }

    public boolean isValidKey(String key) {
        Space space = this.spaceManager.getSpace(key);
        return space != null && this.spacePermissionManager.hasPermission("VIEWSPACE", space, AuthenticatedUserThreadLocal.getUser());
    }
}


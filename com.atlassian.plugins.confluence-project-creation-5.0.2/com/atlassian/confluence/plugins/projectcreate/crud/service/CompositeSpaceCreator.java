/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.user.ConfluenceUser
 *  io.atlassian.fugue.Option
 */
package com.atlassian.confluence.plugins.projectcreate.crud.service;

import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.plugins.projectcreate.crud.exception.CreateSpaceFailureException;
import com.atlassian.confluence.plugins.projectcreate.crud.service.SpaceCreator;
import com.atlassian.confluence.user.ConfluenceUser;
import io.atlassian.fugue.Option;
import java.util.List;
import java.util.Map;

public class CompositeSpaceCreator
implements SpaceCreator {
    private final List<SpaceCreator> spaceCreators;

    public CompositeSpaceCreator(List<SpaceCreator> spaceCreators) {
        this.spaceCreators = spaceCreators;
    }

    @Override
    public Space createSpace(ConfluenceUser user, String spaceKey, String spaceName, Map<String, String> context) throws CreateSpaceFailureException {
        for (SpaceCreator spaceCreator : this.spaceCreators) {
            if (!spaceCreator.canHandle(user, spaceKey, spaceName, context)) continue;
            return spaceCreator.createSpace(user, spaceKey, spaceName, context);
        }
        throw new CreateSpaceFailureException("confluence.projectcreate.space.create.failed");
    }

    @Override
    public boolean canHandle(ConfluenceUser user, String spaceKey, String spaceName, Map<String, String> context) {
        return true;
    }

    @Override
    public Option<String> validateCreateSpace(ConfluenceUser user, String spaceKey, String spaceName, Map<String, String> context) {
        for (SpaceCreator spaceCreator : this.spaceCreators) {
            if (!spaceCreator.canHandle(user, spaceKey, spaceName, context)) continue;
            return spaceCreator.validateCreateSpace(user, spaceKey, spaceName, context);
        }
        return Option.some((Object)"confluence.projectcreate.space.create.failed");
    }
}


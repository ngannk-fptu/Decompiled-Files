/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.service.content.SpaceService
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.projectcreate.crud.service;

import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.service.content.SpaceService;
import com.atlassian.confluence.plugins.projectcreate.crud.exception.CreateSpaceFailureException;
import com.atlassian.confluence.plugins.projectcreate.crud.service.AbstractSpaceCreator;
import com.atlassian.confluence.plugins.projectcreate.crud.service.SpaceCreator;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultSpaceCreator
extends AbstractSpaceCreator
implements SpaceCreator {
    @Autowired
    public DefaultSpaceCreator(@ComponentImport SpaceService spaceService) {
        super(spaceService);
    }

    @Override
    public Space createSpace(ConfluenceUser user, String spaceKey, String spaceName, Map<String, String> context) throws CreateSpaceFailureException {
        Space space = this.spaceService.create(Space.builder().key(spaceKey).name(spaceName).build(), false);
        if (space == null) {
            throw new CreateSpaceFailureException("confluence.projectcreate.space.create.failed");
        }
        return space;
    }

    @Override
    public boolean canHandle(ConfluenceUser user, String spaceKey, String spaceName, Map<String, String> context) {
        return true;
    }
}


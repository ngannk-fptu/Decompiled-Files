/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.validation.ValidationError
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.service.content.SpaceService
 *  com.atlassian.confluence.user.ConfluenceUser
 *  io.atlassian.fugue.Option
 */
package com.atlassian.confluence.plugins.projectcreate.crud.service;

import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.validation.ValidationError;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.content.SpaceService;
import com.atlassian.confluence.plugins.projectcreate.crud.service.SpaceCreator;
import com.atlassian.confluence.user.ConfluenceUser;
import io.atlassian.fugue.Option;
import java.util.Map;

public abstract class AbstractSpaceCreator
implements SpaceCreator {
    protected final SpaceService spaceService;

    protected AbstractSpaceCreator(SpaceService spaceService) {
        this.spaceService = spaceService;
    }

    @Override
    public Option<String> validateCreateSpace(ConfluenceUser user, String spaceKey, String spaceName, Map<String, String> context) {
        ValidationResult result = this.spaceService.validator().validateCreate(Space.builder().key(spaceKey).name(spaceName).build(), false);
        Iterable errors = result.getErrors();
        if (errors.iterator().hasNext()) {
            return Option.some((Object)((ValidationError)errors.iterator().next()).getMessage().getTranslation());
        }
        return Option.none();
    }
}


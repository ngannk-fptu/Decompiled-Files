/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.SpaceContentEntityObject
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.setup.settings.GlobalSettingsManager
 */
package com.atlassian.confluence.plugins.rest.entities.builders;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.plugins.rest.entities.ContentEntity;
import com.atlassian.confluence.plugins.rest.entities.builders.WikiLinkableContentEntityBuilder;
import com.atlassian.confluence.plugins.rest.manager.DateEntityFactory;
import com.atlassian.confluence.plugins.rest.manager.UserEntityHelper;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;

public class CommentEntityBuilder
extends WikiLinkableContentEntityBuilder<Comment> {
    public CommentEntityBuilder(GlobalSettingsManager settingsManager, DateEntityFactory dateEntityFactory, UserEntityHelper userEntityHelper) {
        super(settingsManager, dateEntityFactory, userEntityHelper);
    }

    @Override
    public ContentEntity build(Comment comment) {
        ContentEntityObject owner;
        ContentEntity entity = super.build(comment);
        if (comment.getParent() != null) {
            entity.setParentId(String.valueOf(comment.getParent().getId()));
        }
        if ((owner = comment.getContainer()) instanceof SpaceContentEntityObject) {
            entity.setSpace(CommentEntityBuilder.createSpaceEntity(((SpaceContentEntityObject)owner).getSpace()));
        }
        return entity;
    }
}


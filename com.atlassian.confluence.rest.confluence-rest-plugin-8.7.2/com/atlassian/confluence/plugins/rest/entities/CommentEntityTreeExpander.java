/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.plugins.rest.common.expand.AbstractRecursiveEntityExpander
 */
package com.atlassian.confluence.plugins.rest.entities;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.plugins.rest.entities.CommentEntityTree;
import com.atlassian.confluence.plugins.rest.entities.ContentEntity;
import com.atlassian.confluence.plugins.rest.entities.ContentEntityList;
import com.atlassian.confluence.plugins.rest.manager.RestContentManager;
import com.atlassian.plugins.rest.common.expand.AbstractRecursiveEntityExpander;
import java.util.ArrayList;
import java.util.List;

public class CommentEntityTreeExpander
extends AbstractRecursiveEntityExpander<CommentEntityTree> {
    private RestContentManager restContentManager;

    public CommentEntityTreeExpander(RestContentManager restContentManager) {
        this.restContentManager = restContentManager;
    }

    protected CommentEntityTree expandInternal(CommentEntityTree entity) {
        List<Comment> comments = entity.getComments();
        ArrayList<ContentEntity> entities = new ArrayList<ContentEntity>();
        for (Comment comment : comments) {
            ContentEntity commentEntity = this.restContentManager.convertToContentEntity((ContentEntityObject)comment);
            this.restContentManager.expand(commentEntity);
            entities.add(commentEntity);
        }
        entity.setContents(this.getChildrenList(entities, null).getContents());
        return entity;
    }

    private ContentEntityList getChildrenList(List<ContentEntity> entities, String parentId) {
        List<ContentEntity> topLevelEntities = this.getChildren(entities, parentId);
        ContentEntityList contents = new ContentEntityList(topLevelEntities.size(), null);
        contents.setContents(topLevelEntities);
        return contents;
    }

    private List<ContentEntity> getChildren(List<ContentEntity> entities, String parentId) {
        ArrayList<ContentEntity> children = new ArrayList<ContentEntity>();
        for (ContentEntity e : entities) {
            if ((parentId != null || e.getParentId() != null) && (parentId == null || e.getParentId() == null || !parentId.equals(e.getParentId()))) continue;
            children.add(e);
            e.setChildren(this.getChildrenList(entities, e.getId()));
        }
        return children;
    }
}


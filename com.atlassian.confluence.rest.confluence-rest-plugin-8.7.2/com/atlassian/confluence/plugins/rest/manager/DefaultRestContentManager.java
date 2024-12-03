/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.rest.manager;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.rest.entities.CommentEntityTree;
import com.atlassian.confluence.plugins.rest.entities.ContentBodyEntity;
import com.atlassian.confluence.plugins.rest.entities.ContentEntity;
import com.atlassian.confluence.plugins.rest.entities.ContentEntityList;
import com.atlassian.confluence.plugins.rest.entities.LabelEntityBuilder;
import com.atlassian.confluence.plugins.rest.entities.LabelEntityList;
import com.atlassian.confluence.plugins.rest.entities.builders.ContentEntityBuilder;
import com.atlassian.confluence.plugins.rest.entities.builders.EntityBuilderFactory;
import com.atlassian.confluence.plugins.rest.manager.ChildrenListWrapperCallback;
import com.atlassian.confluence.plugins.rest.manager.RequestContext;
import com.atlassian.confluence.plugins.rest.manager.RequestContextThreadLocal;
import com.atlassian.confluence.plugins.rest.manager.RestAttachmentManager;
import com.atlassian.confluence.plugins.rest.manager.RestContentManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.user.User;
import java.util.List;

public class DefaultRestContentManager
implements RestContentManager {
    private final ContentEntityManager contentEntityManager;
    private final PageManager pageManager;
    private final PermissionManager permissionManager;
    private final RestAttachmentManager restAttachmentManager;
    private final EntityBuilderFactory entityBuilderFactory;

    public DefaultRestContentManager(ContentEntityManager contentEntityManager, PermissionManager permissionManager, PageManager pageManager, RestAttachmentManager restAttachmentManager, EntityBuilderFactory entityBuilderFactory) {
        this.contentEntityManager = contentEntityManager;
        this.permissionManager = permissionManager;
        this.pageManager = pageManager;
        this.restAttachmentManager = restAttachmentManager;
        this.entityBuilderFactory = entityBuilderFactory;
    }

    @Override
    public ContentEntity expand(ContentEntity contentEntity) {
        return this.expand(contentEntity, this.contentEntityManager.getById(contentEntity.getIdLong().longValue()));
    }

    private ContentEntity expand(ContentEntity entity, ContentEntityObject object) {
        if (entity == null) {
            return null;
        }
        entity.setContentBody(new ContentBodyEntity(object.getBodyContent()));
        entity.setAttachments(this.restAttachmentManager.createAttachmentEntityListForContent(object));
        if (object instanceof Page || object instanceof Comment) {
            ChildrenListWrapperCallback children = new ChildrenListWrapperCallback(this, object);
            entity.setChildren(new ContentEntityList(children.getSize(), children));
        }
        if (object.isCurrent() && object instanceof Page) {
            entity.setComments(new CommentEntityTree(this.pageManager.getCommentCountOnPage(object.getId()), ((AbstractPage)object).getComments()));
        } else if (object.isCurrent() && object instanceof BlogPost) {
            entity.setComments(new CommentEntityTree(this.pageManager.getCommentCountOnBlog(object.getId()), ((AbstractPage)object).getComments()));
        }
        User user = RequestContextThreadLocal.get().getUser();
        LabelEntityList labels = this.convertToLabelEntityList(object.getVisibleLabels(user));
        entity.setLabels(labels);
        return entity;
    }

    private LabelEntityList convertToLabelEntityList(List<Label> labels) {
        LabelEntityList list = new LabelEntityList();
        LabelEntityBuilder builder = new LabelEntityBuilder();
        for (Label label : labels) {
            list.addLabel(builder.build(label));
        }
        return list;
    }

    @Override
    public ContentEntity getContentEntity(Long id, boolean expand) {
        ContentEntityObject object = this.contentEntityManager.getById(id.longValue());
        ContentEntity restEntity = this.convertToContentEntity(object);
        if (expand) {
            this.expand(restEntity, object);
        }
        return restEntity;
    }

    @Override
    public ContentEntity convertToContentEntity(ContentEntityObject object) {
        RequestContext requestContext = RequestContextThreadLocal.get();
        User user = requestContext.getUser();
        if (!this.permissionManager.hasPermission(user, Permission.VIEW, (Object)object)) {
            return null;
        }
        return this.createContentEntity(object);
    }

    public PermissionManager getPermissionManager() {
        return this.permissionManager;
    }

    private ContentEntity createContentEntity(ContentEntityObject object) {
        ContentEntityBuilder<?> builder = this.entityBuilderFactory.createContentEntityBuilder(object.getClass());
        return builder.build(object);
    }
}


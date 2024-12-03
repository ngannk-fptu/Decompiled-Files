/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.content.CustomContentManager
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.core.bean.EntityObject
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.service.impl;

import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.content.CustomContentManager;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.mobile.dto.CommentDto;
import com.atlassian.confluence.plugins.mobile.hibernate.MobileContentQueryFactory;
import com.atlassian.confluence.plugins.mobile.model.Inclusions;
import com.atlassian.confluence.plugins.mobile.service.MobileChildContentService;
import com.atlassian.confluence.plugins.mobile.service.converter.MobileCommentConverter;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.core.bean.EntityObject;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class MobileChildContentServiceImpl
implements MobileChildContentService {
    private static final int MAX_LIMIT_REQUEST = 100;
    private final CustomContentManager customContentManager;
    private final PermissionManager permissionManager;
    private final ContentEntityManager contentEntityManager;
    private final MobileCommentConverter commentConverter;

    @Autowired
    public MobileChildContentServiceImpl(@ComponentImport CustomContentManager customContentManager, @ComponentImport PermissionManager permissionManager, @ComponentImport @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, MobileCommentConverter commentConverter) {
        this.customContentManager = customContentManager;
        this.permissionManager = permissionManager;
        this.contentEntityManager = contentEntityManager;
        this.commentConverter = commentConverter;
    }

    @Override
    public List<Page> getPageChildren(long pageId, LimitedRequest limitedRequest) {
        PageResponse response = this.customContentManager.findByQueryAndFilter(MobileContentQueryFactory.findChildrenPageByParentPageId(pageId), true, limitedRequest, source -> this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, source));
        return response.getResults();
    }

    @Override
    public Map<Long, Integer> getPageChildrenCount(List<Long> pageIds) {
        HashSet pageHasChildren = new HashSet(this.customContentManager.findByQueryAndFilter(MobileContentQueryFactory.findPageHasChildren(pageIds), true, LimitedRequestImpl.create((int)pageIds.size()), pair -> true).getResults());
        return pageIds.stream().collect(Collectors.toMap(pageId -> pageId, pageId -> this.getChildrenCount(pageHasChildren, (Long)pageId)));
    }

    @Override
    public PageResponse<CommentDto> getComments(ContentId id, Expansions expansions, Inclusions inclusions) {
        ContentEntityObject container = this.contentEntityManager.getById(id.asLong());
        if (container == null || !this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)container)) {
            throw new NotFoundException("Cannot find comments with container id: " + id.serialise());
        }
        return PageResponseImpl.from(this.commentConverter.to(this.getComments(container, inclusions), expansions), (boolean)false).build();
    }

    public List<Comment> getComments(ContentEntityObject container, Inclusions inclusions) {
        ArrayList<Comment> comments = new ArrayList<Comment>();
        if (inclusions.isInclude(ContentType.PAGE.getValue())) {
            comments.addAll(container.getComments().stream().filter(comment -> comment.getParent() == null && (!comment.isInlineComment() || !comment.getStatus().isResolved())).collect(Collectors.toList()));
        }
        List attachments = container.getAttachments();
        if (inclusions.isInclude(ContentType.ATTACHMENT.getValue()) && !attachments.isEmpty()) {
            List<Long> attachmentIds = container.getAttachments().stream().map(EntityObject::getId).collect(Collectors.toList());
            PageResponse pageResponse = this.customContentManager.findByQueryAndFilter(MobileContentQueryFactory.findAttachmentCommentByContentId(attachmentIds), true, LimitedRequestImpl.create((int)100), comment -> true);
            comments.addAll(pageResponse.getResults().stream().filter(comment -> !comment.getStatus().isResolved()).collect(Collectors.toList()));
        }
        return comments;
    }

    private int getChildrenCount(Set<Long> pageHasChildren, Long pageId) {
        return pageHasChildren.contains(pageId) ? this.getPageChildren(pageId, LimitedRequestImpl.create((int)100)).size() : 0;
    }
}


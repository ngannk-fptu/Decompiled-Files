/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.ContentProperties
 *  com.atlassian.confluence.content.render.xhtml.Renderer
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.plugins.rest.dto.UserDtoFactory
 *  com.atlassian.confluence.plugins.rest.manager.DateEntityFactory
 */
package com.atlassian.confluence.plugins.mobile.dto;

import com.atlassian.confluence.content.ContentProperties;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.plugins.mobile.dto.CommentDto;
import com.atlassian.confluence.plugins.rest.dto.UserDtoFactory;
import com.atlassian.confluence.plugins.rest.manager.DateEntityFactory;

public final class CommentDtoFactory {
    private final Renderer mobileViewRenderer;
    private final DateEntityFactory dateEntityFactory;
    private final UserDtoFactory userDtoFactory;
    public static final String RESOLVED_PROP = "resolved";

    public CommentDtoFactory(Renderer mobileViewRenderer, UserDtoFactory userDtoFactory, DateEntityFactory dateEntityFactory) {
        this.mobileViewRenderer = mobileViewRenderer;
        this.dateEntityFactory = dateEntityFactory;
        this.userDtoFactory = userDtoFactory;
    }

    public CommentDto getCommentDto(Comment comment) {
        long parentId = comment.getParent() != null ? comment.getParent().getId() : 0L;
        String rendered = this.mobileViewRenderer.render((ContentEntityObject)comment);
        CommentDto commentDto = new CommentDto(comment.getId(), this.userDtoFactory.getUserDto(comment.getCreator()), rendered, this.dateEntityFactory.buildDateEntity(comment.getCreationDate()).getFriendly(), parentId);
        ContentProperties properties = comment.getProperties();
        commentDto.setInlineComment(comment.isInlineComment());
        if (comment.isInlineComment() && parentId == 0L) {
            commentDto.setTopInlineComment(true);
            commentDto.setResolved(Boolean.valueOf(properties.getStringProperty(RESOLVED_PROP)));
            commentDto.setHighlightContent(properties.getStringProperty("inline-original-selection"));
        }
        return commentDto;
    }
}


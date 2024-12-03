/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.content.render.xhtml.Renderer
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.like.LikeManager
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.CommentStatus
 *  com.atlassian.confluence.pages.CommentStatus$Value
 *  com.atlassian.confluence.pages.thumbnail.CannotGenerateThumbnailException
 *  com.atlassian.confluence.pages.thumbnail.ThumbnailInfo
 *  com.atlassian.confluence.pages.thumbnail.ThumbnailManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.service.converter;

import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.like.LikeManager;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.CommentStatus;
import com.atlassian.confluence.pages.thumbnail.CannotGenerateThumbnailException;
import com.atlassian.confluence.pages.thumbnail.ThumbnailInfo;
import com.atlassian.confluence.pages.thumbnail.ThumbnailManager;
import com.atlassian.confluence.plugins.mobile.dto.CommentDto;
import com.atlassian.confluence.plugins.mobile.dto.ContentDto;
import com.atlassian.confluence.plugins.mobile.dto.metadata.ContentMetadataDto;
import com.atlassian.confluence.plugins.mobile.dto.metadata.CurrentUserMetadataDto;
import com.atlassian.confluence.plugins.mobile.dto.metadata.LikeMetadataDto;
import com.atlassian.confluence.plugins.mobile.service.converter.MobileConverter;
import com.atlassian.confluence.plugins.mobile.service.factory.PersonFactory;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class MobileCommentConverter
implements MobileConverter<CommentDto, Comment> {
    private static final String COMMENT_STATUS = "status";
    private final LikeManager likeManager;
    private final PersonFactory personFactory;
    private final Renderer mobileViewRenderer;
    private final ThumbnailManager thumbnailManager;

    @Autowired
    public MobileCommentConverter(@Qualifier(value="mobileViewRenderer") Renderer mobileViewRenderer, @ComponentImport LikeManager likeManager, PersonFactory personFactory, @ComponentImport ThumbnailManager thumbnailManager) {
        this.likeManager = likeManager;
        this.personFactory = personFactory;
        this.mobileViewRenderer = mobileViewRenderer;
        this.thumbnailManager = thumbnailManager;
    }

    @Override
    public CommentDto to(@Nonnull Comment comment) {
        CommentDto commentDto = this.buildCommentDto(comment, comment.getParent(), Expansions.EMPTY);
        this.appendChildren(comment, commentDto, Expansions.EMPTY);
        return commentDto;
    }

    @Override
    public List<CommentDto> to(@Nullable List<Comment> comments, @Nonnull Expansions expansions) {
        if (comments == null || comments.isEmpty()) {
            return Collections.emptyList();
        }
        return comments.stream().map(comment -> this.to((Comment)comment, expansions)).sorted(Comparator.comparing(CommentDto::getCreatedDate)).collect(Collectors.toList());
    }

    @Override
    public CommentDto to(@Nonnull Comment rootComment, @Nonnull Expansions expansions) {
        CommentDto rootCommentDto = this.buildCommentDto(rootComment, null, expansions);
        if (rootComment.getChildren() == null || rootComment.getChildren().isEmpty()) {
            rootCommentDto.setChildren(Collections.emptyList());
        } else {
            this.appendChildren(rootComment, rootCommentDto, expansions);
        }
        return rootCommentDto;
    }

    private void appendChildren(Comment parent, CommentDto parentDto, Expansions expansions) {
        List children = parent.getChildren();
        if (children != null && !children.isEmpty()) {
            for (Comment child : children) {
                CommentDto childDto = this.buildCommentDto(child, parent, expansions);
                parentDto.addChildren(childDto);
                this.appendChildren(child, parentDto, expansions);
            }
        }
    }

    private CommentDto buildCommentDto(Comment comment, @Nullable Comment parent, Expansions expansions) {
        CommentDto.Builder builder = CommentDto.builder();
        builder.id(comment.getId()).createdDate(comment.getCreationDate()).location(MobileCommentConverter.isInlineComment(comment) ? "inline" : "footer");
        if (expansions.canExpand("author")) {
            builder.author(this.personFactory.forUser(comment.getCreator()));
        }
        if (expansions.canExpand("body")) {
            builder.body(this.mobileViewRenderer.render((ContentEntityObject)comment));
        }
        if (expansions.canExpand("parent") && parent != null) {
            builder.parent(CommentDto.builder().id(parent.getId()).build());
        }
        if (expansions.canExpand("container")) {
            ContentEntityObject container = comment.getContainer();
            builder.container(new ContentDto(container.getId(), container.getType()));
        }
        if (parent == null) {
            if (comment.isInlineComment()) {
                builder.properties(this.buildInlineProperties(comment.getStatus(), comment.getProperties().getStringProperty("inline-original-selection")));
            } else if (comment.getContainer() instanceof Attachment) {
                builder.properties(this.buildInlineProperties(comment.getStatus(), this.getThumbnailHtml((Attachment)comment.getContainer())));
            }
        }
        if (expansions.canExpand("metadata")) {
            builder.metadata(this.buildMetadata(comment));
        }
        return builder.build();
    }

    private ContentMetadataDto buildMetadata(Comment comment) {
        CurrentUserMetadataDto.Builder currentUserMetadata = new CurrentUserMetadataDto.Builder();
        LikeMetadataDto likeMetadata = new LikeMetadataDto();
        List likes = this.likeManager.getLikes((ContentEntityObject)comment);
        currentUserMetadata.liked(likes.stream().anyMatch(like -> like.getUsername().equals(AuthenticatedUserThreadLocal.getUsername())));
        likeMetadata.setCount(likes.size());
        return ContentMetadataDto.builder().currentUser(currentUserMetadata.build()).likes(likeMetadata).build();
    }

    public static boolean filter(Comment comment, Expansions expansions) {
        if (comment.getParent() != null) {
            return false;
        }
        if (MobileCommentConverter.isInlineComment(comment)) {
            return MobileCommentConverter.filterInlineComment(comment, expansions);
        }
        return expansions.canExpand("footer");
    }

    private static boolean filterInlineComment(Comment comment, Expansions expansions) {
        if (!expansions.canExpand("inline")) {
            return false;
        }
        Expansions sub = expansions.getSubExpansions("inline");
        return sub.isEmpty() || sub.canExpand(comment.getStatus().getValue().getStringValue());
    }

    private String getThumbnailHtml(Attachment attachment) {
        try {
            ThumbnailInfo thumbnailInfo = this.thumbnailManager.getThumbnailInfo(attachment);
            return thumbnailInfo.getThumbnailImageHtml(null);
        }
        catch (CannotGenerateThumbnailException e) {
            return attachment.getFileName();
        }
    }

    private Map<String, Object> buildInlineProperties(CommentStatus status, String selectionText) {
        String statusValue = Objects.isNull(status) || Objects.isNull(status.getValue()) ? CommentStatus.Value.OPEN.getStringValue() : status.getValue().getStringValue();
        return ImmutableMap.of((Object)"inline-original-selection", (Object)(selectionText == null ? "" : selectionText), (Object)COMMENT_STATUS, (Object)statusValue);
    }

    private static boolean isInlineComment(Comment comment) {
        return comment.isInlineComment() || comment.getContainer() instanceof Attachment;
    }
}


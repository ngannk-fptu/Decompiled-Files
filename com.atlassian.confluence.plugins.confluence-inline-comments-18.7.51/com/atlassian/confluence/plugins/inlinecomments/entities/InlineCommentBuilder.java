/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.ContentProperties
 *  com.atlassian.confluence.content.render.xhtml.FormatConverter
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.inlinecomments.entities;

import com.atlassian.confluence.content.ContentProperties;
import com.atlassian.confluence.content.render.xhtml.FormatConverter;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.plugins.inlinecomments.entities.ResolveProperties;
import com.atlassian.confluence.plugins.inlinecomments.entities.TopLevelInlineComment;
import com.atlassian.confluence.plugins.inlinecomments.helper.InlineCommentDateTimeHelper;
import com.atlassian.confluence.plugins.inlinecomments.helper.InlineCommentPermissionHelper;
import com.atlassian.confluence.plugins.inlinecomments.helper.InlineCommentUserHelper;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.renderer.RenderContext;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.List;

public class InlineCommentBuilder {
    private final UserAccessor userAccessor;
    private final InlineCommentPermissionHelper permissionHelper;
    private final WebResourceUrlProvider webResourceUrlProvider;
    private final FormatConverter formatConverter;
    private final InlineCommentDateTimeHelper dateTimeHelper;
    private final I18NBeanFactory i18NBeanFactory;
    private final InlineCommentUserHelper userHelper;

    public InlineCommentBuilder(UserAccessor userAccessor, InlineCommentPermissionHelper permissionHelper, WebResourceUrlProvider webResourceUrlProvider, FormatConverter formatConverter, InlineCommentDateTimeHelper dateTimeHelper, I18NBeanFactory i18NBeanFactory, InlineCommentUserHelper userHelper) {
        this.userAccessor = userAccessor;
        this.permissionHelper = permissionHelper;
        this.webResourceUrlProvider = webResourceUrlProvider;
        this.formatConverter = formatConverter;
        this.dateTimeHelper = dateTimeHelper;
        this.i18NBeanFactory = i18NBeanFactory;
        this.userHelper = userHelper;
    }

    public ResolveProperties buildResolveData(ContentProperties commentProperties) {
        ResolveProperties resolveProperties = new ResolveProperties();
        String status = commentProperties.getStringProperty("status");
        if (status != null) {
            resolveProperties.setResolved(status);
            resolveProperties.setResolvedByDangling(status);
            long resolveTime = commentProperties.getLongProperty("status-lastmoddate", 0L);
            resolveProperties.setResolvedTime(resolveTime);
            resolveProperties.setResolvedFriendlyDate(this.dateTimeHelper.formatFriendlyDate(resolveTime));
            String resolvedUserKey = commentProperties.getStringProperty("status-lastmodifier");
            resolveProperties.setResolvedUser(this.userHelper.getFullNameForUserKey(resolvedUserKey));
            return resolveProperties;
        }
        String resolvedProperty = commentProperties.getStringProperty("resolved");
        if (resolvedProperty != null) {
            resolveProperties.setResolved(Boolean.valueOf(resolvedProperty));
            long resolveTime = commentProperties.getLongProperty("resolved-time", 0L);
            resolveProperties.setResolvedTime(resolveTime);
            resolveProperties.setResolvedFriendlyDate(this.dateTimeHelper.formatFriendlyDate(resolveTime));
            String resolvedUserKey = commentProperties.getStringProperty("resolved-user");
            resolveProperties.setResolvedUser(this.userHelper.getFullNameForUserKey(resolvedUserKey));
            resolveProperties.setResolvedByDangling(Boolean.valueOf(commentProperties.getStringProperty("resolved-by-dangling")));
        }
        return resolveProperties;
    }

    public List<TopLevelInlineComment> build(List<Comment> comments) {
        ArrayList<TopLevelInlineComment> inlineComments = new ArrayList<TopLevelInlineComment>();
        if (comments.isEmpty()) {
            return inlineComments;
        }
        for (Comment comment : comments) {
            inlineComments.add(this.convertCommentToInlineComment(comment));
        }
        this.permissionHelper.setupPermission(inlineComments, comments.get(0));
        return inlineComments;
    }

    public TopLevelInlineComment build(Comment comment) {
        if (comment == null) {
            return null;
        }
        TopLevelInlineComment inlineComment = this.convertCommentToInlineComment(comment);
        this.permissionHelper.setupPermission(inlineComment, comment);
        return inlineComment;
    }

    private TopLevelInlineComment convertCommentToInlineComment(Comment comment) {
        ContentProperties properties = comment.getProperties();
        TopLevelInlineComment inlineComment = new TopLevelInlineComment();
        inlineComment.setMarkerRef(properties.getStringProperty("inline-marker-ref"));
        inlineComment.setOriginalSelection(properties.getStringProperty("inline-original-selection"));
        inlineComment.setBody(this.formatConverter.convertToViewFormat(comment.getBodyAsString(), (RenderContext)comment.toPageContext()));
        inlineComment.setId(comment.getId());
        inlineComment.setLastModificationDate(this.dateTimeHelper.formatFriendlyDate(comment.getLastModificationDate()));
        inlineComment.setCommentDateUrl(comment.getUrlPath());
        inlineComment.setResolveProperties(this.buildResolveData(properties));
        this.setupUserInformation(inlineComment, comment);
        return inlineComment;
    }

    private void setupUserInformation(TopLevelInlineComment inlineComment, Comment comment) {
        ConfluenceUser user = comment.getCreator();
        if (comment.getCreator() != null) {
            inlineComment.setAuthorDisplayName(user.getFullName());
            inlineComment.setAuthorUserName(user.getName());
            inlineComment.setAuthorAvatarUrl(this.webResourceUrlProvider.getStaticResourcePrefix(UrlMode.RELATIVE) + this.userAccessor.getUserProfilePicture((User)user).getDownloadPath());
        } else {
            inlineComment.setAuthorUserName("");
            inlineComment.setAuthorDisplayName(this.i18NBeanFactory.getI18NBean().getText("anonymous.name"));
            inlineComment.setAuthorAvatarUrl(this.webResourceUrlProvider.getStaticResourcePrefix(UrlMode.RELATIVE) + "/images/icons/profilepics/anonymous.png");
        }
    }
}


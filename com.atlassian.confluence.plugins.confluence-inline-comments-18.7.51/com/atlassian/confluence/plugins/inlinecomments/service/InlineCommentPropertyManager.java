/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.JsonString
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentSelector
 *  com.atlassian.confluence.api.model.content.JsonContentProperty
 *  com.atlassian.confluence.api.service.content.ContentPropertyService
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.content.ContentProperties
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.plugins.highlight.SelectionStorageFormatModifier
 *  com.atlassian.confluence.plugins.highlight.model.TextSearch
 *  com.atlassian.confluence.plugins.highlight.model.XMLModification
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.inlinecomments.service;

import com.atlassian.confluence.api.model.JsonString;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentSelector;
import com.atlassian.confluence.api.model.content.JsonContentProperty;
import com.atlassian.confluence.api.service.content.ContentPropertyService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.content.ContentProperties;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.plugins.highlight.SelectionStorageFormatModifier;
import com.atlassian.confluence.plugins.highlight.model.TextSearch;
import com.atlassian.confluence.plugins.highlight.model.XMLModification;
import com.atlassian.confluence.plugins.inlinecomments.models.InlineCreationProperties;
import com.atlassian.confluence.plugins.inlinecomments.service.InlineCommentMarkerHelper;
import com.atlassian.confluence.plugins.inlinecomments.utils.ResolveCommentConverter;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;

public class InlineCommentPropertyManager {
    private static final String SERIALIZED_HIGHLIGHTS_JSON_PROP = "inline-serialized-highlights";
    private static final int CONTENT_PROPERTY_STRING_LIMIT = 255;
    private final ContentEntityManager contentEntityManager;
    private final ContentPropertyService contentPropertyService;
    private final PermissionManager permissionManager;
    private final InlineCommentMarkerHelper inlineCommentMarkerHelper;
    private final SelectionStorageFormatModifier selectionStorageFormatModifier;

    public InlineCommentPropertyManager(@Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, ContentPropertyService contentPropertyService, PermissionManager permissionManager, InlineCommentMarkerHelper inlineCommentMarkerHelper, SelectionStorageFormatModifier selectionStorageFormatModifier) {
        this.contentEntityManager = contentEntityManager;
        this.contentPropertyService = contentPropertyService;
        this.permissionManager = permissionManager;
        this.inlineCommentMarkerHelper = inlineCommentMarkerHelper;
        this.selectionStorageFormatModifier = selectionStorageFormatModifier;
    }

    public boolean setProperties(long commentId, String markerRef, String originalSelection, String serializedHighlights) {
        ContentEntityObject entity = this.contentEntityManager.getById(commentId);
        if (entity != null) {
            entity.getProperties().setStringProperty("inline-marker-ref", markerRef);
            entity.getProperties().setStringProperty("inline-original-selection", this.truncate(originalSelection));
            JsonContentProperty serializedHighlightsJsonProperty = JsonContentProperty.builder().content(Content.buildReference((ContentSelector)ContentSelector.builder().id(entity.getContentId()).build())).key(SERIALIZED_HIGHLIGHTS_JSON_PROP).value(new JsonString(serializedHighlights)).build();
            this.permissionManager.withExemption(() -> this.contentPropertyService.create(serializedHighlightsJsonProperty));
            return true;
        }
        return false;
    }

    public void setResolveProperties(Comment comment, Boolean resolved, Date resolvedTime, ConfluenceUser user, Boolean isDangling) {
        ContentProperties commentProperties = comment.getProperties();
        commentProperties.setStringProperty("status", ResolveCommentConverter.getStatus(resolved, isDangling));
        commentProperties.setLongProperty("status-lastmoddate", resolvedTime.getTime());
        if (user != null) {
            commentProperties.setStringProperty("status-lastmodifier", user.getKey().getStringValue());
        } else {
            commentProperties.removeProperty("status-lastmodifier");
        }
        if (commentProperties.getStringProperty("resolved") != null) {
            commentProperties.removeProperty("resolved");
            commentProperties.removeProperty("resolved-user");
            commentProperties.removeProperty("resolved-time");
            commentProperties.removeProperty("resolved-by-dangling");
        }
    }

    private String truncate(String input) {
        return StringUtils.abbreviate((String)StringUtils.trim((String)input), (int)255);
    }

    public void setProperties(Comment comment, InlineCreationProperties properties) throws Exception {
        if (properties != null) {
            String markerRef = this.inlineCommentMarkerHelper.generateMarkerRef();
            boolean markSuccess = this.selectionStorageFormatModifier.markSelection(comment.getContainer().getId(), Long.parseLong(properties.getLastFetchTime().toString()), new TextSearch(properties.getOriginalSelection(), properties.getNumMatches().intValue(), properties.getMatchIndex().intValue()), new XMLModification(this.inlineCommentMarkerHelper.toStorageFormat(markerRef)));
            if (!markSuccess) {
                throw new BadRequestException("Can not create inline comment maker reference");
            }
            comment.getProperties().setStringProperty("inline-marker-ref", markerRef);
            comment.getProperties().setStringProperty("inline-original-selection", this.truncate(properties.getOriginalSelection()));
            JsonContentProperty serializedHighlightsJsonProperty = JsonContentProperty.builder().content(Content.buildReference((ContentSelector)ContentSelector.builder().id(comment.getContentId()).build())).key(SERIALIZED_HIGHLIGHTS_JSON_PROP).value(new JsonString(properties.getSerializedHighlights())).build();
            this.permissionManager.withExemption(() -> this.contentPropertyService.create(serializedHighlightsJsonProperty));
        }
        comment.setInlineComment(true);
    }
}


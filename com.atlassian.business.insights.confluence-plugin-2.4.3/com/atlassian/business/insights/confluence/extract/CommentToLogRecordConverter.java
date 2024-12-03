/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.business.insights.api.Entity
 *  com.atlassian.business.insights.api.LogRecord
 *  com.atlassian.business.insights.api.extract.EntityToLogRecordConverter
 *  com.atlassian.business.insights.core.util.TextConverter
 *  com.atlassian.confluence.core.ConfluenceEntityObject
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 */
package com.atlassian.business.insights.confluence.extract;

import com.atlassian.business.insights.api.Entity;
import com.atlassian.business.insights.api.LogRecord;
import com.atlassian.business.insights.api.extract.EntityToLogRecordConverter;
import com.atlassian.business.insights.confluence.attribute.CommentAttributes;
import com.atlassian.business.insights.confluence.extract.ConverterHelper;
import com.atlassian.business.insights.core.util.TextConverter;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import java.util.HashMap;
import java.util.Optional;

public class CommentToLogRecordConverter
implements EntityToLogRecordConverter<Long, Comment> {
    private final ApplicationProperties applicationProperties;
    private final ConverterHelper helper;

    public CommentToLogRecordConverter(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
        this.helper = new ConverterHelper(applicationProperties);
    }

    public LogRecord convert(Entity<Long, Comment> entity) {
        Comment comment = (Comment)entity.getValue();
        HashMap<String, Object> payload = new HashMap<String, Object>();
        this.helper.populateCommonAttributes((ConfluenceEntityObject)comment, payload);
        payload.put(CommentAttributes.ID_ATTR.getInternalName(), entity.getId());
        String unescapedContent = ConverterHelper.unescapeXhtml(comment.getBodyContent().getBody());
        payload.put(CommentAttributes.CONTENT_ATTR.getInternalName(), TextConverter.truncateText((String)unescapedContent));
        payload.put(CommentAttributes.PARENT_PAGE_ID_ATTR.getInternalName(), comment.getContainer() != null ? Long.valueOf(comment.getContainer().getId()) : "");
        payload.put(CommentAttributes.PARENT_COMMENT_ID_ATTR.getInternalName(), comment.getParent() != null ? Long.valueOf(comment.getParent().getId()) : "");
        payload.put(CommentAttributes.COMMENT_URL.getInternalName(), comment.getContainer() != null && comment.getUrlPath() != null ? this.applicationProperties.getBaseUrl(UrlMode.CANONICAL) + comment.getUrlPath() : "");
        payload.put(CommentAttributes.SPACE_KEY_ATTR.getInternalName(), Optional.ofNullable(((Comment)entity.getValue()).getSpace()).map(Space::getKey).orElse(null));
        return LogRecord.getInstance((Object)entity.getId(), (long)entity.getTimestamp(), payload);
    }
}


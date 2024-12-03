/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.business.insights.api.Entity
 *  com.atlassian.business.insights.api.LogRecord
 *  com.atlassian.business.insights.api.extract.EntityToLogRecordConverter
 *  com.atlassian.business.insights.core.util.TextConverter
 *  com.atlassian.confluence.core.ConfluenceEntityObject
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.confluence.extract;

import com.atlassian.business.insights.api.Entity;
import com.atlassian.business.insights.api.LogRecord;
import com.atlassian.business.insights.api.extract.EntityToLogRecordConverter;
import com.atlassian.business.insights.confluence.attribute.PageAttributes;
import com.atlassian.business.insights.confluence.extract.ConverterHelper;
import com.atlassian.business.insights.core.util.TextConverter;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import java.util.HashMap;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class PageToLogRecordConverter
implements EntityToLogRecordConverter<Long, AbstractPage> {
    private final ApplicationProperties applicationProperties;
    private final ConverterHelper helper;

    public PageToLogRecordConverter(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
        this.helper = new ConverterHelper(applicationProperties);
    }

    public LogRecord convert(@Nonnull Entity<Long, AbstractPage> entity) {
        AbstractPage pageOrBlogpost = (AbstractPage)entity.getValue();
        HashMap<String, Object> payload = new HashMap<String, Object>();
        this.helper.populateCommonAttributes((ConfluenceEntityObject)pageOrBlogpost, payload);
        payload.put(PageAttributes.PAGE_ID_ATTR.getInternalName(), entity.getId());
        payload.put(PageAttributes.LABELS_ATTR.getInternalName(), pageOrBlogpost.getLabels().stream().map(Label::getName).collect(Collectors.toList()));
        payload.put(PageAttributes.SPACE_KEY_ATTR.getInternalName(), pageOrBlogpost.getSpaceKey());
        payload.put(PageAttributes.PAGE_VERSION_ATTR.getInternalName(), pageOrBlogpost.getVersion());
        payload.put(PageAttributes.PAGE_TYPE_ATTR.getInternalName(), pageOrBlogpost.getType());
        payload.put(PageAttributes.PAGE_STATUS_ATTR.getInternalName(), pageOrBlogpost.getContentStatus());
        payload.put(PageAttributes.PAGE_TITLE_ATTR.getInternalName(), pageOrBlogpost.getTitle());
        payload.put(PageAttributes.PAGE_PARENT_ID_ATTR.getInternalName(), entity.getId());
        String unescapedContent = ConverterHelper.unescapeXhtml(pageOrBlogpost.getBodyContent().getBody());
        payload.put(PageAttributes.PAGE_CONTENT_ATTR.getInternalName(), TextConverter.truncateLongText((String)unescapedContent));
        payload.put(PageAttributes.PAGE_URL_ATTR.getInternalName(), this.applicationProperties.getBaseUrl(UrlMode.CANONICAL) + pageOrBlogpost.getUrlPath());
        payload.put(PageAttributes.LAST_UPDATE_DESCRIPTION_ATTR.getInternalName(), TextConverter.truncateText((String)pageOrBlogpost.getVersionComment()));
        if (pageOrBlogpost instanceof Page) {
            Page page = (Page)pageOrBlogpost;
            payload.put(PageAttributes.PAGE_PARENT_ID_ATTR.getInternalName(), page.getParent() != null ? Long.valueOf(page.getParent().getId()) : "");
        }
        if (pageOrBlogpost instanceof BlogPost) {
            payload.put(PageAttributes.PAGE_PARENT_ID_ATTR.getInternalName(), "");
        }
        return LogRecord.getInstance((Object)entity.getId(), (long)entity.getTimestamp(), payload);
    }
}


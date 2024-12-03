/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Container
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.locator.ContentLocator
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.service.content.ContentService$ContentFinder
 *  com.atlassian.confluence.api.service.content.ContentService$ParameterContentFinder
 *  com.atlassian.confluence.api.service.content.ContentService$SingleContentFetcher
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 */
package com.atlassian.confluence.impl.service.finder.content;

import com.atlassian.confluence.api.model.content.Container;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.locator.ContentLocator;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.impl.service.finder.NoopFetcher;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;

public class NoopContentFinder
extends NoopFetcher<Content>
implements ContentService.ContentFinder {
    public ContentService.SingleContentFetcher withId(ContentId contentId) {
        return this;
    }

    public ContentService.SingleContentFetcher withIdAndVersion(ContentId contentId, int version) {
        return this;
    }

    public ContentService.SingleContentFetcher withLocator(ContentLocator locator) {
        return this;
    }

    public ContentService.ParameterContentFinder withSpace(Space ... space) {
        return this;
    }

    public ContentService.ParameterContentFinder withType(ContentType ... type) {
        return this;
    }

    public ContentService.ParameterContentFinder withCreatedDate(LocalDate time) {
        return this;
    }

    public ContentService.ParameterContentFinder withTitle(String title) {
        return this;
    }

    public ContentService.ParameterContentFinder withContainer(Container container) {
        return this;
    }

    public ContentService.ParameterContentFinder withId(ContentId first, ContentId ... tail) {
        return this;
    }

    public ContentService.ParameterContentFinder withId(Iterable<ContentId> contentIds) {
        return this;
    }

    public ContentService.ContentFinder withStatus(ContentStatus ... status) {
        return this;
    }

    public ContentService.ContentFinder withStatus(Iterable<ContentStatus> statuses) {
        return this;
    }

    public ContentService.ContentFinder withAnyStatus() {
        return this;
    }

    public PageResponse<Content> fetchMany(ContentType type, PageRequest request) throws ServiceException {
        return PageResponseImpl.empty((boolean)false, (PageRequest)request);
    }

    public Map<ContentType, PageResponse<Content>> fetchMappedByContentType(PageRequest request) throws ServiceException {
        return Collections.emptyMap();
    }
}


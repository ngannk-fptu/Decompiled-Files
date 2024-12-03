/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.Label
 *  com.atlassian.confluence.api.model.content.Version
 *  com.atlassian.confluence.api.model.link.Link
 *  com.atlassian.confluence.api.model.link.LinkType
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.service.search.CQLSearchService
 *  com.atlassian.confluence.rest.api.model.RestList
 *  com.google.common.annotations.VisibleForTesting
 *  org.apache.commons.lang3.StringUtils
 *  org.joda.time.DateTime
 */
package com.atlassian.confluence.plugins.conluenceview.services.impl;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.Label;
import com.atlassian.confluence.api.model.content.Version;
import com.atlassian.confluence.api.model.link.Link;
import com.atlassian.confluence.api.model.link.LinkType;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.service.search.CQLSearchService;
import com.atlassian.confluence.plugins.conluenceview.query.ConfluencePagesQuery;
import com.atlassian.confluence.plugins.conluenceview.rest.dto.ConfluencePageDto;
import com.atlassian.confluence.plugins.conluenceview.rest.dto.ConfluencePagesDto;
import com.atlassian.confluence.plugins.conluenceview.rest.exception.CacheTokenNotFoundException;
import com.atlassian.confluence.plugins.conluenceview.rest.exception.InvalidRequestException;
import com.atlassian.confluence.plugins.conluenceview.services.ConfluencePagesService;
import com.atlassian.confluence.rest.api.model.RestList;
import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

public class DefaultConfluencePagesService
implements ConfluencePagesService {
    private static final String PAGES_SEARCH_BY_ID_CQL = "id in (%s) and type = page order by lastModified desc";
    private static final String PAGES_SEARCH_BY_TEXT_CQL = "text ~ \"%s\"";
    private final CQLSearchService searchService;
    private Map<String, String> requestCache;

    public DefaultConfluencePagesService(CQLSearchService searchService) {
        this.searchService = searchService;
        this.requestCache = new HashMap<String, String>();
    }

    @VisibleForTesting
    public void setRequestCache(Map<String, String> requestCache) {
        this.requestCache = requestCache;
    }

    @Override
    public ConfluencePagesDto getPagesInSpace(ConfluencePagesQuery query) {
        String cql = "type = page and space = '" + query.getSpaceKey() + "' order by lastModified desc";
        return this.getPages(cql, query.getStart(), query.getLimit());
    }

    @Override
    public ConfluencePagesDto getPagesByIds(ConfluencePagesQuery query) {
        this.validate(query);
        Object cql = this.buildCql(query.getCacheToken(), query.getPageIds());
        if (StringUtils.isNotBlank((CharSequence)query.getSearchString())) {
            cql = String.format(PAGES_SEARCH_BY_TEXT_CQL, query.getSearchString().trim()) + " and " + (String)cql;
        }
        return this.getPages((String)cql, query.getStart(), query.getLimit());
    }

    private ConfluencePagesDto getPages(String cql, int start, int limit) {
        SimplePageRequest request = new SimplePageRequest(start, limit);
        PageResponse contents = this.searchService.searchContent(cql, (PageRequest)request, new Expansion[]{new Expansion("history", new Expansions(new Expansion[0]).prepend("lastUpdated")), new Expansion("metadata", new Expansions(new Expansion[0]).prepend("labels"))});
        ArrayList<ConfluencePageDto> pages = new ArrayList<ConfluencePageDto>();
        for (Content content : contents) {
            DateTime lastUpdatedAt;
            Person lastUpdatedBy;
            ConfluencePageDto.Builder builder = ConfluencePageDto.newBuilder();
            Version lastUpdatedVersion = (Version)content.getHistory().getLastUpdatedRef().get();
            if (lastUpdatedVersion.getNumber() == 1) {
                builder.withAuthor(content.getHistory().getCreatedBy().getDisplayName());
            }
            if ((lastUpdatedBy = lastUpdatedVersion.getBy()) != null) {
                builder.withLastModifier(lastUpdatedBy.getDisplayName());
            }
            if ((lastUpdatedAt = lastUpdatedVersion.getWhen()) != null) {
                builder.withLastModified(lastUpdatedAt.toDate());
            }
            builder.withPageId(content.getId().asLong());
            builder.withPageTitle(content.getTitle());
            builder.withPageUrl(((Link)content.getLinks().get(LinkType.WEB_UI)).getPath());
            Map metadata = content.getMetadata();
            ArrayList<String> labelList = new ArrayList<String>();
            if (metadata != null && metadata.get("labels") != null) {
                List labels = ((RestList)metadata.get("labels")).getResults();
                labelList.addAll(labels.stream().map(Label::getLabel).collect(Collectors.toList()));
            }
            builder.withLabels(labelList);
            pages.add(builder.build());
        }
        return ConfluencePagesDto.newBuilder().withPages(pages).build();
    }

    private String buildCql(String token, List<Long> pageIds) {
        String cql = this.requestCache.get(token);
        if (pageIds == null || pageIds.isEmpty()) {
            if (StringUtils.isBlank((CharSequence)cql)) {
                throw new CacheTokenNotFoundException();
            }
        } else {
            String pageIdsStr = StringUtils.join(pageIds, (String)",");
            cql = String.format(PAGES_SEARCH_BY_ID_CQL, pageIdsStr);
            this.requestCache.put(token, cql);
        }
        return cql;
    }

    private void validate(ConfluencePagesQuery query) {
        if (StringUtils.isBlank((CharSequence)query.getCacheToken())) {
            throw new InvalidRequestException("Request cache token cannot be empty");
        }
    }
}


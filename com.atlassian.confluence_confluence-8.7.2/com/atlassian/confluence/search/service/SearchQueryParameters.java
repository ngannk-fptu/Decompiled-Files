/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.confluence.search.service;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.search.plugin.ContentTypeSearchDescriptor;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.service.SpaceCategoryEnum;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.query.DateRangeQuery;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.sal.api.user.UserKey;
import java.util.Collections;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class SearchQueryParameters {
    public static final String PREFERRED_SPACE_KEY = "preferredSpaceKey";
    private String query;
    private SpaceCategoryEnum category;
    private Set<String> spaceKeys;
    private Set<ContentTypeEnum> contentTypes;
    private Set<Attachment.Type> attachmentTypes;
    private Set<String> labels;
    private DateRangeQuery.DateRange lastModified;
    private ConfluenceUser contributor;
    private SearchSort sort;
    private SearchQuery searchQueryFilter;
    private Set<ContentTypeSearchDescriptor> pluginContentTypes;
    private boolean includeArchivedSpaces;
    private boolean onlyArchivedSpaces;
    private Set<String> extraFields;
    private static final ConfluenceUser dummyContributor = new ConfluenceUser(){

        @Override
        public UserKey getKey() {
            return new UserKey("");
        }

        @Override
        public String getLowerName() {
            return null;
        }

        public String getFullName() {
            return null;
        }

        public String getEmail() {
            return null;
        }

        public String getName() {
            return null;
        }
    };

    public SearchQueryParameters() {
    }

    public SearchQueryParameters(String query) {
        this.query = query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setCategory(SpaceCategoryEnum category) {
        if (category != null) {
            this.category = category;
        }
    }

    public void setSpaceKey(String spaceKey) {
        if (StringUtils.isNotBlank((CharSequence)spaceKey)) {
            this.spaceKeys = Collections.singleton(spaceKey);
        }
    }

    public void setSpaceKeys(Set<String> spaceKeys) {
        this.spaceKeys = spaceKeys;
    }

    public void setAttachmentTypes(Set<Attachment.Type> attachmentTypes) {
        this.attachmentTypes = attachmentTypes;
    }

    public void setContentType(ContentTypeEnum contentType) {
        this.setContentTypes(Collections.singleton(contentType));
    }

    public void setContentTypes(Set<ContentTypeEnum> contentTypes) {
        this.contentTypes = contentTypes;
    }

    public void setLastModified(DateRangeQuery.DateRange lastModified) {
        this.lastModified = lastModified;
    }

    public void setContributor(ConfluenceUser contributor) {
        this.contributor = contributor != null ? contributor : dummyContributor;
    }

    public String getQuery() {
        return this.query;
    }

    public SpaceCategoryEnum getCategory() {
        return this.category;
    }

    public Set<String> getSpaceKeys() {
        return this.spaceKeys;
    }

    public Set<Attachment.Type> getAttachmentTypes() {
        return this.attachmentTypes;
    }

    public Set<ContentTypeEnum> getContentTypes() {
        return this.contentTypes;
    }

    public DateRangeQuery.DateRange getLastModified() {
        return this.lastModified;
    }

    public ConfluenceUser getContributor() {
        return this.contributor;
    }

    public Set<String> getLabels() {
        return this.labels;
    }

    public void setLabels(Set<String> labels) {
        this.labels = labels;
    }

    public SearchSort getSort() {
        return this.sort;
    }

    public SearchQuery getSearchQueryFilter() {
        return this.searchQueryFilter;
    }

    public void setSort(SearchSort sort) {
        this.sort = sort;
    }

    public void setPluginContentTypes(Set<ContentTypeSearchDescriptor> pluginContentTypes) {
        this.pluginContentTypes = pluginContentTypes;
    }

    public Set<ContentTypeSearchDescriptor> getPluginContentTypes() {
        return this.pluginContentTypes;
    }

    public boolean isIncludeArchivedSpaces() {
        return this.includeArchivedSpaces;
    }

    public void setIncludeArchivedSpaces(boolean includeArchivedSpaces) {
        if (!includeArchivedSpaces) {
            this.setOnlyArchivedSpaces(false);
        }
        this.includeArchivedSpaces = includeArchivedSpaces;
    }

    public void setOnlyArchivedSpaces(boolean onlyArchivedSpaces) {
        if (onlyArchivedSpaces) {
            this.setIncludeArchivedSpaces(true);
        }
        this.onlyArchivedSpaces = onlyArchivedSpaces;
    }

    public boolean isOnlyArchivedSpaces() {
        return this.onlyArchivedSpaces;
    }

    public Set<String> getExtraFields() {
        return this.extraFields;
    }

    public void setExtraFields(Set<String> extraFields) {
        this.extraFields = extraFields;
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("query", (Object)this.query).append("category", (Object)this.category).append("spaceKeys", this.spaceKeys).append("contentTypes", this.contentTypes).append("attachmentTypes", this.attachmentTypes).append("lastModified", (Object)this.lastModified).append("contributor", (Object)this.contributor).append("labels", this.labels).append("sort", (Object)this.sort).append("extraFields", this.extraFields).toString();
    }

    public void setSearchQueryFilter(SearchQuery searchQueryFilter) {
        this.searchQueryFilter = searchQueryFilter;
    }
}


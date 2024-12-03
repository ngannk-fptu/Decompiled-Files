/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.search.plugin.ContentTypeSearchDescriptor
 *  com.atlassian.confluence.search.plugin.SiteSearchPluginModule
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.search.service.DateRangeEnum
 *  com.atlassian.confluence.search.service.SpaceCategoryEnum
 *  com.atlassian.confluence.search.v2.BooleanOperator
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.query.ContentTypeQuery
 *  com.atlassian.confluence.search.v2.query.ContributorQuery
 *  com.atlassian.confluence.search.v2.query.DateRangeQuery
 *  com.atlassian.confluence.search.v2.query.DateRangeQuery$DateRangeQueryType
 *  com.atlassian.confluence.search.v2.query.InSpaceQuery
 *  com.atlassian.confluence.search.v2.query.LabelQuery
 *  com.atlassian.confluence.search.v2.query.SpaceCategoryQuery
 *  com.atlassian.confluence.search.v2.query.TextFieldQuery
 *  com.atlassian.confluence.user.persistence.dao.ConfluenceUserDao
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.spring.container.ContainerManager
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableMap
 *  org.joda.time.DateTime
 *  org.joda.time.Interval
 *  org.joda.time.ReadableInstant
 */
package com.atlassian.confluence.plugins.search.api.model;

import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.search.plugin.ContentTypeSearchDescriptor;
import com.atlassian.confluence.search.plugin.SiteSearchPluginModule;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.service.DateRangeEnum;
import com.atlassian.confluence.search.service.SpaceCategoryEnum;
import com.atlassian.confluence.search.v2.BooleanOperator;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.ContentTypeQuery;
import com.atlassian.confluence.search.v2.query.ContributorQuery;
import com.atlassian.confluence.search.v2.query.DateRangeQuery;
import com.atlassian.confluence.search.v2.query.InSpaceQuery;
import com.atlassian.confluence.search.v2.query.LabelQuery;
import com.atlassian.confluence.search.v2.query.SpaceCategoryQuery;
import com.atlassian.confluence.search.v2.query.TextFieldQuery;
import com.atlassian.confluence.user.persistence.dao.ConfluenceUserDao;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.spring.container.ContainerManager;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.ReadableInstant;

public class SearchQueryParameters {
    public static final int MAX_QUERY_LENGTH = 255;
    private final String query;
    private final int startIndex;
    private final int pageSize;
    private final SpaceCategoryEnum spaceCategory;
    private final String spaceKey;
    private final Interval interval;
    private final ContentTypeEnum contentType;
    private final ContentTypeSearchDescriptor pluginContentType;
    private final DateRangeEnum lastModified;
    private final String contributor;
    private final Set<String> labels;
    private final boolean highlight;
    private final boolean includeArchivedSpaces;

    private SearchQueryParameters(Builder builder) {
        this.query = builder.query;
        this.startIndex = builder.startIndex;
        this.pageSize = builder.pageSize;
        this.spaceCategory = builder.spaceCategory;
        this.spaceKey = builder.spaceKey;
        this.interval = builder.interval;
        this.contentType = builder.contentType;
        this.labels = builder.labels == null ? Collections.emptySet() : builder.labels;
        this.pluginContentType = builder.pluginContentType;
        this.lastModified = builder.lastModified;
        this.contributor = builder.contributor;
        this.includeArchivedSpaces = builder.includeArchivedSpaces;
        this.highlight = builder.highlight;
    }

    public String getQuery() {
        return this.query;
    }

    public int getStartIndex() {
        return this.startIndex;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public SpaceCategoryEnum getSpaceCategory() {
        return this.spaceCategory;
    }

    public Interval getInterval() {
        return this.interval;
    }

    public ContentTypeEnum getContentType() {
        return this.contentType;
    }

    public ContentTypeSearchDescriptor getPluginContentType() {
        return this.pluginContentType;
    }

    public DateRangeEnum getLastModified() {
        return this.lastModified;
    }

    public String getContributor() {
        return this.contributor;
    }

    public boolean isIncludeArchivedSpaces() {
        return this.includeArchivedSpaces;
    }

    public Set<String> getLabels() {
        return this.labels;
    }

    public boolean isHighlight() {
        return this.highlight;
    }

    public SearchQuery toSearchV2Query(Map<String, String> extraParams) {
        return extraParams != null ? new SearchV2Query(extraParams) : new SearchV2Query(Collections.emptyMap());
    }

    public static Builder newSearchQueryParameters(String query) {
        return new Builder(query);
    }

    public static Builder newSearchQueryParameters(SearchQueryParameters searchQueryParameters) {
        Builder builder = new Builder(searchQueryParameters.getQuery());
        builder.pageSize(searchQueryParameters.getPageSize()).startIndex(searchQueryParameters.getStartIndex()).lastModified(searchQueryParameters.getLastModified()).contentType(searchQueryParameters.getContentType()).pluginContentType(searchQueryParameters.getPluginContentType()).spaceCategory(searchQueryParameters.getSpaceCategory()).spaceKey(searchQueryParameters.getSpaceKey()).contributor(searchQueryParameters.getContributor()).includeArchivedSpaces(searchQueryParameters.isIncludeArchivedSpaces());
        return builder;
    }

    public static class Builder {
        private final String query;
        private int pageSize = 10;
        private int startIndex = 0;
        private String spaceKey = null;
        private Interval interval = null;
        private ContentTypeEnum contentType = null;
        private Set<String> labels = null;
        private ContentTypeSearchDescriptor pluginContentType = null;
        private DateRangeEnum lastModified;
        private String contributor = null;
        private boolean includeArchivedSpaces = false;
        private boolean highlight = true;
        public SpaceCategoryEnum spaceCategory;

        public Builder(String query) {
            if (query != null && query.length() > 255) {
                query = query.substring(0, 255);
            }
            this.query = query;
        }

        public Builder pageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public Builder startIndex(int startIndex) {
            this.startIndex = startIndex;
            return this;
        }

        public Builder where(String spaceKeyOrCategory) {
            SpaceCategoryEnum categoryEnum = SpaceCategoryEnum.get((String)spaceKeyOrCategory);
            if (categoryEnum != null) {
                this.spaceCategory = categoryEnum;
            } else {
                this.spaceKey = spaceKeyOrCategory;
            }
            return this;
        }

        private Builder spaceCategory(SpaceCategoryEnum spaceCategory) {
            this.spaceCategory = spaceCategory;
            return this;
        }

        private Builder spaceKey(String spaceKey) {
            this.spaceKey = spaceKey;
            return this;
        }

        public Builder fromDate(DateTime fromDate) {
            DateTime toDate = this.interval == null ? new DateTime() : this.interval.getEnd();
            this.interval = new Interval((ReadableInstant)fromDate, (ReadableInstant)toDate);
            return this;
        }

        public Builder toDate(DateTime toDate) {
            DateTime fromDate = this.interval == null ? new DateTime() : this.interval.getEnd();
            this.interval = new Interval((ReadableInstant)fromDate, (ReadableInstant)toDate);
            return this;
        }

        public Builder lastModified(DateRangeEnum lastModified) {
            this.lastModified = lastModified;
            return this;
        }

        public Builder contentType(ContentTypeEnum contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder labels(Set<String> labels) {
            this.labels = labels;
            return this;
        }

        public Builder pluginContentType(ContentTypeSearchDescriptor pluginContentType) {
            this.pluginContentType = pluginContentType;
            return this;
        }

        public Builder pluggableContentType(PluginAccessor pluginAccessor, String representation) {
            ContentTypeEnum contentType = ContentTypeEnum.getByRepresentation((String)representation);
            if (contentType == null) {
                ContentTypeSearchDescriptor descriptor = this.findSearchTypeDescriptorByRepresentation(pluginAccessor, representation);
                if (descriptor != null) {
                    this.pluginContentType = descriptor;
                }
            } else {
                this.contentType = contentType;
            }
            return this;
        }

        public Builder contributor(String contributor) {
            this.contributor = contributor;
            return this;
        }

        public Builder includeArchivedSpaces(boolean includeArchivedSpaces) {
            this.includeArchivedSpaces = includeArchivedSpaces;
            return this;
        }

        public Builder highlight(boolean highlight) {
            this.highlight = highlight;
            return this;
        }

        public SearchQueryParameters build() {
            return new SearchQueryParameters(this);
        }

        private ContentTypeSearchDescriptor findSearchTypeDescriptorByRepresentation(PluginAccessor pluginAccessor, String representation) {
            List modules = pluginAccessor.getEnabledModulesByClass(SiteSearchPluginModule.class);
            return modules.stream().flatMap(module -> module.getContentTypeDescriptors().stream()).filter(descriptor -> descriptor.getIdentifier().equals(representation)).findFirst().orElse(null);
        }
    }

    private class SearchV2Query
    implements SearchQuery {
        private final List<SearchQuery> queries = new LinkedList<SearchQuery>();

        SearchV2Query(Map<String, String> extraParams) {
            String queryString = !Strings.isNullOrEmpty((String)SearchQueryParameters.this.query) ? SearchQueryParameters.this.query : "*";
            this.queries.add((SearchQuery)new TextFieldQuery("title", queryString, BooleanOperator.OR));
            this.queries.add((SearchQuery)new TextFieldQuery("body", queryString, BooleanOperator.OR));
            if (SearchQueryParameters.this.spaceKey != null) {
                this.queries.add((SearchQuery)new InSpaceQuery(SearchQueryParameters.this.spaceKey));
            } else {
                this.queries.add((SearchQuery)new SpaceCategoryQuery(SearchQueryParameters.this.spaceCategory != null ? SearchQueryParameters.this.spaceCategory : SpaceCategoryEnum.ALL, (LabelManager)ContainerManager.getComponent((String)"labelManager", LabelManager.class)));
            }
            if (SearchQueryParameters.this.contentType != null) {
                this.queries.add((SearchQuery)new ContentTypeQuery(SearchQueryParameters.this.contentType));
            }
            if (SearchQueryParameters.this.labels != null) {
                SearchQueryParameters.this.labels.forEach(label -> this.queries.add((SearchQuery)new LabelQuery(label)));
            }
            if (SearchQueryParameters.this.pluginContentType != null) {
                this.queries.add(SearchQueryParameters.this.pluginContentType.getQuery());
            }
            if (SearchQueryParameters.this.lastModified != null) {
                this.queries.add((SearchQuery)new DateRangeQuery(SearchQueryParameters.this.lastModified.dateRange(), DateRangeQuery.DateRangeQueryType.MODIFIED));
            }
            if (SearchQueryParameters.this.contributor != null) {
                this.queries.add((SearchQuery)new ContributorQuery(SearchQueryParameters.this.contributor, (ConfluenceUserDao)ContainerManager.getComponent((String)"confluenceUserDao", ConfluenceUserDao.class)));
            }
            ImmutableMap.builder().put((Object)"startIndex", (Object)Integer.toString(SearchQueryParameters.this.startIndex)).put((Object)"pageSize", (Object)Integer.toString(SearchQueryParameters.this.pageSize)).putAll(extraParams).build().forEach((key, value) -> this.queries.add(new SearchQuery((String)key, (String)value){
                final /* synthetic */ String val$key;
                final /* synthetic */ String val$value;
                {
                    this.val$key = string;
                    this.val$value = string2;
                }

                public String getKey() {
                    return this.val$key;
                }

                public List getParameters() {
                    return Collections.singletonList(this.val$value);
                }
            }));
        }

        public String getKey() {
            return "searchv3";
        }

        public List getParameters() {
            return this.queries;
        }
    }
}


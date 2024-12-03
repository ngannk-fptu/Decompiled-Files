/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.search.v2.BooleanOperator
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.ContentTypeQuery
 *  com.atlassian.confluence.search.v2.query.ContributorQuery
 *  com.atlassian.confluence.search.v2.query.CreatorQuery
 *  com.atlassian.confluence.search.v2.query.DateRangeQuery
 *  com.atlassian.confluence.search.v2.query.DateRangeQuery$DateRangeQueryType
 *  com.atlassian.confluence.search.v2.query.InSpaceQuery
 *  com.atlassian.confluence.search.v2.query.MultiTextFieldQuery
 *  com.atlassian.confluence.user.persistence.dao.ConfluenceUserDao
 *  com.atlassian.streams.api.ActivityObjectType
 *  com.atlassian.streams.api.ActivityObjectTypes
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.api.common.Options
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.streams.confluence;

import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.BooleanOperator;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ContentTypeQuery;
import com.atlassian.confluence.search.v2.query.ContributorQuery;
import com.atlassian.confluence.search.v2.query.CreatorQuery;
import com.atlassian.confluence.search.v2.query.DateRangeQuery;
import com.atlassian.confluence.search.v2.query.InSpaceQuery;
import com.atlassian.confluence.search.v2.query.MultiTextFieldQuery;
import com.atlassian.confluence.user.persistence.dao.ConfluenceUserDao;
import com.atlassian.streams.api.ActivityObjectType;
import com.atlassian.streams.api.ActivityObjectTypes;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.api.common.Options;
import com.atlassian.streams.confluence.ConfluenceActivityObjectTypes;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class ConfluenceSearchQueryBuilder {
    static final List<ContentTypeEnum> CONTENT_TYPES = ImmutableList.of((Object)ContentTypeEnum.PAGE, (Object)ContentTypeEnum.BLOG, (Object)ContentTypeEnum.COMMENT, (Object)ContentTypeEnum.ATTACHMENT, (Object)ContentTypeEnum.SPACE_DESCRIPTION, (Object)ContentTypeEnum.PERSONAL_SPACE_DESCRIPTION);
    static final Set<String> TEXT_FIELDS = ImmutableSet.of((Object)"title", (Object)"labelText", (Object)"contentBody", (Object)"filename", (Object)"username", (Object)"fullName", (Object[])new String[]{"email", "from", "recipients"});
    private final ConfluenceUserDao confluenceUserDao;
    private final Set<String> createdByUsers = Sets.newLinkedHashSet();
    private final Set<String> lastModifiedByUsers = Sets.newLinkedHashSet();
    private final Set<String> spaceKeys = Sets.newLinkedHashSet();
    private final Set<String> searchTerms = Sets.newLinkedHashSet();
    private final Set<String> excludedSearchTerms = Sets.newLinkedHashSet();
    private final Set<ActivityObjectType> activityObjects = Sets.newLinkedHashSet();
    private final Set<SearchQuery> filters = Sets.newLinkedHashSet();
    private Option<Date> minDate = Option.none();
    private Option<Date> maxDate = Option.none();
    private final Function<String, Option<ContributorQuery>> toLastModifierQuery = new Function<String, Option<ContributorQuery>>(){

        public Option<ContributorQuery> apply(String username) {
            if (StringUtils.isBlank((CharSequence)username)) {
                return Option.none();
            }
            return Option.some((Object)new ContributorQuery(username, ConfluenceSearchQueryBuilder.this.confluenceUserDao));
        }
    };

    ConfluenceSearchQueryBuilder(ConfluenceUserDao confluenceUserDao) {
        this.confluenceUserDao = Objects.requireNonNull(confluenceUserDao);
    }

    public ConfluenceSearchQueryBuilder createdOrLastModifiedBy(Iterable<String> userNames) {
        return this.createdBy(userNames).lastModifiedBy(userNames);
    }

    public ConfluenceSearchQueryBuilder createdOrLastModifiedBy(String ... userNames) {
        return this.createdOrLastModifiedBy(Arrays.asList(userNames));
    }

    public ConfluenceSearchQueryBuilder createdBy(Iterable<String> userNames) {
        Iterables.addAll(this.createdByUsers, (Iterable)Options.catOptions((Iterable)Iterables.transform(userNames, (Function)StringTrimmer.INSTANCE)));
        return this;
    }

    public ConfluenceSearchQueryBuilder createdBy(String ... userNames) {
        return this.createdBy(Arrays.asList(userNames));
    }

    public ConfluenceSearchQueryBuilder lastModifiedBy(Iterable<String> userNames) {
        Iterables.addAll(this.lastModifiedByUsers, (Iterable)Options.catOptions((Iterable)Iterables.transform(userNames, (Function)StringTrimmer.INSTANCE)));
        return this;
    }

    public ConfluenceSearchQueryBuilder lastModifiedBy(String ... userNames) {
        return this.lastModifiedBy(Arrays.asList(userNames));
    }

    public ConfluenceSearchQueryBuilder inSpace(Iterable<String> spaceKeys) {
        Iterables.addAll(this.spaceKeys, (Iterable)Options.catOptions((Iterable)Iterables.transform(spaceKeys, (Function)StringTrimmer.INSTANCE)));
        return this;
    }

    public ConfluenceSearchQueryBuilder inSpace(String ... spaceKeys) {
        return this.inSpace(Arrays.asList(spaceKeys));
    }

    public ConfluenceSearchQueryBuilder searchFor(Iterable<String> searchTerms) {
        Iterables.addAll(this.searchTerms, (Iterable)Options.catOptions((Iterable)Iterables.transform(searchTerms, (Function)StringTrimmer.INSTANCE)));
        return this;
    }

    public ConfluenceSearchQueryBuilder searchFor(String ... searchTerms) {
        return this.searchFor(Arrays.asList(searchTerms));
    }

    public ConfluenceSearchQueryBuilder excludeTerms(Iterable<String> excludedSearchTerms) {
        Iterables.addAll(this.excludedSearchTerms, (Iterable)Options.catOptions((Iterable)Iterables.transform(excludedSearchTerms, (Function)StringTrimmer.INSTANCE)));
        return this;
    }

    public ConfluenceSearchQueryBuilder addFilters(SearchQuery ... filters) {
        Iterables.addAll(this.filters, Arrays.asList(filters));
        return this;
    }

    public ConfluenceSearchQueryBuilder minDate(Option<Date> minDate) {
        this.minDate = minDate;
        return this;
    }

    public ConfluenceSearchQueryBuilder maxDate(Option<Date> maxDate) {
        this.maxDate = maxDate;
        return this;
    }

    public ConfluenceSearchQueryBuilder activityObjects(Iterable<ActivityObjectType> activityObjects) {
        Iterables.addAll(this.activityObjects, activityObjects);
        return this;
    }

    public SearchQuery build() {
        AndBooleanQueryBuilder finalQuery = new AndBooleanQueryBuilder(new ContentTypeQuery(ConfluenceSearchQueryBuilder.getContentTypes(this.activityObjects)));
        if (!this.createdByUsers.isEmpty() || !this.lastModifiedByUsers.isEmpty()) {
            finalQuery.and(BooleanQuery.composeOrQuery((Set)ImmutableSet.copyOf((Iterable)Iterables.concat((Iterable)Options.catOptions((Iterable)Iterables.transform(this.createdByUsers, (Function)ToCreatorQuery.INSTANCE)), (Iterable)Options.catOptions((Iterable)Iterables.transform(this.lastModifiedByUsers, this.toLastModifierQuery))))));
        }
        if (!Iterables.isEmpty(this.spaceKeys)) {
            finalQuery.and(new SearchQuery[]{new InSpaceQuery(new HashSet<String>(this.spaceKeys))});
        }
        if (!Iterables.isEmpty(this.searchTerms)) {
            finalQuery.and(ConfluenceSearchQueryBuilder.buildMultiTextFieldQuery(this.searchTerms));
        }
        if (!Iterables.isEmpty(this.excludedSearchTerms)) {
            finalQuery.not(ConfluenceSearchQueryBuilder.buildMultiTextFieldQuery(this.excludedSearchTerms));
        }
        if (!Iterables.isEmpty(this.filters)) {
            finalQuery.and(this.filters);
        }
        if (this.minDate.isDefined() || this.maxDate.isDefined()) {
            finalQuery.and(new SearchQuery[]{new DateRangeQuery((Date)this.minDate.getOrElse((Object)null), (Date)this.maxDate.getOrElse((Object)null), true, true, DateRangeQuery.DateRangeQueryType.MODIFIED)});
        }
        return finalQuery.build();
    }

    private static SearchQuery buildMultiTextFieldQuery(Set<String> keywords) {
        StringBuilder sb = new StringBuilder();
        for (String keyword : keywords) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(keyword);
        }
        return new MultiTextFieldQuery(sb.toString(), TEXT_FIELDS, BooleanOperator.OR);
    }

    private static Collection<ContentTypeEnum> getContentTypes(Set<ActivityObjectType> activityObjectTypes) {
        ImmutableSet.Builder builder = ImmutableSet.builder();
        for (ActivityObjectType type : activityObjectTypes) {
            if (type.equals(ActivityObjectTypes.article())) {
                builder.add((Object)ContentTypeEnum.BLOG);
                continue;
            }
            if (type.equals(ActivityObjectTypes.comment())) {
                builder.add((Object)ContentTypeEnum.COMMENT);
                continue;
            }
            if (type.equals(ActivityObjectTypes.file())) {
                builder.add((Object)ContentTypeEnum.ATTACHMENT);
                continue;
            }
            if (type.equals(ConfluenceActivityObjectTypes.page())) {
                builder.add((Object)ContentTypeEnum.PAGE);
                continue;
            }
            if (type.equals(ConfluenceActivityObjectTypes.personalSpace())) {
                builder.add((Object)ContentTypeEnum.PERSONAL_SPACE_DESCRIPTION);
                continue;
            }
            if (!type.equals(ConfluenceActivityObjectTypes.space())) continue;
            builder.add((Object)ContentTypeEnum.SPACE_DESCRIPTION);
            builder.add((Object)ContentTypeEnum.PERSONAL_SPACE_DESCRIPTION);
        }
        ImmutableSet types = builder.build();
        return types.isEmpty() ? CONTENT_TYPES : types;
    }

    static class AndBooleanQueryBuilder {
        private final Set<SearchQuery> queries = Sets.newHashSet();
        private final Set<SearchQuery> nots = Sets.newHashSet();

        private AndBooleanQueryBuilder(ContentTypeQuery contentTypeQuery) {
            this.queries.add((SearchQuery)contentTypeQuery);
        }

        public void not(SearchQuery query) {
            this.nots.add(query);
        }

        AndBooleanQueryBuilder and(Iterable<SearchQuery> queries) {
            Iterables.addAll(this.queries, (Iterable)((Iterable)Preconditions.checkNotNull(queries, (Object)"queries")));
            return this;
        }

        AndBooleanQueryBuilder and(SearchQuery ... queries) {
            return this.and(Arrays.asList((Object[])Preconditions.checkNotNull((Object)queries, (Object)"Sub queries")));
        }

        AndBooleanQueryBuilder and(AndBooleanQueryBuilder queryBuilder) {
            return this.and(queryBuilder.build());
        }

        SearchQuery build() {
            return new BooleanQuery(this.queries, Collections.emptyList(), this.nots);
        }
    }

    static enum ToCreatorQuery implements Function<String, Option<CreatorQuery>>
    {
        INSTANCE;


        public Option<CreatorQuery> apply(String username) {
            if (StringUtils.isBlank((CharSequence)username)) {
                return Option.none();
            }
            return Option.some((Object)new CreatorQuery(username));
        }
    }

    static enum StringTrimmer implements Function<String, Option<String>>
    {
        INSTANCE;


        public Option<String> apply(String s) {
            return Option.option((Object)StringUtils.trimToNull((String)s));
        }
    }
}


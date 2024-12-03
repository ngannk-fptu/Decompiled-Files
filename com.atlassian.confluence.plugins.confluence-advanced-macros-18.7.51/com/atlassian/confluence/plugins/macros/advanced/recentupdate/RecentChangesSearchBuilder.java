/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.macro.MacroExecutionContext
 *  com.atlassian.confluence.macro.params.ParameterException
 *  com.atlassian.confluence.macro.query.BooleanQueryFactory
 *  com.atlassian.confluence.macro.query.params.AuthorParameter
 *  com.atlassian.confluence.macro.query.params.ContentTypeParameter
 *  com.atlassian.confluence.macro.query.params.LabelParameter
 *  com.atlassian.confluence.macro.query.params.SpaceKeyParameter
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.search.v2.ChangesSearch
 *  com.atlassian.confluence.search.v2.DefaultSearchWithToken
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SearchSort
 *  com.atlassian.confluence.search.v2.SearchWithToken
 *  com.atlassian.confluence.search.v2.SiteSearchPermissionsQueryFactory
 *  com.atlassian.confluence.search.v2.query.AllQuery
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.BooleanQuery$Builder
 *  com.atlassian.confluence.search.v2.query.ContentStatusQuery
 *  com.atlassian.confluence.search.v2.query.LastModifierUserQuery
 *  com.atlassian.confluence.search.v2.query.NonViewableCustomContentTypeQuery
 *  com.atlassian.confluence.search.v2.sort.ModifiedSort
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.plugin.PluginAccessor
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.macros.advanced.recentupdate;

import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.macro.MacroExecutionContext;
import com.atlassian.confluence.macro.params.ParameterException;
import com.atlassian.confluence.macro.query.BooleanQueryFactory;
import com.atlassian.confluence.macro.query.params.AuthorParameter;
import com.atlassian.confluence.macro.query.params.ContentTypeParameter;
import com.atlassian.confluence.macro.query.params.LabelParameter;
import com.atlassian.confluence.macro.query.params.SpaceKeyParameter;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.Theme;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.search.v2.ChangesSearch;
import com.atlassian.confluence.search.v2.DefaultSearchWithToken;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.SearchWithToken;
import com.atlassian.confluence.search.v2.SiteSearchPermissionsQueryFactory;
import com.atlassian.confluence.search.v2.query.AllQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ContentStatusQuery;
import com.atlassian.confluence.search.v2.query.LastModifierUserQuery;
import com.atlassian.confluence.search.v2.query.NonViewableCustomContentTypeQuery;
import com.atlassian.confluence.search.v2.sort.ModifiedSort;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.plugin.PluginAccessor;
import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class RecentChangesSearchBuilder {
    public static final int DEFAULT_PAGE_SIZE = 15;
    private final PluginAccessor pluginAccessor;
    private final UserAccessor userAccessor;
    private final SiteSearchPermissionsQueryFactory siteSearchPermissionsQueryFactory;
    private String labels;
    private String authors;
    private String spaceKeys;
    private String contentTypes;
    private int startIndex = 0;
    private int pageSize = 15;
    private long searchToken = -1L;

    public RecentChangesSearchBuilder(PluginAccessor pluginAccessor, UserAccessor userAccessor, SiteSearchPermissionsQueryFactory siteSearchPermissionsQueryFactory) {
        this.pluginAccessor = pluginAccessor;
        this.userAccessor = userAccessor;
        this.siteSearchPermissionsQueryFactory = siteSearchPermissionsQueryFactory;
    }

    public ISearch buildSearch() {
        BooleanQuery.Builder queryBuilder = BooleanQuery.builder();
        queryBuilder.addFilter((SearchQuery)new ContentStatusQuery(new ContentStatus[]{ContentStatus.CURRENT, ContentStatus.HISTORICAL}));
        queryBuilder.addFilter(this.siteSearchPermissionsQueryFactory.create());
        queryBuilder.addFilter((SearchQuery)new NonViewableCustomContentTypeQuery(this.pluginAccessor));
        try {
            if (StringUtils.isNotBlank((CharSequence)this.labels)) {
                queryBuilder.addMust((Object)this.getLabelQuery(this.labels));
            }
            queryBuilder.addMust((Object)this.getContentTypeQuery(this.contentTypes));
            if (StringUtils.isNotBlank((CharSequence)this.authors)) {
                this.getAuthorQuery(this.authors).ifPresent(arg_0 -> ((BooleanQuery.Builder)queryBuilder).addFilter(arg_0));
            }
            if (StringUtils.isNotBlank((CharSequence)this.spaceKeys)) {
                queryBuilder.addMust((Object)this.getSpaceQuery(this.spaceKeys));
            }
        }
        catch (ParameterException e) {
            throw new IllegalArgumentException(e);
        }
        return new ChangesSearch(queryBuilder.build(), (SearchSort)ModifiedSort.DEFAULT, this.startIndex, this.pageSize);
    }

    public SearchWithToken buildSearchWithToken() {
        Preconditions.checkArgument((this.searchToken > 0L ? 1 : 0) != 0, (Object)"searchToken must be greater than 0.");
        return new DefaultSearchWithToken(this.buildSearch(), this.searchToken);
    }

    private SearchQuery getSpaceQuery(String spaceKeys) throws ParameterException {
        return ((BooleanQueryFactory)new SpaceKeyParameter().findValue(this.newMacroExecutionContext("spaces", spaceKeys))).toBooleanQuery();
    }

    private SearchQuery getContentTypeQuery(String contentType) throws ParameterException {
        if (StringUtils.isBlank((CharSequence)contentType)) {
            BooleanQueryFactory booleanQueryFactory = new BooleanQueryFactory();
            booleanQueryFactory.addMust((SearchQuery)AllQuery.getInstance());
            return booleanQueryFactory.toBooleanQuery();
        }
        return ((BooleanQueryFactory)new ContentTypeParameter().findValue(this.newMacroExecutionContext("type", contentType))).toBooleanQuery();
    }

    private SearchQuery getLabelQuery(String labels) throws ParameterException {
        return ((BooleanQueryFactory)new LabelParameter().findValue(this.newMacroExecutionContext("labels", labels))).toBooleanQuery();
    }

    private Optional<SearchQuery> getAuthorQuery(String authorsParamValue) throws ParameterException {
        Set authors = ((Set)new AuthorParameter().findValue(this.newMacroExecutionContext("author", authorsParamValue))).stream().map(arg_0 -> ((UserAccessor)this.userAccessor).getUserByName(arg_0)).filter(Objects::nonNull).collect(Collectors.toSet());
        return !authors.isEmpty() ? Optional.of(new LastModifierUserQuery(authors)) : Optional.empty();
    }

    private MacroExecutionContext newMacroExecutionContext(String key, String value) {
        return new MacroExecutionContext(Collections.singletonMap(key, value), null, new PageContext());
    }

    public String buildSearchUrl(Theme theme, String contextPath) {
        StringBuilder url = new StringBuilder(contextPath == null ? "" : contextPath);
        url.append("/plugins/recently-updated/changes.action");
        url.append("?theme=").append(theme == null ? Theme.concise.name() : theme.name());
        if (this.pageSize != 15) {
            url.append("&pageSize=").append(this.pageSize);
        }
        if (this.startIndex > 0) {
            url.append("&startIndex=").append(this.startIndex);
        }
        if (this.searchToken > 0L) {
            url.append("&searchToken=").append(this.searchToken);
        }
        if (StringUtils.isNotBlank((CharSequence)this.authors)) {
            url.append("&authors=").append(HtmlUtil.urlEncode((String)this.authors));
        }
        if (StringUtils.isNotBlank((CharSequence)this.labels)) {
            url.append("&labels=").append(HtmlUtil.urlEncode((String)this.labels));
        }
        if (StringUtils.isNotBlank((CharSequence)this.spaceKeys)) {
            url.append("&spaceKeys=").append(HtmlUtil.urlEncode((String)this.spaceKeys));
        }
        if (StringUtils.isNotBlank((CharSequence)this.contentTypes)) {
            url.append("&contentType=").append(this.contentTypes);
        }
        return url.toString();
    }

    public RecentChangesSearchBuilder withLabels(String labels) {
        this.labels = labels;
        return this;
    }

    public RecentChangesSearchBuilder withAuthors(String authors) {
        this.authors = authors;
        return this;
    }

    public RecentChangesSearchBuilder withSpaceKeys(String spaceKeys) {
        this.spaceKeys = spaceKeys;
        return this;
    }

    public RecentChangesSearchBuilder withContentTypes(String contentTypes) {
        this.contentTypes = contentTypes;
        return this;
    }

    public RecentChangesSearchBuilder withStartIndex(int startIndex) {
        Preconditions.checkArgument((startIndex >= 0 ? 1 : 0) != 0);
        this.startIndex = startIndex;
        return this;
    }

    public RecentChangesSearchBuilder withPageSize(int pageSize) {
        Preconditions.checkArgument((pageSize > 0 ? 1 : 0) != 0);
        this.pageSize = pageSize;
        return this;
    }

    public RecentChangesSearchBuilder withSearchToken(long searchToken) {
        Preconditions.checkArgument((searchToken > 0L ? 1 : 0) != 0);
        this.searchToken = searchToken;
        return this;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.search.v2.SearchFieldNames
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.search.model;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.plugins.search.api.model.SearchExplanation;
import com.atlassian.confluence.plugins.search.api.model.SearchResult;
import com.atlassian.confluence.plugins.search.api.model.SearchResultContainer;
import com.atlassian.confluence.plugins.search.model.LastModificationFormatter;
import com.atlassian.confluence.plugins.search.model.SearchResultBuilder;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.user.User;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

@Internal
class ContentResultBuilder
implements SearchResultBuilder {
    private static final Pattern PLUGIN_KEY_AS_CSS_ID = Pattern.compile(":|\\.");
    private final LastModificationFormatter lastModificationFormatter;
    private final ContextPathHolder contextPathHolder;
    private final User user;

    ContentResultBuilder(LastModificationFormatter lastModificationFormatter, ContextPathHolder contextPathHolder, User user) {
        this.lastModificationFormatter = lastModificationFormatter;
        this.contextPathHolder = contextPathHolder;
        this.user = user;
    }

    @Override
    public SearchResult newSearchResult(Function<String, String> getFieldValue, Supplier<String> getTitleWithHighlights, Supplier<String> getExcerptWithHighlights, Supplier<SearchExplanation> getExplanation) {
        String friendlyDate = this.lastModificationFormatter.format(getFieldValue.apply(SearchFieldNames.LAST_MODIFICATION_DATE), this.user);
        long id = SearchResultBuilder.getId(getFieldValue.apply(SearchFieldNames.HANDLE));
        return new SearchResult(id, this.getType(getFieldValue), getTitleWithHighlights.get(), getExcerptWithHighlights.get(), this.getUrlPath(getFieldValue), this.getSearchResultContainer(getFieldValue), friendlyDate, getExplanation.get(), Collections.emptyMap());
    }

    private String getUrlPath(Function<String, String> getFieldValue) {
        return ContentResultBuilder.appendAnalyticsParams(getFieldValue.apply("urlPath"));
    }

    private SearchResultContainer getSearchResultContainer(Function<String, String> getFieldValue) {
        String spaceName = getFieldValue.apply(SearchFieldNames.SPACE_NAME);
        Space space = new Space(getFieldValue.apply(SearchFieldNames.SPACE_KEY));
        String spaceUrl = this.contextPathHolder.getContextPath() + space.getUrlPath();
        return SearchResultContainer.create(spaceName, spaceUrl);
    }

    private String getType(Function<String, String> getFieldValue) {
        String type = getFieldValue.apply(SearchFieldNames.TYPE);
        String contentPluginKey = getFieldValue.apply(SearchFieldNames.CONTENT_PLUGIN_KEY);
        if ("custom".equals(type)) {
            return PLUGIN_KEY_AS_CSS_ID.matcher(contentPluginKey).replaceAll("-");
        }
        return type;
    }

    private static String appendAnalyticsParams(String url) {
        if (StringUtils.isBlank((CharSequence)url)) {
            return url;
        }
        try {
            URI uri = new URI(url);
            if (uri.isAbsolute()) {
                throw new AssertionError((Object)"urlPath should be relative.");
            }
            Object query = uri.getQuery();
            if (query == null) {
                query = "";
            } else if (!((String)query).endsWith("&")) {
                query = (String)query + "&";
            }
            query = (String)query + "src=search";
            return new URI(null, null, uri.getPath(), (String)query, uri.getFragment()).toString();
        }
        catch (URISyntaxException e) {
            return url;
        }
    }
}


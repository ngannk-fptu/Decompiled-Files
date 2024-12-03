/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.internal.search.contentnames.v2;

import com.atlassian.confluence.search.actions.json.ContentNameMatch;
import com.atlassian.confluence.search.contentnames.Category;
import com.atlassian.confluence.search.contentnames.SearchResult;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.actions.ContentTypesDisplayMapper;
import com.atlassian.core.filters.ServletContextThreadLocal;
import java.util.Objects;
import java.util.function.Function;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

public class DefaultSearchResultTransformer
implements Function<SearchResult, ContentNameMatch> {
    private final ContentTypesDisplayMapper contentTypesDisplayMapper;

    public DefaultSearchResultTransformer(ContentTypesDisplayMapper contentTypesDisplayMapper) {
        this.contentTypesDisplayMapper = Objects.requireNonNull(contentTypesDisplayMapper);
    }

    @Override
    public ContentNameMatch apply(SearchResult searchResult) {
        String spaceKey;
        String iconUrl = null;
        if (searchResult.getCategory() == Category.PEOPLE) {
            iconUrl = this.contentTypesDisplayMapper.getIconUriReferenceForUsername(searchResult.getPreviewKey());
        }
        String className = this.contentTypesDisplayMapper.getClassName(searchResult);
        String spaceName = searchResult.getSpaceName();
        if (!StringUtils.isEmpty((CharSequence)spaceName)) {
            spaceName = HtmlUtil.htmlEncode(spaceName);
        }
        if (!StringUtils.isEmpty((CharSequence)(spaceKey = searchResult.getSpaceKey()))) {
            spaceKey = HtmlUtil.htmlEncode(spaceKey);
        }
        HttpServletRequest servletRequest = ServletContextThreadLocal.getRequest();
        ContentNameMatch contentNameMatch = new ContentNameMatch();
        if (searchResult.getId() != null) {
            contentNameMatch.setId(searchResult.getId().toString());
        }
        if (searchResult.getCategory() == Category.PEOPLE) {
            contentNameMatch.setUsername(searchResult.getUsername());
        }
        contentNameMatch.setClassName(className);
        contentNameMatch.setHref(servletRequest.getContextPath() + searchResult.getUrl());
        contentNameMatch.setIcon(iconUrl);
        contentNameMatch.setName(HtmlUtil.htmlEncode(searchResult.getName()));
        contentNameMatch.setSpaceName(spaceName);
        contentNameMatch.setSpaceKey(spaceKey);
        return contentNameMatch;
    }
}


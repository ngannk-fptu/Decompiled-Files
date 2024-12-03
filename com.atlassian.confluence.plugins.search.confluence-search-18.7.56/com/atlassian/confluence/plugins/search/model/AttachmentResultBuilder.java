/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.content.ui.AttachmentUiSupport
 *  com.atlassian.confluence.content.ui.AttachmentUiSupport$AttachmentInfo
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.search.v2.SearchFieldNames
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.user.User
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableMap
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.search.model;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.content.ui.AttachmentUiSupport;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.plugins.search.api.model.SearchExplanation;
import com.atlassian.confluence.plugins.search.api.model.SearchResult;
import com.atlassian.confluence.plugins.search.api.model.SearchResultContainer;
import com.atlassian.confluence.plugins.search.model.LastModificationFormatter;
import com.atlassian.confluence.plugins.search.model.SearchResultBuilder;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.user.User;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.commons.lang3.StringUtils;

@Internal
class AttachmentResultBuilder
implements SearchResultBuilder {
    private final LastModificationFormatter lastModificationFormatter;
    private final ContextPathHolder contextPathHolder;
    private final User user;

    AttachmentResultBuilder(LastModificationFormatter lastModificationFormatter, ContextPathHolder contextPathHolder, User user) {
        this.lastModificationFormatter = lastModificationFormatter;
        this.contextPathHolder = contextPathHolder;
        this.user = user;
    }

    @Override
    public SearchResult newSearchResult(Function<String, String> getFieldValue, Supplier<String> getTitleWithHighlights, Supplier<String> getExcerptWithHighlights, Supplier<SearchExplanation> getExplanation) {
        String friendlyDate = this.lastModificationFormatter.format(getFieldValue.apply(SearchFieldNames.LAST_MODIFICATION_DATE), this.user);
        long id = SearchResultBuilder.getId(getFieldValue.apply(SearchFieldNames.HANDLE));
        String url = getFieldValue.apply("downloadPath");
        return new SearchResult(id, getFieldValue.apply(SearchFieldNames.TYPE), getTitleWithHighlights.get(), getExcerptWithHighlights.get(), url, this.getSearchResultContainer(getFieldValue), friendlyDate, getExplanation.get(), this.getMetadata(getFieldValue));
    }

    private Map<String, String> getMetadata(Function<String, String> getFieldValue) {
        String title = getFieldValue.apply(SearchFieldNames.TITLE);
        String mimeType = getFieldValue.apply(SearchFieldNames.ATTACHMENT_MIME_TYPE);
        String fileExtension = StringUtils.substringAfterLast((String)title, (String)".");
        AttachmentUiSupport.AttachmentInfo attachmentInfo = AttachmentUiSupport.getAttachmentInfo((String)mimeType, (String)fileExtension);
        return ImmutableMap.of((Object)"attachmentMimeType", (Object)Strings.nullToEmpty((String)mimeType), (Object)"cssClass", (Object)Strings.nullToEmpty((String)attachmentInfo.getCssClass()));
    }

    private SearchResultContainer getSearchResultContainer(Function<String, String> getFieldValue) {
        String spaceName = getFieldValue.apply(SearchFieldNames.SPACE_NAME);
        String spaceUrl = this.contextPathHolder.getContextPath() + new Space(getFieldValue.apply(SearchFieldNames.SPACE_KEY)).getUrlPath();
        String contentRealTitle = getFieldValue.apply(SearchFieldNames.ATTACHMENT_OWNER_REAL_TITLE);
        String contentUrlPath = getFieldValue.apply(SearchFieldNames.ATTACHMENT_OWNER_URL_PATH);
        SearchResultContainer contentPage = SearchResultContainer.create(contentRealTitle, this.contextPathHolder.getContextPath() + contentUrlPath);
        return SearchResultContainer.create(spaceName, spaceUrl, contentPage);
    }
}


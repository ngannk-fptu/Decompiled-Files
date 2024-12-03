/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Handle
 *  com.atlassian.confluence.core.persistence.hibernate.HibernateHandle
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.confluence_kb_space_blueprint.rest.response;

import com.atlassian.bonnie.Handle;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.User;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AugmentedSearchResult
implements SearchResult {
    private final Map<String, String> augmentedFields;
    private final SearchResult searchResult;

    public AugmentedSearchResult(SearchResult searchResult, Map<String, String> augmentedFields) {
        this.searchResult = searchResult;
        this.augmentedFields = augmentedFields;
    }

    public Map<String, String> getExtraFields() {
        HashMap<String, String> extraFields = new HashMap<String, String>(this.searchResult.getExtraFields());
        extraFields.putAll(this.augmentedFields);
        return extraFields;
    }

    public String getResultExcerpt() {
        return this.searchResult.getResultExcerpt();
    }

    public String getResultExcerptWithHighlights() {
        return this.searchResult.getResultExcerptWithHighlights();
    }

    public String getDisplayTitleWithHighlights() {
        return this.searchResult.getDisplayTitleWithHighlights();
    }

    public String getContent() {
        return this.searchResult.getContent();
    }

    public String getType() {
        return this.searchResult.getType();
    }

    public final String getStatus() {
        return this.searchResult.getStatus();
    }

    public boolean isHomePage() {
        return this.searchResult.isHomePage();
    }

    public Date getLastModificationDate() {
        return this.searchResult.getLastModificationDate();
    }

    @Deprecated
    public String getLastModifier() {
        return this.searchResult.getLastModifier();
    }

    public ConfluenceUser getLastModifierUser() {
        return this.searchResult.getLastModifierUser();
    }

    public String getDisplayTitle() {
        return this.searchResult.getDisplayTitle();
    }

    public String getUrlPath() {
        return this.searchResult.getUrlPath();
    }

    public String getLastUpdateDescription() {
        return this.searchResult.getLastUpdateDescription();
    }

    public String getSpaceName() {
        return this.searchResult.getSpaceName();
    }

    public String getSpaceKey() {
        return this.searchResult.getSpaceKey();
    }

    public boolean hasLabels() {
        return this.searchResult.hasLabels();
    }

    public Set<String> getLabels(User user) {
        return this.searchResult.getLabels(user);
    }

    public Set<String> getPersonalLabels() {
        return this.searchResult.getPersonalLabels();
    }

    public Date getCreationDate() {
        return this.searchResult.getCreationDate();
    }

    @Deprecated
    public String getCreator() {
        return this.searchResult.getCreator();
    }

    public ConfluenceUser getCreatorUser() {
        return this.searchResult.getCreatorUser();
    }

    public String getOwnerType() {
        return this.searchResult.getOwnerType();
    }

    public String getOwnerTitle() {
        return this.searchResult.getOwnerTitle();
    }

    public Integer getContentVersion() throws NumberFormatException {
        return this.searchResult.getContentVersion();
    }

    public Handle getHandle() {
        return this.searchResult.getHandle();
    }

    public long getHandleId() {
        Handle handle = this.getHandle();
        return handle instanceof HibernateHandle ? ((HibernateHandle)handle).getId() : 0L;
    }

    public Set<String> getFieldNames() {
        return this.searchResult.getFieldNames();
    }

    public String getField(String fieldName) {
        return this.searchResult.getField(fieldName);
    }

    public Set<String> getFieldValues(String fieldName) {
        return this.searchResult.getFieldValues(fieldName);
    }
}


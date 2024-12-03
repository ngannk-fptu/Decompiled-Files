/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.bonnie.Handle
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.bonnie.Handle;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.User;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class ProjectedSearchResult
implements SearchResult {
    private final SearchResult delegate;
    private final Set<String> requestedFields;

    public ProjectedSearchResult(SearchResult delegate, Collection<String> requestedFields) {
        this.delegate = Objects.requireNonNull(delegate);
        this.requestedFields = new HashSet<String>(Objects.requireNonNull(requestedFields));
    }

    @Override
    public Map<String, String> getExtraFields() {
        return this.delegate.getExtraFields().entrySet().stream().filter(entry -> this.requestedFields.contains(entry.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public String getResultExcerpt() {
        return this.delegate.getResultExcerpt();
    }

    @Override
    public String getResultExcerptWithHighlights() {
        return this.delegate.getResultExcerptWithHighlights();
    }

    @Override
    public String getDisplayTitleWithHighlights() {
        return this.delegate.getDisplayTitleWithHighlights();
    }

    @Override
    public String getContent() {
        return this.delegate.getContent();
    }

    @Override
    public String getType() {
        return this.delegate.getType();
    }

    @Override
    public final String getStatus() {
        return this.delegate.getStatus();
    }

    @Override
    public boolean isHomePage() {
        return this.delegate.isHomePage();
    }

    @Override
    public Date getLastModificationDate() {
        return this.delegate.getLastModificationDate();
    }

    @Override
    @Deprecated
    public String getLastModifier() {
        return this.delegate.getLastModifier();
    }

    @Override
    public ConfluenceUser getLastModifierUser() {
        return this.delegate.getLastModifierUser();
    }

    @Override
    public String getDisplayTitle() {
        return this.delegate.getDisplayTitle();
    }

    @Override
    public String getUrlPath() {
        return this.delegate.getUrlPath();
    }

    @Override
    public String getLastUpdateDescription() {
        return this.delegate.getLastUpdateDescription();
    }

    @Override
    public String getSpaceName() {
        return this.delegate.getSpaceName();
    }

    @Override
    public String getSpaceKey() {
        return this.delegate.getSpaceKey();
    }

    @Override
    public boolean hasLabels() {
        return this.delegate.hasLabels();
    }

    @Override
    public Set<String> getLabels(User user) {
        return this.delegate.getLabels(user);
    }

    @Override
    public Set<String> getPersonalLabels() {
        return this.delegate.getPersonalLabels();
    }

    @Override
    public Date getCreationDate() {
        return this.delegate.getCreationDate();
    }

    @Override
    @Deprecated
    public String getCreator() {
        return this.delegate.getCreator();
    }

    @Override
    public ConfluenceUser getCreatorUser() {
        return this.delegate.getCreatorUser();
    }

    @Override
    public String getOwnerType() {
        return this.delegate.getOwnerType();
    }

    @Override
    public String getOwnerTitle() {
        return this.delegate.getOwnerTitle();
    }

    @Override
    public Integer getContentVersion() throws NumberFormatException {
        return this.delegate.getContentVersion();
    }

    @Override
    public Handle getHandle() {
        return this.delegate.getHandle();
    }

    @Override
    public long getHandleId() {
        Handle handle = this.getHandle();
        return handle instanceof HibernateHandle ? ((HibernateHandle)handle).getId() : 0L;
    }

    @Override
    public Set<String> getFieldNames() {
        return this.delegate.getFieldNames();
    }

    @Override
    public String getField(String fieldName) {
        return this.delegate.getField(fieldName);
    }

    @Override
    public Set<String> getFieldValues(String fieldName) {
        return this.delegate.getFieldValues(fieldName);
    }
}


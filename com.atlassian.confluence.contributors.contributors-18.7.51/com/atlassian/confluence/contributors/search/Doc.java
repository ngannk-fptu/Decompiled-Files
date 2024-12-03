/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.core.persistence.hibernate.HibernateHandle
 *  com.atlassian.confluence.search.v2.SearchFieldNames
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.contributors.search;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.google.common.collect.ImmutableSet;
import java.text.ParseException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

@Internal
public class Doc {
    public static final String AUTHOR_CONTRIBUTIONS = "authorContributions";
    public static final String LABEL_CONTRIBUTIONS = "labelContributions";
    public static final String WATCHERS = "watchers";
    public static final String ANCESTOR_IDS = "ancestorIds";
    public static final String KEY = "key";
    public static final String MODIFIED = "modified";
    public static final String LAST_MODIFIER_NAME = "lastModifierName";
    public static final String SCOPE_CHILDREN = "children";
    public static final String SCOPE_DESCENDANTS = "descendants";
    public static final String SPACES_ALL = "@all";
    public static final Set<String> REQUESTED_FIELDS = ImmutableSet.of((Object)"authorContributions", (Object)"labelContributions", (Object)"watchers", (Object)"ancestorIds", (Object)"key", (Object)"modified", (Object[])new String[]{"lastModifierName", "containingPageId", SearchFieldNames.HANDLE, SearchFieldNames.URL_PATH, SearchFieldNames.SPACE_KEY, SearchFieldNames.TITLE, SearchFieldNames.PAGE_DISPLAY_TITLE});
    private final Function<String, String> getFieldValue;
    private final Function<String, String[]> getFieldValues;

    public Doc(Function<String, String> getFieldValue, Function<String, String[]> getFieldValues) {
        this.getFieldValue = getFieldValue;
        this.getFieldValues = getFieldValues;
    }

    public Doc(Function<String, String[]> getFieldValues) {
        this.getFieldValue = fieldName -> {
            String[] values = (String[])getFieldValues.apply((String)fieldName);
            return values.length == 0 ? null : values[0];
        };
        this.getFieldValues = getFieldValues;
    }

    public String getTitle() {
        return this.getFieldValue.apply(SearchFieldNames.TITLE);
    }

    public String getUrlPath() {
        return this.getFieldValue.apply(SearchFieldNames.URL_PATH);
    }

    public String[] getAuthorContributions() {
        return this.getFieldValues.apply(AUTHOR_CONTRIBUTIONS);
    }

    public String[] getLabelContributions() {
        return this.getFieldValues.apply(LABEL_CONTRIBUTIONS);
    }

    public String getSpaceKey() {
        return this.getFieldValue.apply(SearchFieldNames.SPACE_KEY);
    }

    public String[] getWatchers() {
        return this.getFieldValues.apply(WATCHERS);
    }

    public String getHandle() {
        return this.getFieldValue.apply(SearchFieldNames.HANDLE);
    }

    public Optional<Long> getParentId() {
        String[] values = this.getFieldValues.apply(ANCESTOR_IDS);
        if (values.length == 0) {
            return Optional.empty();
        }
        String lastVal = values[values.length - 1];
        try {
            long parentId = Long.parseLong(lastVal.substring(lastVal.lastIndexOf(" ") + 1));
            return values.length > 1 || lastVal.lastIndexOf(" ") >= 0 ? Optional.of(parentId) : Optional.empty();
        }
        catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    public String getKey() {
        return this.getFieldValue.apply(KEY);
    }

    public String getPageDisplayTitle() {
        return this.getFieldValue.apply(SearchFieldNames.PAGE_DISPLAY_TITLE);
    }

    public String getModified() {
        return this.getFieldValue.apply(MODIFIED);
    }

    public String getLastModifier() {
        return this.getFieldValue.apply(LAST_MODIFIER_NAME);
    }

    public long getPageId() {
        String containingPageId = this.getFieldValue.apply("containingPageId");
        if (containingPageId != null) {
            return Long.parseLong(containingPageId);
        }
        return this.createHandle(this.getHandle()).getId();
    }

    private HibernateHandle createHandle(String str) {
        try {
            return new HibernateHandle(str);
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}


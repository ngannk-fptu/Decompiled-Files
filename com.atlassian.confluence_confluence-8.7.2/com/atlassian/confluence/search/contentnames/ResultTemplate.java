/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.contentnames;

import com.atlassian.confluence.search.contentnames.Category;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ResultTemplate {
    public static final ResultTemplate DEFAULT = new ResultTemplate();
    private final Map<Category, Integer> categoryCounts = new LinkedHashMap<Category, Integer>();
    private int numResultsSpecified = 0;

    public void addCategory(Category category, int maxResultCount) {
        this.categoryCounts.put(category, maxResultCount);
        this.numResultsSpecified += maxResultCount;
    }

    public boolean hasCategory(Category category) {
        return this.categoryCounts.containsKey(category);
    }

    public int getMaxResultCount(Category category) {
        if (!this.hasCategory(category)) {
            return 0;
        }
        return this.categoryCounts.get(category);
    }

    public int getNumberOfCategories() {
        return this.categoryCounts.size();
    }

    public Set<Category> getCategories() {
        return this.categoryCounts.keySet();
    }

    public int getMaximumResults() {
        return this.numResultsSpecified;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResultTemplate)) {
            return false;
        }
        ResultTemplate that = (ResultTemplate)o;
        if (this.numResultsSpecified != that.numResultsSpecified) {
            return false;
        }
        return Objects.equals(this.categoryCounts, that.categoryCounts);
    }

    public int hashCode() {
        int result = this.categoryCounts != null ? this.categoryCounts.hashCode() : 0;
        result = 31 * result + this.numResultsSpecified;
        return result;
    }

    static {
        DEFAULT.addCategory(Category.CONTENT, 6);
        DEFAULT.addCategory(Category.ATTACHMENTS, 2);
        DEFAULT.addCategory(Category.PEOPLE, 3);
        DEFAULT.addCategory(Category.SPACES, 2);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.types.FilterSet;

public class FilterSetCollection {
    private List<FilterSet> filterSets = new ArrayList<FilterSet>();

    public FilterSetCollection() {
    }

    public FilterSetCollection(FilterSet filterSet) {
        this.addFilterSet(filterSet);
    }

    public void addFilterSet(FilterSet filterSet) {
        this.filterSets.add(filterSet);
    }

    public String replaceTokens(String line) {
        String replacedLine = line;
        for (FilterSet filterSet : this.filterSets) {
            replacedLine = filterSet.replaceTokens(replacedLine);
        }
        return replacedLine;
    }

    public boolean hasFilters() {
        return this.filterSets.stream().anyMatch(FilterSet::hasFilters);
    }
}


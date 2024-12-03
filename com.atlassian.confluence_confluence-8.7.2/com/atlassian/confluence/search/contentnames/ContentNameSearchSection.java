/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.search.contentnames;

import com.atlassian.confluence.search.actions.json.ContentNameMatch;
import java.util.Comparator;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ContentNameSearchSection {
    public static final Comparator<ContentNameSearchSection> COMPARATOR = (contentNameSearchSection, contentNameSearchSection2) -> {
        Integer weight1 = contentNameSearchSection.getWeight();
        Integer weight2 = contentNameSearchSection2.getWeight();
        return weight1.compareTo(weight2);
    };
    private final Integer weight;
    private final List<ContentNameMatch> results;

    public ContentNameSearchSection(@NonNull Integer weight, @NonNull List<ContentNameMatch> results) {
        this.weight = weight;
        this.results = results;
    }

    public Integer getWeight() {
        return this.weight;
    }

    public List<ContentNameMatch> getResults() {
        return this.results;
    }
}


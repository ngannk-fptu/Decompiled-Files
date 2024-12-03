/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.internal.search.v2.SpaceCategoryQueryFactory;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.search.service.SpaceCategoryEnum;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.AllQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ConstantScoreQuery;
import com.atlassian.confluence.search.v2.query.MatchNoDocsQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SpaceCategoryQuery
implements SearchQuery {
    private static final String KEY = "spaceCategory";
    private final Set<SpaceCategoryEnum> spaceCategories;
    private final LabelManager labelManager;

    public SpaceCategoryQuery(Collection<SpaceCategoryEnum> categories, LabelManager labelManager) {
        if (categories == null || categories.isEmpty()) {
            throw new IllegalArgumentException("categories must be supplied.");
        }
        this.spaceCategories = ImmutableSet.copyOf(categories);
        this.labelManager = labelManager;
    }

    public SpaceCategoryQuery(SpaceCategoryEnum category, LabelManager labelManager) {
        this((Collection<SpaceCategoryEnum>)ImmutableSet.of((Object)((Object)category)), labelManager);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List<String> getParameters() {
        return this.spaceCategories.stream().map(SpaceCategoryEnum::getRepresentation).collect(Collectors.toList());
    }

    public Set<SpaceCategoryEnum> getSpaceCategories() {
        return this.spaceCategories;
    }

    @Override
    public SearchQuery expand() {
        SpaceCategoryQueryFactory<SearchQuery> factory = new SpaceCategoryQueryFactory<SearchQuery>(this.spaceCategories, this.labelManager, BooleanQuery::builder, TermQuery::builder, MatchNoDocsQuery::getInstance, AllQuery::getInstance);
        return new ConstantScoreQuery(factory.create());
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        SpaceCategoryQuery other = (SpaceCategoryQuery)obj;
        return new EqualsBuilder().append(this.spaceCategories, other.spaceCategories).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(123, 37).append(this.spaceCategories).toHashCode();
    }
}


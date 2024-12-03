/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.Space
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Sets
 */
package com.atlassian.confluence.plugins.createcontent.rest;

import com.atlassian.confluence.plugins.createcontent.rest.SpaceResultsEntity;
import com.atlassian.confluence.plugins.createcontent.rest.entities.SpaceEntity;
import com.atlassian.confluence.spaces.Space;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public class SpaceResultsEntityBuilder {
    private final Set<Space> spaces = Sets.newLinkedHashSet();
    private final int resultsLimit;
    private final Predicate<? super Space> spaceFilter;
    private boolean allSpacesAdded = true;

    public SpaceResultsEntityBuilder(int resultsLimit, Predicate<Space> spaceFilter) {
        this.resultsLimit = resultsLimit;
        this.spaceFilter = spaceFilter;
    }

    private static <T> boolean addUntilLimitReached(Collection<T> destination, Iterable<T> source, int destinationSizeLimit) {
        for (T item : source) {
            if (destination.size() < destinationSizeLimit) {
                destination.add(item);
                continue;
            }
            return false;
        }
        return true;
    }

    public SpaceResultsEntityBuilder addSpaces(Space ... spaces) {
        return this.addSpaces(Arrays.asList(spaces));
    }

    public SpaceResultsEntityBuilder addSpaces(Collection<Space> newSpaces) {
        this.allSpacesAdded &= SpaceResultsEntityBuilder.addUntilLimitReached(this.spaces, Collections2.filter(newSpaces, this.spaceFilter), this.resultsLimit);
        return this;
    }

    public Set<Space> getSpaces() {
        return this.spaces;
    }

    public SpaceResultsEntity build() {
        return new SpaceResultsEntity(Collections2.transform(this.spaces, SpaceEntity.spaceTransformer()), this.resultsLimit, !this.allSpacesAdded);
    }
}


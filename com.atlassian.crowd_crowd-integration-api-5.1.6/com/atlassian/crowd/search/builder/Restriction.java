/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.google.common.base.Preconditions
 */
package com.atlassian.crowd.search.builder;

import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.search.builder.Combine;
import com.atlassian.crowd.search.query.entity.restriction.MatchMode;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.PropertyRestriction;
import com.atlassian.crowd.search.query.entity.restriction.TermRestriction;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.stream.Collectors;

public class Restriction {
    public static <T> RestrictionWithProperty<T> on(Property<T> property) {
        return new RestrictionWithProperty<T>(property);
    }

    public static class RestrictionWithProperty<T> {
        private final Property<T> property;

        public RestrictionWithProperty(Property<T> property) {
            this.property = property;
        }

        public PropertyRestriction<T> exactlyMatching(T value) {
            return new TermRestriction<T>(this.property, MatchMode.EXACTLY_MATCHES, value);
        }

        public SearchRestriction exactlyMatchingAny(Collection<T> values) {
            Preconditions.checkArgument((!values.isEmpty() ? 1 : 0) != 0, (Object)"exactlyMatchingAny requires non-empty collection of values to match");
            return Combine.anyOfIfNeeded(values.stream().map(this::exactlyMatching).collect(Collectors.toList()));
        }

        public PropertyRestriction<T> startingWith(T value) {
            return new TermRestriction<T>(this.property, MatchMode.STARTS_WITH, value);
        }

        public PropertyRestriction<T> endingWith(T value) {
            return new TermRestriction<T>(this.property, MatchMode.ENDS_WITH, value);
        }

        public PropertyRestriction<T> containing(T value) {
            return new TermRestriction<T>(this.property, MatchMode.CONTAINS, value);
        }

        public PropertyRestriction<T> lessThan(T value) {
            return new TermRestriction<T>(this.property, MatchMode.LESS_THAN, value);
        }

        public PropertyRestriction<T> lessThanOrEqual(T value) {
            return new TermRestriction<T>(this.property, MatchMode.LESS_THAN_OR_EQUAL, value);
        }

        public PropertyRestriction<T> greaterThan(T value) {
            return new TermRestriction<T>(this.property, MatchMode.GREATER_THAN, value);
        }

        public PropertyRestriction<T> greaterThanOrEqual(T value) {
            return new TermRestriction<T>(this.property, MatchMode.GREATER_THAN_OR_EQUAL, value);
        }

        public PropertyRestriction<T> isNull() {
            return new TermRestriction<Object>(this.property, MatchMode.NULL, null);
        }
    }
}


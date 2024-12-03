/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.caldav.filter;

import com.atlassian.confluence.extra.calendar3.caldav.filter.AbstractPropertyFilter;
import org.bedework.util.calendar.PropertyIndex;

public class PropertyValueFilter<T>
extends AbstractPropertyFilter {
    protected final T entity;
    protected MatchingConfiguration matchingConfiguration;

    public PropertyValueFilter(String name, PropertyIndex.PropertyInfoIndex propertyInfoIndex, T entity) {
        super(name, propertyInfoIndex);
        this.entity = entity;
    }

    public MatchingConfiguration getMatchingConfiguration() {
        return this.matchingConfiguration;
    }

    public void setMatchingConfiguration(MatchingConfiguration matchingConfiguration) {
        this.matchingConfiguration = matchingConfiguration;
    }

    public T getEntity() {
        return this.entity;
    }

    public static class MatchingConfiguration {
        private boolean isExactMatch;
        private boolean isCaseSensitive;

        public MatchingConfiguration(boolean isExactMatch) {
            this(isExactMatch, false);
        }

        public MatchingConfiguration(boolean isExactMatch, boolean isCaseSensitive) {
            this.isExactMatch = isExactMatch;
            this.isCaseSensitive = isCaseSensitive;
        }

        public boolean isExactMatch() {
            return this.isExactMatch;
        }

        public void setExactMatch(boolean exactMatch) {
            this.isExactMatch = exactMatch;
        }

        public boolean isCaseSensitive() {
            return this.isCaseSensitive;
        }

        public void setCaseSensitive(boolean caseSensitive) {
            this.isCaseSensitive = caseSensitive;
        }
    }
}


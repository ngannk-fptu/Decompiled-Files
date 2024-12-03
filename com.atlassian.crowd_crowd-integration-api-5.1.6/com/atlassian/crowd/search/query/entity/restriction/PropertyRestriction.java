/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 */
package com.atlassian.crowd.search.query.entity.restriction;

import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.search.query.entity.restriction.MatchMode;
import com.atlassian.crowd.search.query.entity.restriction.Property;

public interface PropertyRestriction<T>
extends SearchRestriction {
    public Property<T> getProperty();

    public MatchMode getMatchMode();

    public T getValue();
}


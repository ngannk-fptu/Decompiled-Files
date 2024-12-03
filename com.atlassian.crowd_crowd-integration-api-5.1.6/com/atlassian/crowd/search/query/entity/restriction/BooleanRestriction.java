/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 */
package com.atlassian.crowd.search.query.entity.restriction;

import com.atlassian.crowd.embedded.api.SearchRestriction;
import java.util.Collection;

public interface BooleanRestriction
extends SearchRestriction {
    public Collection<SearchRestriction> getRestrictions();

    public BooleanLogic getBooleanLogic();

    public static enum BooleanLogic {
        AND,
        OR;

    }
}


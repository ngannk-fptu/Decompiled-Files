/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 */
package com.atlassian.crowd.search.query.entity.restriction;

import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.search.query.entity.restriction.NullRestrictionImpl;

public interface NullRestriction
extends SearchRestriction {
    public static final NullRestriction INSTANCE = NullRestrictionImpl.INSTANCE;
}


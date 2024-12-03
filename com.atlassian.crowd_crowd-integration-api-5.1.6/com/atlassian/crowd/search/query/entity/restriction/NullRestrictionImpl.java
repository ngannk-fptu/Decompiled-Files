/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.search.query.entity.restriction;

import com.atlassian.crowd.search.query.entity.restriction.NullRestriction;

public class NullRestrictionImpl
implements NullRestriction {
    public static final NullRestriction INSTANCE = new NullRestrictionImpl();

    private NullRestrictionImpl() {
    }

    public boolean equals(Object o) {
        return o instanceof NullRestriction;
    }

    public int hashCode() {
        return 1;
    }
}


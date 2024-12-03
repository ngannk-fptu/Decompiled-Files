/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.Period
 */
package com.atlassian.confluence.plugins.edgeindex;

import org.joda.time.Period;

public interface EdgeIndexBuilder {
    public static final Period EDGE_INDEX_REBUILD_DEFAULT_START_PERIOD = Period.weeks((int)2);

    public void rebuild(Period var1, RebuildCondition var2);

    public static enum RebuildCondition {
        FORCE,
        ONLY_IF_INDEX_PRESENT;

    }
}


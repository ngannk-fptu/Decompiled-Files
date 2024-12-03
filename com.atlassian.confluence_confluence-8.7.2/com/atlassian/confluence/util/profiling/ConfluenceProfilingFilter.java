/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.filters.ProfilingFilter
 *  com.atlassian.util.profiling.filters.StatusUpdateStrategy
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.confluence.util.profiling.ProfilingStatusUpdateWithoutRequestStrategy;
import com.atlassian.util.profiling.filters.ProfilingFilter;
import com.atlassian.util.profiling.filters.StatusUpdateStrategy;

public class ConfluenceProfilingFilter
extends ProfilingFilter {
    public ConfluenceProfilingFilter() {
        super((StatusUpdateStrategy)new ProfilingStatusUpdateWithoutRequestStrategy());
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.filters.StatusUpdateStrategy
 *  javax.servlet.ServletRequest
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.util.profiling.filters.StatusUpdateStrategy;
import javax.servlet.ServletRequest;

class ProfilingStatusUpdateWithoutRequestStrategy
implements StatusUpdateStrategy {
    ProfilingStatusUpdateWithoutRequestStrategy() {
    }

    public void setStateViaRequest(ServletRequest request) {
    }
}


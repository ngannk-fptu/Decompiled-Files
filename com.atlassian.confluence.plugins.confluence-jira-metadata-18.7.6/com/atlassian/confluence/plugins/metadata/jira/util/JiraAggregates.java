/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.metadata.jira.util;

import com.atlassian.confluence.plugins.metadata.jira.model.JiraAggregate;

public class JiraAggregates {
    private static final int COUNT_INITIAL = -1;
    private static final int COUNT_TIMED_OUT = 0;

    public static JiraAggregate initial() {
        return new JiraAggregate(-1, true);
    }

    public static JiraAggregate timedOut() {
        return new JiraAggregate(0, true);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.guardrails;

import com.atlassian.migration.agent.store.guardrails.QueryResult;

public interface AssessmentQuery<T extends QueryResult> {
    public String getQueryId();

    public T execute();
}


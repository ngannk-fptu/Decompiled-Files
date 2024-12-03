/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.guardrails;

import com.atlassian.migration.agent.store.guardrails.AssessmentQuery;
import com.atlassian.migration.agent.store.guardrails.L1AssessmentResult;

public interface L1AssessmentQuery<T extends L1AssessmentResult>
extends AssessmentQuery<T> {
    @Override
    public T execute();
}


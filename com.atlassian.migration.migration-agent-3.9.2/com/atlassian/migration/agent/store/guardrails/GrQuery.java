/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.guardrails;

import com.atlassian.migration.agent.store.guardrails.AssessmentQuery;
import com.atlassian.migration.agent.store.guardrails.GrResult;

public interface GrQuery<T extends GrResult>
extends AssessmentQuery<T> {
    @Override
    public T execute();
}


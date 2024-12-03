/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.guardrails;

import com.atlassian.migration.agent.store.guardrails.QueryResult;

public interface GrResult
extends QueryResult {
    public String generateGrResult();
}


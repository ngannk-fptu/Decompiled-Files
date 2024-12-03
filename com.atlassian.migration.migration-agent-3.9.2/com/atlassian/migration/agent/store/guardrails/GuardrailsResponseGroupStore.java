/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.guardrails;

import com.atlassian.migration.agent.entity.GuardrailsResponseGroup;
import java.util.Optional;

public interface GuardrailsResponseGroupStore {
    public String createResponseGroup(GuardrailsResponseGroup var1);

    public void updateResponseGroup(String var1);

    public GuardrailsResponseGroup getResponseGroupByJobId(String var1);

    public Optional<GuardrailsResponseGroup> findLastJobId();
}


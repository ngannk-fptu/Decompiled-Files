/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.guardrails;

import com.atlassian.migration.agent.entity.GuardrailsResponse;
import com.atlassian.migration.agent.entity.GuardrailsResponseGroup;
import java.util.List;
import java.util.Optional;

public interface GuardrailsResponseStore {
    public void createGuardrailsResponse(GuardrailsResponse var1);

    public long getNumberOfQueries(String var1);

    public List<GuardrailsResponse> getResponses(String var1);

    public Optional<GuardrailsResponseGroup> getLatestResponseGroup();
}


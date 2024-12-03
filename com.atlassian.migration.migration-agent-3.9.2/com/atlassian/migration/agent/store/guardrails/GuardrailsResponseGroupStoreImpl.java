/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.guardrails;

import com.atlassian.migration.agent.entity.GuardrailsResponseGroup;
import com.atlassian.migration.agent.store.guardrails.GuardrailsResponseGroupStore;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.Optional;

public class GuardrailsResponseGroupStoreImpl
implements GuardrailsResponseGroupStore {
    private final EntityManagerTemplate tmpl;

    public GuardrailsResponseGroupStoreImpl(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public String createResponseGroup(GuardrailsResponseGroup guardrailsResponseGroup) {
        this.tmpl.persist(guardrailsResponseGroup);
        return guardrailsResponseGroup.getId();
    }

    @Override
    public void updateResponseGroup(String responseGroupId) {
        this.tmpl.query("update GuardrailsResponseGroup gr set gr.endTimestamp=:endTimestamp where gr.id = :responseGroupId").param("endTimestamp", (Object)System.currentTimeMillis()).param("responseGroupId", (Object)responseGroupId).update();
    }

    @Override
    public GuardrailsResponseGroup getResponseGroupByJobId(String jobId) {
        return this.tmpl.query(GuardrailsResponseGroup.class, "select grg from GuardrailsResponseGroup grg where jobId=:jobId").param("jobId", (Object)jobId).single();
    }

    @Override
    public Optional<GuardrailsResponseGroup> findLastJobId() {
        return this.tmpl.query(GuardrailsResponseGroup.class, "select grg from GuardrailsResponseGroup grg order by grg.startTimestamp DESC").first();
    }
}


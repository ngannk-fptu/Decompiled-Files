/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.guardrails;

import com.atlassian.migration.agent.entity.GuardrailsResponse;
import com.atlassian.migration.agent.entity.GuardrailsResponseGroup;
import com.atlassian.migration.agent.store.guardrails.GuardrailsResponseStore;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.List;
import java.util.Optional;

public class GuardrailsResponseStoreImpl
implements GuardrailsResponseStore {
    private final EntityManagerTemplate tmpl;

    public GuardrailsResponseStoreImpl(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public void createGuardrailsResponse(GuardrailsResponse guardrailsResponse) {
        this.tmpl.persist(guardrailsResponse);
    }

    @Override
    public long getNumberOfQueries(String jobId) {
        String query = "select count(gr) from GuardrailsResponse gr join GuardrailsResponseGroup grg on gr.responseGroupId = grg.id where grg.jobId=:jobId";
        Long size = this.tmpl.query(Long.class, query).param("jobId", (Object)jobId).single();
        return size == null ? 0L : size;
    }

    @Override
    public List<GuardrailsResponse> getResponses(String jobId) {
        String query = "select new com.atlassian.migration.agent.entity.GuardrailsResponse(gr.queryResponse, gr.queryId, gr.success, gr.queryStatus) from GuardrailsResponse gr join GuardrailsResponseGroup grg on gr.responseGroupId = grg.id where grg.jobId=:jobId order by gr.queryId";
        List<GuardrailsResponse> responses = this.tmpl.query(GuardrailsResponse.class, query).param("jobId", (Object)jobId).list();
        return responses;
    }

    @Override
    public Optional<GuardrailsResponseGroup> getLatestResponseGroup() {
        String query = "select gr.responseGroup from GuardrailsResponse gr order by gr.id desc";
        return this.tmpl.query(GuardrailsResponseGroup.class, query).first();
    }
}


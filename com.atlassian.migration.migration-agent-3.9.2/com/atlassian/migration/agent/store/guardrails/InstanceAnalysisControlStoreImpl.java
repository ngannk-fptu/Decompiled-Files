/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.guardrails;

import com.atlassian.migration.agent.entity.InstanceAnalysisControl;
import com.atlassian.migration.agent.store.guardrails.InstanceAnalysisControlStore;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.Optional;

public class InstanceAnalysisControlStoreImpl
implements InstanceAnalysisControlStore {
    private final EntityManagerTemplate tmpl;

    public InstanceAnalysisControlStoreImpl(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public boolean isFinished(Long endTimestamp) {
        return endTimestamp != null;
    }

    @Override
    public InstanceAnalysisControl createInstanceAnalysisControl(String analysisType) {
        InstanceAnalysisControl instanceAnalysisControl = new InstanceAnalysisControl(analysisType, System.currentTimeMillis());
        this.tmpl.persist(instanceAnalysisControl);
        return instanceAnalysisControl;
    }

    @Override
    public void updateInstanceAnalysisControl(InstanceAnalysisControl instanceAnalysisControl) {
        this.tmpl.query("update InstanceAnalysisControl iac set iac.endTimestamp=:endTimestamp  where iac.analysisType = :analysisType and iac.startTimestamp = :startTimestamp").param("endTimestamp", (Object)instanceAnalysisControl.getEndTimestamp()).param("analysisType", (Object)instanceAnalysisControl.getAnalysisType()).param("startTimestamp", (Object)instanceAnalysisControl.getStartTimestamp()).update();
    }

    @Override
    public void completeInstanceAnalysisControl(String analysisType) {
        InstanceAnalysisControl instanceAnalysisControl = this.findInstanceAnalysisControl(analysisType).orElseThrow(IllegalStateException::new);
        if (!this.isFinished(instanceAnalysisControl.getEndTimestamp())) {
            instanceAnalysisControl.setEndTimestamp(System.currentTimeMillis());
            this.updateInstanceAnalysisControl(instanceAnalysisControl);
        }
    }

    @Override
    public Optional<InstanceAnalysisControl> findInstanceAnalysisControl(String analysisType) {
        String query = "select iac from InstanceAnalysisControl iac where iac.analysisType=:analysisType order by iac.startTimestamp desc";
        return this.tmpl.query(InstanceAnalysisControl.class, query).param("analysisType", (Object)analysisType).first();
    }
}


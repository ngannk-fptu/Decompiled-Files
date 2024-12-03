/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.migration.agent.entity.AppAssessmentInfo;
import com.atlassian.migration.agent.entity.AttachmentMigration;
import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.entity.Plan;
import com.atlassian.migration.agent.entity.Stats;
import com.atlassian.migration.agent.entity.Step;
import com.atlassian.migration.agent.entity.Task;
import com.atlassian.migration.agent.store.impl.AppAssessmentInfoStore;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import com.atlassian.migration.agent.testsupport.BackdoorService;
import com.atlassian.migration.agent.testsupport.appassessment.AppAssessmentInfoDTO;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DefaultBackdoorService
implements BackdoorService {
    private final PluginTransactionTemplate ptx;
    private final EntityManagerTemplate entityManagerTemplate;
    private final AppAssessmentInfoStore appInfoStore;

    public DefaultBackdoorService(PluginTransactionTemplate ptx, EntityManagerTemplate entityManagerTemplate, AppAssessmentInfoStore appInfoStore) {
        this.ptx = ptx;
        this.entityManagerTemplate = entityManagerTemplate;
        this.appInfoStore = appInfoStore;
    }

    @Override
    public void reset() {
        this.ptx.write(() -> Stream.of(Stats.class, Step.class, Task.class, Plan.class, AttachmentMigration.class, CloudSite.class, AppAssessmentInfo.class).forEach(entityType -> this.entityManagerTemplate.query("delete from " + entityType.getName()).update()));
    }

    @Override
    public List<AppAssessmentInfoDTO> getAppAssessmentEntries() {
        return this.appInfoStore.getAll().stream().map(info -> new AppAssessmentInfoDTO(info.getAppKey(), info.getMigrationStatus().name(), info.getMigrationNotes(), info.getAlternativeAppKey())).collect(Collectors.toList());
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.migration.agent;

import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.CommonBeanConfiguration;
import com.atlassian.migration.agent.ImportedOsgiServiceBeans;
import com.atlassian.migration.agent.StoreBeanConfiguration;
import com.atlassian.migration.agent.service.planning.ConfSpacePlanningEngine;
import com.atlassian.migration.agent.service.planning.GlobalEntitiesMigrationPlanningEngine;
import com.atlassian.migration.agent.service.planning.SpaceAttachmentsStepPlanningEngine;
import com.atlassian.migration.agent.service.planning.TaskPlanningEngine;
import com.atlassian.migration.agent.service.planning.UsersMigrationPlanningEngine;
import com.atlassian.migration.agent.store.TaskStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(value={ImportedOsgiServiceBeans.class, CommonBeanConfiguration.class, StoreBeanConfiguration.class})
@Configuration
public class PlanningEngineBeanConfiguration {
    @Bean
    public GlobalEntitiesMigrationPlanningEngine globalEntitiesMigrationPlanningEngine() {
        return new GlobalEntitiesMigrationPlanningEngine();
    }

    @Bean
    public ConfSpacePlanningEngine confSpacePlanningEngine(MigrationDarkFeaturesManager migrationDarkFeaturesManager) {
        return new ConfSpacePlanningEngine(migrationDarkFeaturesManager);
    }

    @Bean
    public SpaceAttachmentsStepPlanningEngine spaceAttachmentsStepPlanningEngine() {
        return new SpaceAttachmentsStepPlanningEngine();
    }

    @Bean
    public UsersMigrationPlanningEngine usersMigrationPlanningEngine() {
        return new UsersMigrationPlanningEngine();
    }

    @Bean
    public TaskPlanningEngine taskPlanningEngine(TaskStore taskStore) {
        return new TaskPlanningEngine(taskStore);
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.scheduler.SchedulerService
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.upm.spring;

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.upm.UpmInformation;
import com.atlassian.upm.UpmSchedulerUpgradeTask;
import com.atlassian.upm.UserSettingsStore;
import com.atlassian.upm.log.AuditLogUpgradeTask;
import com.atlassian.upm.upgrade.UserSettingsUpgradeTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UpgradeTaskBeans {
    @Bean
    public AuditLogUpgradeTask auditLogUpgradeTask(PluginSettingsFactory pluginSettingsFactory, UpmInformation upm) {
        return new AuditLogUpgradeTask(pluginSettingsFactory, upm);
    }

    @Bean
    public UpmSchedulerUpgradeTask upmSchedulerUpgradeTask(SchedulerService pluginScheduler) {
        return new UpmSchedulerUpgradeTask(pluginScheduler);
    }

    @Bean
    public UserSettingsUpgradeTask userSettingsUpgradeTask(PluginSettingsFactory pluginSettingsFactory, UpmInformation upm, UserSettingsStore userSettingsStore) {
        return new UserSettingsUpgradeTask(pluginSettingsFactory, upm, userSettingsStore);
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.plugin.spring.AvailableToPlugins
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.impl.admin;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.admin.AdminTasklistManager;
import com.atlassian.confluence.admin.DefaultAdminTasklistManager;
import com.atlassian.confluence.admin.criteria.AdminConfigurationCriteria;
import com.atlassian.confluence.admin.criteria.BackupsAreManualCriteria;
import com.atlassian.confluence.admin.criteria.BaseURLIsValidCriteria;
import com.atlassian.confluence.admin.criteria.IgnorableAdminTaskCriteria;
import com.atlassian.confluence.admin.criteria.MoreThanOneUserCriteria;
import com.atlassian.confluence.admin.criteria.RemigrationAdminTaskCriteria;
import com.atlassian.confluence.admin.tasks.AdminTaskConfig;
import com.atlassian.confluence.admin.tasks.DefaultAdminTaskConfig;
import com.atlassian.confluence.content.render.xhtml.migration.macro.MacroMigrationService;
import com.atlassian.confluence.schedule.managers.ScheduledJobManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.UserChecker;
import com.atlassian.plugin.spring.AvailableToPlugins;
import com.atlassian.plugin.web.WebInterfaceManager;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminTaskListConfig {
    @Resource
    private SettingsManager settingsManager;
    @Resource
    private BandanaManager bandanaManager;
    @Resource
    private WebInterfaceManager webInterfaceManager;
    @Resource
    private UserChecker userChecker;
    @Resource
    private ScheduledJobManager scheduledJobManager;
    @Resource
    private MacroMigrationService macroMigrationService;
    @Resource
    private AdminConfigurationCriteria mailServerExistsCriteria;

    @Bean
    @AvailableToPlugins
    AdminTasklistManager adminTasklistManager() {
        return new DefaultAdminTasklistManager(this.bandanaManager, this.getAdminTaskConfigs(), true, this.settingsManager, this.webInterfaceManager);
    }

    private List<AdminTaskConfig> getAdminTaskConfigs() {
        return Arrays.asList(this.reviewBaseURLAdminTask(), this.configureMailServerAdminTask(), this.addSomeUsersAdminTask(), this.manualBackupsAdminTask(), this.remigrationAdminTask());
    }

    private AdminTaskConfig reviewBaseURLAdminTask() {
        return new DefaultAdminTaskConfig("review.baseurl", (AdminConfigurationCriteria)new BaseURLIsValidCriteria(this.settingsManager), "/admin/editgeneralconfig.action?autofocus=editbaseurl");
    }

    private AdminTaskConfig addSomeUsersAdminTask() {
        return new DefaultAdminTaskConfig("add.users", (AdminConfigurationCriteria)new IgnorableAdminTaskCriteria("add.more.users", this.settingsManager, new MoreThanOneUserCriteria(this.userChecker)), Arrays.asList("/admin/users/browseusers.action", "/plugins/servlet/embedded-crowd/directories/list"));
    }

    private AdminTaskConfig manualBackupsAdminTask() {
        return new DefaultAdminTaskConfig("manual.backups", (AdminConfigurationCriteria)new BackupsAreManualCriteria(this.settingsManager, this.scheduledJobManager), "/admin/scheduledjobs/viewscheduledjobs.action");
    }

    private AdminTaskConfig remigrationAdminTask() {
        return new DefaultAdminTaskConfig("remigration.xhtml", (AdminConfigurationCriteria)new RemigrationAdminTaskCriteria(this.macroMigrationService), "/admin/unmigratedcontent.action");
    }

    private AdminTaskConfig configureMailServerAdminTask() {
        return new DefaultAdminTaskConfig("configure.mail", this.mailServerExistsCriteria, "/admin/mail/viewmailservers.action");
    }
}


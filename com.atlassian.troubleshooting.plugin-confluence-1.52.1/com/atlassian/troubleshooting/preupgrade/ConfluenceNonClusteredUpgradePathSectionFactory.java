/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.metadata.PluginMetadataManager
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.preupgrade;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.metadata.PluginMetadataManager;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.troubleshooting.healthcheck.model.DbType;
import com.atlassian.troubleshooting.preupgrade.AbstractUpgradePathSectionFactory;
import com.atlassian.troubleshooting.preupgrade.AnalyticsKey;
import com.atlassian.troubleshooting.preupgrade.accessors.PupPlatformAccessor;
import com.atlassian.troubleshooting.preupgrade.model.MicroservicePreUpgradeDataDTO;
import com.atlassian.troubleshooting.preupgrade.model.PreUpgradeInfoDto;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.util.RendererUtils;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class ConfluenceNonClusteredUpgradePathSectionFactory
extends AbstractUpgradePathSectionFactory {
    private static final String CLEAR_CONFLUENCE_PLUGINS_CACHE_URL = "https://confluence.atlassian.com/x/TgG_EQ";

    @Autowired
    public ConfluenceNonClusteredUpgradePathSectionFactory(I18nResolver i18n, SupportApplicationInfo supportApplicationInfo, PupPlatformAccessor pupPlatformAccessor, PluginMetadataManager pluginMetadataManager, PluginAccessor pluginAccessor) {
        super(i18n, supportApplicationInfo, pupPlatformAccessor, pluginMetadataManager, pluginAccessor, "conf", false, "upgrade-path-conf", "https://docs.atlassian.com/confluence/docs-%s%s/%s");
    }

    @Override
    public List<PreUpgradeInfoDto.Version.UpgradePathSection> getSections(MicroservicePreUpgradeDataDTO.Version targetVersion, boolean isZduAvailable) {
        return ImmutableList.of((Object)this.createChooseAnUpgradeMethodSection(targetVersion), (Object)this.createUpgradeInATestEnvironmentSection(targetVersion), (Object)this.createUpgradeInProductionSection(targetVersion), (Object)this.createPerformPostUpgradeActivitiesSection());
    }

    @VisibleForTesting
    protected PreUpgradeInfoDto.Version.UpgradePathSection createChooseAnUpgradeMethodSection(MicroservicePreUpgradeDataDTO.Version targetVersion) {
        return new AbstractUpgradePathSectionFactory.SectionBuilder("2", AnalyticsKey.UPGRADE_METHOD, "choose-upgrade-method", new Serializable[0]).descriptionArgs(this.supportApplicationInfo.getApplicationName(), targetVersion.getUpgradeInstructionsUrl()).build();
    }

    @VisibleForTesting
    protected PreUpgradeInfoDto.Version.UpgradePathSection createUpgradeInATestEnvironmentSection(MicroservicePreUpgradeDataDTO.Version targetVersion) {
        String productDisplayName = this.supportApplicationInfo.getApplicationName();
        return new AbstractUpgradePathSectionFactory.SectionBuilder("3", AnalyticsKey.TEST_UPGRADE, "upgrade-in-test-env", new Serializable[]{productDisplayName}).descriptionArgs(productDisplayName).step("create-staging-env", new Serializable[]{RendererUtils.renderLink(targetVersion.getStagingEnvironmentInstructionsUrl(), this.i18n.getText("stp.pup.default.help.link.title"))}).step(this.hasApps(), "check-app-compatibility", new Serializable[]{this.getWebContext() + "/plugins/servlet/upm/check?source=manage", productDisplayName}).step("backup-your-data", new Serializable[0]).step("download-product", new Serializable[]{productDisplayName}).step("stop", new Serializable[]{productDisplayName}).step("run-installer-or-upgrade-manually", new Serializable[0]).step(this.isDbType(DbType.oracle), "install-oracle-driver", new Serializable[]{this.createDatabaseJDBCDriversUrl(targetVersion)}).step(this.isDbType(DbType.mysql), "install-mysql-driver", new Serializable[]{this.createDatabaseJDBCDriversUrl(targetVersion)}).step("windows-service", new Serializable[0]).step(this.getModificationCount() > 0 && this.getModificationCount() < 6, "reapply-modifications", new Serializable[]{this.renderModificationList()}).step(this.getModificationCount() >= 6, "reapply-modifications-button", new Serializable[0]).step("start-application", new Serializable[]{productDisplayName}).step("update-your-apps", new Serializable[]{this.getWebContext() + "/plugins/servlet/upm"}).step("stop-after-update", new Serializable[]{productDisplayName}).step("clear-plugin-cache", new Serializable[]{CLEAR_CONFLUENCE_PLUGINS_CACHE_URL}).step("start-after-update", new Serializable[]{productDisplayName}).step("uat", new Serializable[0]).step("companion-app", new Serializable[]{this.createCompanionAppUrl(targetVersion)}).addSuffix(this.i18n.getText("stp.pup.test.upgrade.tip")).build();
    }

    @VisibleForTesting
    protected PreUpgradeInfoDto.Version.UpgradePathSection createUpgradeInProductionSection(MicroservicePreUpgradeDataDTO.Version targetVersion) {
        String productDisplayName = this.supportApplicationInfo.getApplicationName();
        return new AbstractUpgradePathSectionFactory.SectionBuilder("4", AnalyticsKey.PRODUCTION_UPGRADE, "upgrade-in-prod", new Serializable[]{productDisplayName}).descriptionArgs(productDisplayName).step("schedule-downtime", new Serializable[0]).step(this.hasApps(), "check-app-compatibility", new Serializable[]{this.getWebContext() + "/plugins/servlet/upm/check?source=manage", productDisplayName}).step("backup-your-data", new Serializable[0]).step("download-product", new Serializable[]{productDisplayName}).step("stop", new Serializable[]{productDisplayName}).step("run-installer-or-upgrade-manually", new Serializable[0]).step(this.isDbType(DbType.oracle), "install-oracle-driver", new Serializable[]{this.createDatabaseJDBCDriversUrl(targetVersion)}).step(this.isDbType(DbType.mysql), "install-mysql-driver", new Serializable[]{this.createDatabaseJDBCDriversUrl(targetVersion)}).step("windows-service", new Serializable[0]).step(this.getModificationCount() > 0 && this.getModificationCount() < 6, "reapply-modifications", new Serializable[]{this.renderModificationList()}).step(this.getModificationCount() >= 6, "reapply-modifications-button", new Serializable[0]).step("start-application", new Serializable[]{productDisplayName}).step("update-your-apps", new Serializable[]{this.getWebContext() + "/plugins/servlet/upm"}).step("stop-after-update", new Serializable[]{productDisplayName}).step("clear-plugin-cache", new Serializable[]{CLEAR_CONFLUENCE_PLUGINS_CACHE_URL}).step("start-after-update", new Serializable[]{productDisplayName}).step("uat", new Serializable[0]).step("companion-app", new Serializable[]{this.createCompanionAppUrl(targetVersion)}).build();
    }

    private String createCompanionAppUrl(MicroservicePreUpgradeDataDTO.Version targetVersion) {
        return this.createDocsUrl("Atlassian+Companion+app+release+notes", targetVersion.getVersion().getMajor(), targetVersion.getVersion().getMinor());
    }

    private String createDatabaseJDBCDriversUrl(MicroservicePreUpgradeDataDTO.Version targetVersion) {
        return this.createDocsUrl("Database+JDBC+Drivers", targetVersion.getVersion().getMajor(), targetVersion.getVersion().getMinor());
    }

    @VisibleForTesting
    protected PreUpgradeInfoDto.Version.UpgradePathSection createPerformPostUpgradeActivitiesSection() {
        return new AbstractUpgradePathSectionFactory.SectionBuilder("5", AnalyticsKey.POST_UPGRADE, "post-upgrade-activities", new Serializable[0]).build();
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.metadata.PluginMetadataManager
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.preupgrade;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.metadata.PluginMetadataManager;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.troubleshooting.healthcheck.model.DbType;
import com.atlassian.troubleshooting.preupgrade.AbstractUpgradePathSectionFactory;
import com.atlassian.troubleshooting.preupgrade.AnalyticsKey;
import com.atlassian.troubleshooting.preupgrade.accessors.ConfluencePupPlatformAccessor;
import com.atlassian.troubleshooting.preupgrade.model.MicroservicePreUpgradeDataDTO;
import com.atlassian.troubleshooting.preupgrade.model.PreUpgradeInfoDto;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.spi.Version;
import com.atlassian.troubleshooting.util.RendererUtils;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class ConfluenceClusteredUpgradePathSectionFactory
extends AbstractUpgradePathSectionFactory<ConfluencePupPlatformAccessor> {
    private static final String CLEAR_CONFLUENCE_PLUGINS_CACHE_URL = "https://confluence.atlassian.com/x/TgG_EQ";
    private static final String ZDU_PAGE_URL = "/plugins/servlet/zdu";

    @Autowired
    protected ConfluenceClusteredUpgradePathSectionFactory(I18nResolver i18n, SupportApplicationInfo supportApplicationInfo, ConfluencePupPlatformAccessor pupPlatformAccessor, PluginMetadataManager pluginMetadataManager, PluginAccessor pluginAccessor) {
        super(i18n, supportApplicationInfo, pupPlatformAccessor, pluginMetadataManager, pluginAccessor, "conf", true, "upgrade-path-conf-dc", "https://docs.atlassian.com/confluence/docs-%s%s/%s");
    }

    @Override
    public List<PreUpgradeInfoDto.Version.UpgradePathSection> getSections(MicroservicePreUpgradeDataDTO.Version targetVersion, boolean isZduAvailable) {
        ImmutableList.Builder builder = ImmutableList.builder().add((Object[])new PreUpgradeInfoDto.Version.UpgradePathSection[]{this.createChooseAnUpgradeMethodSection(isZduAvailable), this.createUpgradeInATestEnvironmentSection(targetVersion, isZduAvailable)});
        if (isZduAvailable) {
            builder.add((Object)this.createZduUpgradeInTestSection(targetVersion));
        }
        builder.add((Object)this.createUpgradeInProductionSection(targetVersion, isZduAvailable));
        if (isZduAvailable) {
            builder.add((Object)this.createZduUpgradeInProductionSection(targetVersion));
        }
        builder.add((Object)this.createPerformPostUpgradeActivitiesSection());
        return builder.build();
    }

    @VisibleForTesting
    protected PreUpgradeInfoDto.Version.UpgradePathSection createChooseAnUpgradeMethodSection(boolean isZduAvailable) {
        return new AbstractUpgradePathSectionFactory.SectionBuilder(this, "2", AnalyticsKey.UPGRADE_METHOD, "choose-upgrade-method", new Serializable[0]).descriptionArgs(this.supportApplicationInfo.getApplicationName(), isZduAvailable ? "<br><br>" + this.i18n.getText("stp.pup.dc-upgrade-path.description.zero-downtime-paragraph", new Serializable[]{this.supportApplicationInfo.getApplicationName()}) : "").build();
    }

    @VisibleForTesting
    protected PreUpgradeInfoDto.Version.UpgradePathSection createUpgradeInATestEnvironmentSection(MicroservicePreUpgradeDataDTO.Version targetVersion, boolean isZduAvailable) {
        String productDisplayName = this.supportApplicationInfo.getApplicationName();
        AbstractUpgradePathSectionFactory.SectionBuilder builder = new AbstractUpgradePathSectionFactory.SectionBuilder(this, isZduAvailable ? "3a" : "3", AnalyticsKey.TEST_UPGRADE, "upgrade-in-test-env", new Serializable[]{productDisplayName}).descriptionArgs(productDisplayName).step("create-staging-env", new Serializable[]{RendererUtils.renderLink(targetVersion.getStagingEnvironmentInstructionsUrl(), this.i18n.getText("stp.pup.default.help.link.title"))}).step(this.hasApps(), "check-app-compatibility", new Serializable[]{this.getWebContext() + "/plugins/servlet/upm/check?source=manage", productDisplayName}).step("stop-nodes", new Serializable[]{productDisplayName}).step("backup-your-data", new Serializable[0]);
        builder.startNewSubSection("first-node-in-test", new Serializable[0]).step("download-product", new Serializable[]{productDisplayName}).step("run-installer-or-upgrade-manually", new Serializable[0]).step("point-to-local-home", new Serializable[0]).step(this.isDbType(DbType.oracle), "install-oracle-driver", new Serializable[]{this.createDatabaseJDBCDriversUrl(targetVersion)}).step(this.isDbType(DbType.mysql), "install-mysql-driver", new Serializable[]{this.createDatabaseJDBCDriversUrl(targetVersion)}).step(this.getModificationCount() > 0 && this.getModificationCount() < 6, "reapply-modifications", new Serializable[]{this.renderModificationList()}).step(this.getModificationCount() >= 6, "reapply-modifications-button", new Serializable[0]).step("start-application", new Serializable[]{productDisplayName}).step(this.hasApps(), "update-your-apps", new Serializable[]{this.getWebContext() + "/plugins/servlet/upm"}).step("stop-after-update", new Serializable[]{productDisplayName}).step("clear-plugin-cache", new Serializable[]{CLEAR_CONFLUENCE_PLUGINS_CACHE_URL});
        this.addSynchronyStandaloneSection(builder, productDisplayName);
        builder.startNewSubSection("remaining-nodes-in-test", new Serializable[0]).step("copy-directories", new Serializable[0]).step("start-in-second-node", new Serializable[]{productDisplayName}).step("stop-for-nodes", new Serializable[]{productDisplayName}).step("repeat-remaining-nodes", new Serializable[0]).step("start-on-each-node", new Serializable[]{productDisplayName}).step("cluster-monitoring", new Serializable[]{this.getWebContext() + "/plugins/servlet/cluster-monitoring"}).step("uat", new Serializable[0]).step(this.localProductVersionIsEqualToOrLaterThan(6, 11), "companion-app", new Serializable[]{this.createCompanionAppUrl(targetVersion)}).startNewSubSection("tip", new Serializable[0]);
        return builder.build();
    }

    @VisibleForTesting
    protected PreUpgradeInfoDto.Version.UpgradePathSection createZduUpgradeInTestSection(MicroservicePreUpgradeDataDTO.Version targetVersion) {
        String productDisplayName = this.supportApplicationInfo.getApplicationName();
        AbstractUpgradePathSectionFactory.SectionBuilder builder = new AbstractUpgradePathSectionFactory.SectionBuilder(this, "3b", AnalyticsKey.TEST_UPGRADE, "zdu-upgrade-in-test", new Serializable[]{productDisplayName}).descriptionArgs(productDisplayName).step("create-staging-env", new Serializable[]{RendererUtils.renderLink(targetVersion.getStagingEnvironmentInstructionsUrl(), this.i18n.getText("stp.pup.default.help.link.title"))}).step(this.hasApps(), "check-app-compatibility", new Serializable[]{this.getWebContext() + "/plugins/servlet/upm/check?source=manage", productDisplayName}).step("backup-your-data", new Serializable[0]).step("upgrade-mode", new Serializable[0]);
        this.addFirstNodeSection(builder, targetVersion, productDisplayName, true);
        this.addSynchronyStandaloneSection(builder, productDisplayName);
        this.addZduRemainingNodesSection(builder);
        this.addZduFinalizeSection(builder, targetVersion);
        builder.startNewSubSection("tip", new Serializable[0]);
        return builder.build();
    }

    private String createCompanionAppUrl(MicroservicePreUpgradeDataDTO.Version targetVersion) {
        return this.createDocsUrl("Atlassian+Companion+app+release+notes", targetVersion.getVersion().getMajor(), targetVersion.getVersion().getMinor());
    }

    private String createDatabaseJDBCDriversUrl(MicroservicePreUpgradeDataDTO.Version targetVersion) {
        return this.createDocsUrl("Database+JDBC+Drivers", targetVersion.getVersion().getMajor(), targetVersion.getVersion().getMinor());
    }

    private boolean localProductVersionIsEqualToOrLaterThan(int major, int minor) {
        Version current = Version.of(((ConfluencePupPlatformAccessor)this.pupPlatformAccessor).getVersion());
        int currentMajor = current.getMajor();
        int currentMinor = current.getMinor();
        return currentMajor > major || currentMajor == major && currentMinor >= minor;
    }

    @VisibleForTesting
    protected PreUpgradeInfoDto.Version.UpgradePathSection createZduUpgradeInProductionSection(MicroservicePreUpgradeDataDTO.Version targetVersion) {
        String productDisplayName = this.supportApplicationInfo.getApplicationName();
        AbstractUpgradePathSectionFactory.SectionBuilder builder = new AbstractUpgradePathSectionFactory.SectionBuilder(this, "4b", AnalyticsKey.PRODUCTION_UPGRADE, "zdu-upgrade-in-prod", new Serializable[]{productDisplayName}).descriptionArgs(productDisplayName).step(this.hasApps(), "check-app-compatibility", new Serializable[]{this.getWebContext() + "/plugins/servlet/upm/check?source=manage", productDisplayName}).step("backup-your-data", new Serializable[0]).step("upgrade-mode", new Serializable[]{this.getWebContext() + ZDU_PAGE_URL});
        this.addFirstNodeSection(builder, targetVersion, productDisplayName, true);
        this.addSynchronyStandaloneSection(builder, productDisplayName);
        this.addZduRemainingNodesSection(builder);
        this.addZduFinalizeSection(builder, targetVersion);
        return builder.build();
    }

    private void addZduRemainingNodesSection(AbstractUpgradePathSectionFactory.SectionBuilder builder) {
        builder.startNewSubSection("zdu-remaining-nodes", new Serializable[0]).step("stop-second-node", new Serializable[0]).step("copy-directories", new Serializable[0]).step("start-in-second-node", new Serializable[0]).step("check-node-rejoined", new Serializable[]{this.getWebContext() + ZDU_PAGE_URL}).step("repeat-remaining-nodes", new Serializable[0]);
    }

    private void addZduFinalizeSection(AbstractUpgradePathSectionFactory.SectionBuilder builder, MicroservicePreUpgradeDataDTO.Version targetVersion) {
        builder.startNewSubSection("after-all-nodes", new Serializable[0]).step("finalize", new Serializable[]{this.getWebContext() + ZDU_PAGE_URL}).step(this.hasApps(), "update-your-apps", new Serializable[]{this.getWebContext() + "/plugins/servlet/upm"}).step("companion-app", new Serializable[]{this.createCompanionAppUrl(targetVersion)});
    }

    private void addFirstNodeSection(AbstractUpgradePathSectionFactory.SectionBuilder builder, MicroservicePreUpgradeDataDTO.Version targetVersion, String productDisplayName, boolean isZduVersion) {
        builder.startNewSubSection("first-node-in-prod", new Serializable[0]).step("download-product", new Serializable[]{productDisplayName}).step("run-installer-or-upgrade-manually", new Serializable[0]).step("point-to-local-home", new Serializable[0]).step(this.isDbType(DbType.oracle), "install-oracle-driver", new Serializable[]{this.createDatabaseJDBCDriversUrl(targetVersion)}).step(this.isDbType(DbType.mysql), "install-mysql-driver", new Serializable[]{this.createDatabaseJDBCDriversUrl(targetVersion)}).step(this.getModificationCount() > 0 && this.getModificationCount() < 6, "reapply-modifications", new Serializable[]{this.renderModificationList()}).step(this.getModificationCount() >= 6, "reapply-modifications-button", new Serializable[0]).step("start-application", new Serializable[]{productDisplayName});
        if (!isZduVersion) {
            builder.step(this.hasApps(), "update-your-apps", new Serializable[]{this.getWebContext() + "/plugins/servlet/upm"}).step("stop-after-update", new Serializable[]{productDisplayName}).step("clear-plugin-cache", new Serializable[]{CLEAR_CONFLUENCE_PLUGINS_CACHE_URL});
        } else {
            builder.step("check-node-rejoined", new Serializable[]{this.getWebContext() + ZDU_PAGE_URL});
        }
    }

    private void addSynchronyStandaloneSection(AbstractUpgradePathSectionFactory.SectionBuilder builder, String productDisplayName) {
        builder.startNewSubSection(((ConfluencePupPlatformAccessor)this.pupPlatformAccessor).isSynchronyStandalone(), "upgrade-synchrony-in-prod", new Serializable[0]).step(((ConfluencePupPlatformAccessor)this.pupPlatformAccessor).isSynchronyStandalone(), "get-new-standalone", new Serializable[]{productDisplayName}).step(((ConfluencePupPlatformAccessor)this.pupPlatformAccessor).isSynchronyStandalone(), "copy-standalone", new Serializable[0]);
    }

    @VisibleForTesting
    protected PreUpgradeInfoDto.Version.UpgradePathSection createUpgradeInProductionSection(MicroservicePreUpgradeDataDTO.Version targetVersion, boolean isZduAvailable) {
        String productDisplayName = this.supportApplicationInfo.getApplicationName();
        AbstractUpgradePathSectionFactory.SectionBuilder builder = new AbstractUpgradePathSectionFactory.SectionBuilder(this, isZduAvailable ? "4a" : "4", AnalyticsKey.PRODUCTION_UPGRADE, "upgrade-in-prod", new Serializable[]{productDisplayName}).descriptionArgs(productDisplayName).step("schedule-downtime", new Serializable[0]).step(this.hasApps(), "check-app-compatibility", new Serializable[]{this.getWebContext() + "/plugins/servlet/upm/check?source=manage", productDisplayName}).step("stop-nodes", new Serializable[]{productDisplayName}).step("backup-your-data", new Serializable[0]);
        this.addFirstNodeSection(builder, targetVersion, productDisplayName, false);
        this.addSynchronyStandaloneSection(builder, productDisplayName);
        builder.startNewSubSection("remaining-nodes-in-prod", new Serializable[0]).step("copy-directories", new Serializable[0]).step("start-in-second-node", new Serializable[]{productDisplayName}).step("stop-for-nodes", new Serializable[]{productDisplayName}).step("repeat-remaining-nodes", new Serializable[0]).step("start-on-each-node", new Serializable[]{productDisplayName}).step("cluster-monitoring", new Serializable[]{this.getWebContext() + "/plugins/servlet/cluster-monitoring"}).step("uat", new Serializable[0]).step(this.localProductVersionIsEqualToOrLaterThan(6, 11), "companion-app", new Serializable[]{this.createCompanionAppUrl(targetVersion)});
        return builder.build();
    }

    @VisibleForTesting
    protected PreUpgradeInfoDto.Version.UpgradePathSection createPerformPostUpgradeActivitiesSection() {
        return new AbstractUpgradePathSectionFactory.SectionBuilder(this, "5", AnalyticsKey.POST_UPGRADE, "post-upgrade-activities", new Serializable[0]).build();
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.metadata.PluginMetadataManager
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.troubleshooting.preupgrade;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.metadata.PluginMetadataManager;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.troubleshooting.healthcheck.model.DbType;
import com.atlassian.troubleshooting.preupgrade.AnalyticsKey;
import com.atlassian.troubleshooting.preupgrade.UpgradePathSectionFactory;
import com.atlassian.troubleshooting.preupgrade.accessors.PupPlatformAccessor;
import com.atlassian.troubleshooting.preupgrade.model.PreUpgradeInfoDto;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractUpgradePathSectionFactory<T extends PupPlatformAccessor>
implements UpgradePathSectionFactory {
    protected final I18nResolver i18n;
    protected final SupportApplicationInfo supportApplicationInfo;
    protected final T pupPlatformAccessor;
    protected final PluginMetadataManager pluginMetadataManager;
    protected final PluginAccessor pluginAccessor;
    private final String platformId;
    private final boolean isClustered;
    private final String i18nPrefix;
    private final String upgradeDocsUrlTemplate;

    protected AbstractUpgradePathSectionFactory(I18nResolver i18n, SupportApplicationInfo supportApplicationInfo, T pupPlatformAccessor, PluginMetadataManager pluginMetadataManager, PluginAccessor pluginAccessor, String platformId, boolean isClustered, String i18nPrefix, String upgradeDocsUrlTemplate) {
        this.i18n = Objects.requireNonNull(i18n);
        this.supportApplicationInfo = Objects.requireNonNull(supportApplicationInfo);
        this.pupPlatformAccessor = (PupPlatformAccessor)Objects.requireNonNull(pupPlatformAccessor);
        this.pluginMetadataManager = Objects.requireNonNull(pluginMetadataManager);
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor);
        this.platformId = Objects.requireNonNull(platformId);
        this.isClustered = isClustered;
        this.i18nPrefix = Objects.requireNonNull(i18nPrefix);
        this.upgradeDocsUrlTemplate = Objects.requireNonNull(upgradeDocsUrlTemplate);
    }

    @Override
    public String getPlatformId() {
        return this.platformId;
    }

    @Override
    public boolean isClustered() {
        return this.isClustered;
    }

    protected String getWebContext() {
        return this.supportApplicationInfo.getBaseURL(UrlMode.RELATIVE);
    }

    protected boolean hasApps() {
        return this.pluginAccessor.getEnabledPlugins().stream().anyMatch(arg_0 -> ((PluginMetadataManager)this.pluginMetadataManager).isUserInstalled(arg_0));
    }

    protected String renderModificationList() {
        return this.pupPlatformAccessor.getModifiedFiles().map(m -> m.hasModifications() ? this.renderModifications(m.getNamesOfModifiedFiles()) : "").orElse(this.i18n.getText("stp.pup.no-modification-data-available"));
    }

    private String renderModifications(List<String> files) {
        if (files.isEmpty()) {
            return "";
        }
        return "<ul><li>" + StringUtils.join(files, (String)"</li><li>") + "</li></ul>";
    }

    protected int getModificationCount() {
        return this.pupPlatformAccessor.getModifiedFiles().map(m -> m.getModifiedFiles().size()).orElse(0);
    }

    protected boolean isDbType(DbType dbType) {
        return this.pupPlatformAccessor.getCurrentDbPlatform().map(p -> p.getDbType() == dbType).orElse(false);
    }

    protected String createDocsUrl(String pageName, int majorVersion, int minorVersion) {
        return String.format(this.upgradeDocsUrlTemplate, majorVersion, minorVersion, pageName);
    }

    protected class SectionBuilder {
        private final String sectionNumber;
        private final AnalyticsKey analyticsKey;
        private final String sectionKey;
        private final Serializable[] titleArgs;
        private final List<String> steps = new ArrayList<String>();
        private final List<PreUpgradeInfoDto.Version.UpgradePathSection.UpgradePathSubSection> subSections = new ArrayList<PreUpgradeInfoDto.Version.UpgradePathSection.UpgradePathSubSection>();
        private String subSectionKey;
        private Serializable[] descriptionArgs = new String[0];
        private String suffix = null;

        SectionBuilder(String sectionNumber, AnalyticsKey analyticsKey, String sectionKey, Serializable ... titleArgs) {
            this.sectionNumber = sectionNumber;
            this.analyticsKey = Objects.requireNonNull(analyticsKey);
            this.sectionKey = Objects.requireNonNull(sectionKey);
            this.titleArgs = Objects.requireNonNull(titleArgs);
            this.subSectionKey = Objects.requireNonNull(sectionKey);
        }

        SectionBuilder step(String stepKey, Serializable ... args) {
            this.steps.add(AbstractUpgradePathSectionFactory.this.i18n.getText("stp.pup." + AbstractUpgradePathSectionFactory.this.i18nPrefix + "." + this.subSectionKey + "." + stepKey, args));
            return this;
        }

        SectionBuilder step(boolean applicable, String key, Serializable ... args) {
            if (applicable) {
                this.step(key, args);
            }
            return this;
        }

        SectionBuilder descriptionArgs(String ... args) {
            this.descriptionArgs = args;
            return this;
        }

        SectionBuilder startNewSubSection(String newSubSectionKey, Serializable ... newDescriptionArgs) {
            this.endCurrentSubSection();
            this.subSectionKey = Objects.requireNonNull(newSubSectionKey);
            this.descriptionArgs = newDescriptionArgs;
            this.steps.clear();
            return this;
        }

        SectionBuilder startNewSubSection(boolean applicable, String newSubSectionKey, Serializable ... newDescriptionArgs) {
            if (applicable) {
                this.startNewSubSection(newSubSectionKey, newDescriptionArgs);
            }
            return this;
        }

        SectionBuilder addSuffix(String suffix) {
            this.suffix = suffix;
            return this;
        }

        private void endCurrentSubSection() {
            this.subSections.add(new PreUpgradeInfoDto.Version.UpgradePathSection.UpgradePathSubSection(AbstractUpgradePathSectionFactory.this.i18n.getText("stp.pup." + AbstractUpgradePathSectionFactory.this.i18nPrefix + ".description." + this.subSectionKey, this.descriptionArgs), this.steps.stream().filter(Objects::nonNull).collect(Collectors.toList())));
        }

        PreUpgradeInfoDto.Version.UpgradePathSection build() {
            this.endCurrentSubSection();
            return new PreUpgradeInfoDto.Version.UpgradePathSection(this.analyticsKey, this.sectionNumber + ". " + AbstractUpgradePathSectionFactory.this.i18n.getText("stp.pup." + AbstractUpgradePathSectionFactory.this.i18nPrefix + ".title." + this.sectionKey, this.titleArgs), this.subSections, this.suffix);
        }
    }
}


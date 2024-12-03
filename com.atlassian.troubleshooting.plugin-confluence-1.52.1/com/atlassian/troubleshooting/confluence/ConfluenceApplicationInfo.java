/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.languages.Language
 *  com.atlassian.confluence.languages.LanguageManager
 *  com.atlassian.confluence.license.LicenseService
 *  com.atlassian.confluence.logging.ConfluenceHomeLogAppender
 *  com.atlassian.confluence.plugins.synchrony.api.SynchronyEnv
 *  com.atlassian.confluence.plugins.synchrony.api.SynchronyProcessManager
 *  com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager
 *  com.atlassian.confluence.setup.BootstrapManager
 *  com.atlassian.confluence.setup.settings.Settings
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.setup.settings.beans.CaptchaSettings
 *  com.atlassian.confluence.status.service.SystemInformationHelper
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.confluence.status.service.systeminfo.ConfluenceInfo
 *  com.atlassian.confluence.status.service.systeminfo.DatabaseInfo
 *  com.atlassian.confluence.status.service.systeminfo.MemoryInfo
 *  com.atlassian.confluence.status.service.systeminfo.UsageInfo
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.util.UserChecker
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.extras.api.ProductLicense
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginState
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  javax.annotation.Nonnull
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.log4j.Appender
 *  org.apache.log4j.LogManager
 *  org.apache.log4j.RollingFileAppender
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.confluence;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.languages.Language;
import com.atlassian.confluence.languages.LanguageManager;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.logging.ConfluenceHomeLogAppender;
import com.atlassian.confluence.plugins.synchrony.api.SynchronyEnv;
import com.atlassian.confluence.plugins.synchrony.api.SynchronyProcessManager;
import com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.setup.settings.beans.CaptchaSettings;
import com.atlassian.confluence.status.service.SystemInformationHelper;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.confluence.status.service.systeminfo.ConfluenceInfo;
import com.atlassian.confluence.status.service.systeminfo.DatabaseInfo;
import com.atlassian.confluence.status.service.systeminfo.MemoryInfo;
import com.atlassian.confluence.status.service.systeminfo.UsageInfo;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.UserChecker;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.extras.api.ProductLicense;
import com.atlassian.extras.api.confluence.ConfluenceLicense;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginState;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sisyphus.SisyphusPatternSource;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.troubleshooting.confluence.ConfluenceFileSanitizerPatternManager;
import com.atlassian.troubleshooting.confluence.format.ByteSizeFormat;
import com.atlassian.troubleshooting.stp.hercules.HerculesRegexResource;
import com.atlassian.troubleshooting.stp.hercules.ScanItem;
import com.atlassian.troubleshooting.stp.hercules.regex.cacheables.SavedExternalResourceService;
import com.atlassian.troubleshooting.stp.properties.PropertyStore;
import com.atlassian.troubleshooting.stp.properties.SupportDataAppenderManager;
import com.atlassian.troubleshooting.stp.properties.SupportDataXmlKeyResolver;
import com.atlassian.troubleshooting.stp.request.FileSanitizer;
import com.atlassian.troubleshooting.stp.salext.AbstractSupportApplicationInfo;
import com.atlassian.troubleshooting.stp.salext.ApplicationType;
import com.atlassian.troubleshooting.stp.salext.bundle.SupportZipBundleAccessor;
import com.atlassian.troubleshooting.stp.salext.license.ApplicationLicenseInfo;
import com.atlassian.troubleshooting.stp.salext.license.ProductLicenseInfo;
import com.atlassian.troubleshooting.stp.salext.mail.MailUtility;
import com.atlassian.troubleshooting.stp.salext.output.XmlSupportDataFormatter;
import com.atlassian.troubleshooting.stp.spi.SupportDataDetail;
import java.io.File;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.LogManager;
import org.apache.log4j.RollingFileAppender;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ConfluenceApplicationInfo
extends AbstractSupportApplicationInfo {
    public static final String CONFLUENCE_CLUSTERED = "clustered";
    public static final String CONFLUENCE_SITE_HOME_PAGE = "site.home.page";
    public static final String CONFLUENCE_USAGE_CONTENT_CURRENT = "usage.content.current";
    public static final String CONFLUENCE_USAGE_CONTENT_TOTAL = "usage.content.total";
    public static final String CONFLUENCE_USAGE_GLOBAL_SPACES = "usage.global.spaces";
    public static final String CONFLUENCE_USAGE_PERSONAL_SPACES = "usage.personal.spaces";
    public static final String CONFLUENCE_USAGE_TOTAL_SPACES = "usage.total.spaces";
    public static final String SYSTEM_PROPERTY_STORAGE_TYPE = "AttachmentStorageType";
    private static final Logger LOG = LoggerFactory.getLogger(ConfluenceApplicationInfo.class);
    private static final int CONFLUENCE_SIX_BASE_BUILD_NUMBER = 7100;
    private static final String STATIC_ASSETS_PLUGIN_KEY = "com.atlassian.plugins.static-assets-url";
    private static final String CONFLUENCE_LOG_APPENDER_NAME = "confluencelog";
    private final ByteSizeFormat byteFormat = new ByteSizeFormat();
    private final NumberFormat percentFormat = NumberFormat.getPercentInstance();
    private final ApplicationConfiguration applicationConfiguration;
    private final BootstrapManager bootstrapManager;
    private final ClusterManager clusterManager;
    private final BundleContext bundleContext;
    private final I18NBeanFactory i18NBeanFactory;
    private final LanguageManager languageManager;
    private final LicenseService licenseService;
    private final SettingsManager settingsManager;
    private final PluginSettingsFactory pluginSettingsFactory;
    private final SystemInformationService sysInfoService;

    @Autowired
    public ConfluenceApplicationInfo(ApplicationConfiguration applicationConfiguration, ApplicationProperties applicationProperties, BootstrapManager bootstrapManager, I18nResolver i18nResolver, TemplateRenderer renderer, SystemInformationService sysInfoService, LanguageManager languageManager, ClusterManager clusterManager, I18NBeanFactory i18NBeanFactory, LicenseService licenseService, SettingsManager settingsManager, PluginAccessor pluginAccessor, SupportDataAppenderManager supportDataAppenderManager, SupportDataXmlKeyResolver supportDataXmlKeyResolver, XmlSupportDataFormatter xmlSupportDataFormatter, BundleContext bundleContext, MailUtility mailUtility, SavedExternalResourceService savedExternalResourceService, ConfluenceFileSanitizerPatternManager confluenceFileSanitizerPatternManager, PluginSettingsFactory pluginSettingsFactory, SupportZipBundleAccessor supportZipBundleAccessor) {
        super(applicationProperties, i18nResolver, renderer, supportDataAppenderManager, supportDataXmlKeyResolver, xmlSupportDataFormatter, pluginAccessor, mailUtility, savedExternalResourceService, confluenceFileSanitizerPatternManager, supportZipBundleAccessor);
        this.applicationConfiguration = Objects.requireNonNull(applicationConfiguration);
        this.bootstrapManager = Objects.requireNonNull(bootstrapManager);
        this.sysInfoService = sysInfoService;
        this.languageManager = languageManager;
        this.clusterManager = clusterManager;
        this.i18NBeanFactory = i18NBeanFactory;
        this.licenseService = licenseService;
        this.settingsManager = settingsManager;
        this.bundleContext = bundleContext;
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    @Override
    public SisyphusPatternSource getPatternSource() {
        return this.getSourceFromCacheable(HerculesRegexResource.CONFLUENCE_HERCULES_REGEX);
    }

    @Override
    public String getApplicationSEN() {
        ConfluenceInfo confluenceInfo = this.sysInfoService.getConfluenceInfo();
        return confluenceInfo.getSupportEntitlementNumber();
    }

    @Override
    public String getApplicationServerID() {
        ConfluenceInfo confluenceInfo = this.sysInfoService.getConfluenceInfo();
        return confluenceInfo.getServerId();
    }

    @Override
    public String getApplicationHome() {
        return this.applicationConfiguration.getApplicationHome();
    }

    public String getSharedApplicationHome() {
        return this.bootstrapManager.getSharedHome().toPath().toString();
    }

    @Override
    public String getLocalApplicationHome() {
        return this.bootstrapManager.getLocalHome().toPath().toString();
    }

    @Override
    @Nonnull
    public File getTempDirectory() {
        return GeneralUtil.getLocalTempDirectory();
    }

    @Override
    @Nonnull
    public File getExportDirectory() {
        return new File(this.bootstrapManager.getApplicationHome(), "export");
    }

    @Override
    protected PropertyStore internalLoadProperties(SupportDataDetail detail) {
        int userCount;
        PropertyStore store = super.internalLoadProperties(detail);
        DatabaseInfo dbInfo = this.sysInfoService.getDatabaseInfo();
        PropertyStore dbProperties = store.addCategory("stp.properties.db");
        dbProperties.setValue("stp.properties.db.driver.class", dbInfo.getClass().getName());
        dbProperties.setValue("stp.properties.db.dialect", dbInfo.getDialect());
        dbProperties.setValue("stp.properties.db.driver.name", dbInfo.getDriverName());
        dbProperties.setValue("stp.properties.db.driver.version", dbInfo.getDriverVersion());
        dbProperties.setValue("stp.properties.db.example.Latency", dbInfo.getExampleLatency().toString());
        dbProperties.setValue("stp.properties.db.connection.transaction.isolation", dbInfo.getIsolationLevel());
        dbProperties.setValue("stp.properties.db.name", dbInfo.getName());
        dbProperties.setValue("stp.properties.db.connection.url", dbInfo.getUrl());
        dbProperties.setValue("stp.properties.db.version", dbInfo.getVersion());
        ConfluenceInfo confluenceInfo = this.sysInfoService.getConfluenceInfo();
        PropertyStore confluenceProperties = store.addCategory("stp.properties.application.info");
        confluenceProperties.setValue("stp.properties.application.base.url", confluenceInfo.getBaseUrl());
        confluenceProperties.setValue("stp.properties.application.build.number", confluenceInfo.getBuildNumber());
        confluenceProperties.setValue("stp.properties.application.home", confluenceInfo.getHome());
        confluenceProperties.setValue("stp.properties.license.server.id", confluenceInfo.getServerId());
        confluenceProperties.setValue("stp.properties.application.start.time", new Date(confluenceInfo.getStartTime()).toString());
        confluenceProperties.setValue("stp.properties.license.sen", confluenceInfo.getSupportEntitlementNumber());
        confluenceProperties.setValue("stp.properties.application.uptime", confluenceInfo.getUpTime());
        confluenceProperties.setValue("stp.properties.application.version", confluenceInfo.getVersion());
        Settings globalSettings = confluenceInfo.getGlobalSettings();
        confluenceProperties.setValue("stp.properties.attachment.data.store", this.getAttachmentDataStore(globalSettings));
        confluenceProperties.setValue("stp.properties.attachment.max.size", String.valueOf(globalSettings.getAttachmentMaxSize()));
        confluenceProperties.setValue("stp.properties.backup.path", globalSettings.getBackupPath());
        CaptchaSettings captchaSettings = globalSettings.getCaptchaSettings();
        confluenceProperties.setValue("stp.properties.captcha.enabled", String.valueOf(captchaSettings.isEnableCaptcha()));
        confluenceProperties.setValue("stp.properties.captcha.groups", StringUtils.join((Iterable)captchaSettings.getCaptchaGroups(), (char)','));
        confluenceProperties.setValue("stp.properties.backup.date.format.pattern", globalSettings.getDailyBackupDateFormatPattern());
        confluenceProperties.setValue("stp.properties.backup.file.prefix", globalSettings.getDailyBackupFilePrefix());
        confluenceProperties.setValue("stp.properties.default.encoding", globalSettings.getDefaultEncoding());
        confluenceProperties.setValue("stp.properties.global.default.locale", globalSettings.getGlobalDefaultLocale());
        confluenceProperties.setValue("stp.properties.indexing.language", globalSettings.getIndexingLanguage());
        confluenceProperties.setValue("stp.properties.attachment.ui.max", String.valueOf(globalSettings.getMaxAttachmentsInUI()));
        confluenceProperties.setValue("stp.properties.rss.max.items", String.valueOf(globalSettings.getMaxRssItems()));
        confluenceProperties.setValue("stp.properties.quicknav.max.requests", String.valueOf(globalSettings.getMaxSimultaneousQuickNavRequests()));
        confluenceProperties.setValue(CONFLUENCE_SITE_HOME_PAGE, globalSettings.getSiteHomePage());
        confluenceProperties.setValue("stp.properties.application.time.zone", globalSettings.getTimeZone().toString());
        SystemInformationHelper helper = new SystemInformationHelper(this.i18NBeanFactory.getI18NBean(Locale.ENGLISH), this.sysInfoService);
        confluenceProperties.setValue("stp.properties.system.date", (String)helper.getSystemSummary().get("system.date"));
        confluenceProperties.setValue("stp.properties.system.time", (String)helper.getSystemSummary().get("system.time"));
        PropertyStore systemSummaryStore = store.addCategory("stp.properties.system");
        systemSummaryStore.putValues(helper.getSystemSummary());
        if (this.isCollabEditingSupported()) {
            this.addSynchronyConfiguration(store);
        }
        if (this.isCdnPluginInstalled()) {
            this.addCdnConfiguration(store);
        }
        MemoryInfo memoryInfo = this.sysInfoService.getMemoryInfo();
        PropertyStore memoryProperties = store.addCategory("stp.properties.memory");
        memoryProperties.setValue("stp.properties.java.heap.allocated", this.byteFormat.format(memoryInfo.getAllocatedHeap().bytes()));
        memoryProperties.setValue("stp.properties.java.heap.available", this.byteFormat.format(memoryInfo.getAvailableHeap().bytes()));
        memoryProperties.setValue("stp.properties.java.permgen.available", this.byteFormat.format(memoryInfo.getAvailablePermGen().bytes()));
        memoryProperties.setValue("stp.properties.java.heap.free.allocated", this.byteFormat.format(memoryInfo.getFreeAllocatedHeap().bytes()));
        memoryProperties.setValue("stp.properties.java.heap.percent.used", this.percentFormat.format((double)memoryInfo.getFreeAllocatedHeap().bytes() / (double)memoryInfo.getMaxHeap().bytes()) + this.i18nResolver.getText("stp.java.memory.free"));
        memoryProperties.setValue("stp.properties.java.heap.max", this.byteFormat.format(memoryInfo.getMaxHeap().bytes()));
        memoryProperties.setValue("stp.properties.java.heap.used", this.byteFormat.format(memoryInfo.getUsedHeap().bytes()));
        memoryProperties.setValue("stp.properties.java.permgen.percent.used", this.percentFormat.format((double)memoryInfo.getAvailablePermGen().bytes() / (double)memoryInfo.getMaxPermGen().bytes()) + this.i18nResolver.getText("stp.java.memory.free"));
        memoryProperties.setValue("stp.properties.java.permgen.max", this.byteFormat.format(memoryInfo.getMaxPermGen().bytes()));
        memoryProperties.setValue("stp.properties.java.permgen.used", this.byteFormat.format(memoryInfo.getUsedPermGen().bytes()));
        Map modifications = this.sysInfoService.getModifications();
        PropertyStore modificationProperties = store.addCategory("stp.properties.modz");
        modificationProperties.putValues(modifications);
        UsageInfo usageInfo = this.sysInfoService.getUsageInfo();
        PropertyStore usageProperties = store.addCategory("stp.properties.usage");
        usageProperties.setValue(CONFLUENCE_USAGE_CONTENT_TOTAL, String.valueOf(usageInfo.getAllContent()));
        usageProperties.setValue(CONFLUENCE_USAGE_CONTENT_CURRENT, String.valueOf(usageInfo.getCurrentContent()));
        usageProperties.setValue("stp.properties.usage.local.groups", String.valueOf(usageInfo.getLocalGroups()));
        usageProperties.setValue("stp.properties.usage.local.users", String.valueOf(usageInfo.getLocalUsers()));
        usageProperties.setValue(CONFLUENCE_USAGE_GLOBAL_SPACES, String.valueOf(usageInfo.getGlobalSpaces()));
        usageProperties.setValue(CONFLUENCE_USAGE_PERSONAL_SPACES, String.valueOf(usageInfo.getPersonalSpaces()));
        usageProperties.setValue(CONFLUENCE_USAGE_TOTAL_SPACES, String.valueOf(usageInfo.getTotalSpaces()));
        PropertyStore languageProperties = store.addCategory("stp.properties.languages.installed");
        for (Language language : this.languageManager.getLanguages()) {
            PropertyStore languageStore = languageProperties.addCategory(language.getName());
            languageStore.setValue("stp.properties.languages.language.name", language.getName());
            languageStore.setValue("stp.properties.languages.language.country", language.getCountry());
        }
        PropertyStore licenseProperties = store.addCategory("stp.properties.license");
        ConfluenceLicense license = this.licenseService.retrieve();
        Optional.ofNullable(license.getOrganisation()).ifPresent(organisation -> licenseProperties.setValue("stp.properties.license.organisation", organisation.getName()));
        Optional.ofNullable(license.getLicenseType()).ifPresent(licenseType -> licenseProperties.setValue("stp.properties.license.type", licenseType.name()));
        Optional.ofNullable(license.getExpiryDate()).ifPresent(expiryDate -> licenseProperties.setValue("stp.properties.license.period", expiryDate.toString()));
        licenseProperties.setValue("stp.properties.license.users", String.valueOf(license.getMaximumNumberOfUsers()));
        Optional.ofNullable(license.getPartner()).ifPresent(partner -> licenseProperties.setValue("stp.properties.license.partner", partner.getName()));
        licenseProperties.setValue("stp.properties.license.server.id", confluenceInfo.getServerId());
        licenseProperties.setValue("stp.properties.license.sen", confluenceInfo.getSupportEntitlementNumber());
        licenseProperties.setValue(CONFLUENCE_CLUSTERED, String.valueOf(this.clusterManager.isClustered()));
        UserChecker userChecker = (UserChecker)ContainerManager.getComponent((String)"userChecker");
        if (userChecker != null && (userCount = userChecker.getNumberOfRegisteredUsers()) > 0) {
            licenseProperties.setValue("stp.properties.license.users.active", String.valueOf(userCount));
        }
        this.addUpgradeRecoveryFileNames(store);
        return store;
    }

    private void addUpgradeRecoveryFileNames(PropertyStore store) {
        PropertyStore upgradeRecoveryFiles = store.addCategory("stp.properties.upgrade.recovery.files");
        File directory = new File(this.applicationProperties.getHomeDirectory(), "recovery");
        if (!directory.isDirectory()) {
            return;
        }
        File[] files = directory.listFiles((dir, name) -> name.startsWith("upgradeRecoveryFile-") && name.endsWith(".xml.gz"));
        Arrays.sort(files, (o1, o2) -> Long.signum(o2.lastModified() - o1.lastModified()));
        for (File file : files) {
            PropertyStore upgradeRecoveryfile = upgradeRecoveryFiles.addCategory("stp.properties.upgrade.recovery.file");
            upgradeRecoveryfile.setValue("stp.properties.upgrade.recovery.file.name", file.getName());
            upgradeRecoveryfile.setValue("stp.properties.upgrade.recovery.file.date", new Date(file.lastModified()).toString());
            upgradeRecoveryfile.setValue("stp.properties.upgrade.recovery.file.size", this.byteFormat.format(file.length()));
        }
    }

    @Override
    public List<ScanItem> getApplicationLogFilePaths() {
        String logFilePath = this.getLogFilePath();
        if (new File(logFilePath).exists()) {
            return Collections.singletonList(ScanItem.createDefaultItem(logFilePath));
        }
        return Collections.emptyList();
    }

    private String getLogFilePath() {
        Appender appender = LogManager.getRootLogger().getAppender(CONFLUENCE_LOG_APPENDER_NAME);
        if (appender != null) {
            try {
                return this.getApplicationHome() + "/logs/" + ((ConfluenceHomeLogAppender)appender).getLogFileName();
            }
            catch (ClassCastException cce) {
                try {
                    return ((RollingFileAppender)appender).getFile();
                }
                catch (ClassCastException classCastException) {
                    // empty catch block
                }
            }
        }
        return this.getApplicationHome() + "/logs/atlassian-confluence.log";
    }

    @Override
    public String getCreateSupportRequestEmail() {
        return "confluence-autosupportrequests@atlassian.com";
    }

    @Override
    @Nonnull
    public Optional<String> getInstanceTitle() {
        return Optional.ofNullable(this.settingsManager).map(SettingsManager::getGlobalSettings).map(Settings::getSiteTitle);
    }

    @Override
    public String getMailQueueURL(HttpServletRequest req) {
        return this.getBaseURL(req) + "/admin/mail/viewmailqueue.action";
    }

    @Override
    public boolean isMailExceptionAvailable() {
        return true;
    }

    @Override
    public String getMailServerConfigurationURL(HttpServletRequest request) {
        return this.getBaseURL(request) + "/admin/mail/viewmailservers.action";
    }

    @Override
    public FileSanitizer getFileSanitizer() {
        return new FileSanitizer(this.getFileSanitizerPatternManager(), EXECUTABLE_EXTENSIONS, this.getTempDirectory());
    }

    @Override
    public ApplicationLicenseInfo getLicenseInfo() {
        return new ProductLicenseInfo((ProductLicense)this.licenseService.retrieve());
    }

    @Override
    public String getAdminLicenseUrl() {
        return "/admin/license.action";
    }

    @Override
    public ApplicationType getApplicationType() {
        return ApplicationType.CONFLUENCE;
    }

    @Override
    public Pattern getApplicationRestartPattern() {
        return Pattern.compile("(Starting\\sConfluence...)");
    }

    private boolean isCollabEditingSupported() {
        return Integer.parseInt(this.getApplicationBuildNumber()) >= 7100;
    }

    private boolean isCdnPluginInstalled() {
        Plugin staticAssetsPlugin = this.pluginAccessor.getEnabledPlugin(STATIC_ASSETS_PLUGIN_KEY);
        return staticAssetsPlugin != null && staticAssetsPlugin.getPluginState() == PluginState.ENABLED;
    }

    private void addSynchronyConfiguration(PropertyStore store) {
        try (SynchronyServiceWrapper wrapper = new SynchronyServiceWrapper();){
            SynchronyProcessManager processManager;
            PropertyStore configProps = store.addCategory("stp.properties.synchrony.configuration");
            ServiceReference configRef = wrapper.getConfigManagerRef();
            if (configRef != null) {
                SynchronyConfigurationManager configManager = wrapper.getConfigManager();
                if (configManager != null) {
                    configProps.setValue("stp.properties.synchrony.enabled", String.valueOf(configManager.isSynchronyEnabled()));
                    configProps.setValue("stp.properties.synchrony.shared.drafts.enabled", String.valueOf(configManager.isSharedDraftsEnabled()));
                    configProps.setValue("stp.properties.synchrony.explicitly.disabled", String.valueOf(configManager.isSynchronyExplicitlyDisabled()));
                    configProps.setValue("stp.properties.synchrony.shared.drafts.explicitly.disabled", String.valueOf(configManager.isSharedDraftsExplicitlyDisabled()));
                    configProps.setValue("stp.properties.synchrony.production.override", String.valueOf(configManager.isSynchronyProdOverrideEnabled()));
                    configProps.setValue("stp.properties.synchrony.internal.url", configManager.getInternalServiceUrl());
                    configProps.setValue("stp.properties.synchrony.internal.port", String.valueOf(configManager.getInternalPort()));
                    configProps.setValue("stp.properties.synchrony.external.url", configManager.getExternalServiceUrl());
                    configProps.setValue("stp.properties.synchrony.resources.url", configManager.getResourcesUrl());
                }
            } else if (this.isCollabEditingSupported()) {
                LOG.warn("Not able to locate the service for getting the Synchrony configuration. The Collaborative Editor Plugin may have been disabled.");
            }
            PropertyStore envProps = store.addCategory("stp.properties.synchrony.environment");
            ServiceReference processRef = wrapper.getProcessManagerRef();
            if (processRef != null && (processManager = wrapper.getProcessManager()) != null) {
                try {
                    ?[] enumConstants;
                    Class<?> synchronyEnv = Class.forName("com.atlassian.confluence.plugins.synchrony.api.SynchronyEnv");
                    for (Object enumConstant : enumConstants = synchronyEnv.getEnumConstants()) {
                        envProps.setValue(String.valueOf(enumConstant), processManager.getSynchronyProperty((SynchronyEnv)enumConstant));
                    }
                    configProps.setValue("stp.properties.synchrony.proxy.enabled", (String)processManager.getConfiguration().get("isProxyEnabled"));
                    configProps.setValue("stp.properties.synchrony.proxy.running", (String)processManager.getConfiguration().get("isProxyRunning"));
                }
                catch (ClassNotFoundException ex) {
                    LOG.error(ex.getMessage());
                }
            }
        }
    }

    private void addCdnConfiguration(PropertyStore store) {
        PropertyStore configProps = store.addCategory("stp.properties.cdn.configuration");
        PluginSettings pluginSettings = this.pluginSettingsFactory.createGlobalSettings();
        configProps.setValue("stp.properties.cdn.enabled", String.valueOf(pluginSettings.get("atlassian.cdn.enabled")));
        configProps.setValue("stp.properties.cdn.url", String.valueOf(pluginSettings.get("atlassian.prefix.cdn.url")));
    }

    protected String getAttachmentDataStore(Settings globalSettings) {
        String attachmentStorageType = System.getProperty(SYSTEM_PROPERTY_STORAGE_TYPE);
        if (StringUtils.isBlank((CharSequence)attachmentStorageType)) {
            return globalSettings.getAttachmentDataStore();
        }
        return attachmentStorageType;
    }

    private final class SynchronyServiceWrapper
    implements AutoCloseable {
        private final ServiceReference configManagerRef;
        private final ServiceReference processManagerRef;

        SynchronyServiceWrapper() {
            this.configManagerRef = ConfluenceApplicationInfo.this.bundleContext.getServiceReference("com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager");
            this.processManagerRef = ConfluenceApplicationInfo.this.bundleContext.getServiceReference("com.atlassian.confluence.plugins.synchrony.api.SynchronyProcessManager");
        }

        @Override
        public void close() {
            if (this.getConfigManagerRef() != null) {
                ConfluenceApplicationInfo.this.bundleContext.ungetService(this.configManagerRef);
            }
            if (this.getProcessManagerRef() != null) {
                ConfluenceApplicationInfo.this.bundleContext.ungetService(this.processManagerRef);
            }
        }

        private ServiceReference getConfigManagerRef() {
            return this.configManagerRef;
        }

        private ServiceReference getProcessManagerRef() {
            return this.processManagerRef;
        }

        private SynchronyConfigurationManager getConfigManager() {
            return (SynchronyConfigurationManager)ConfluenceApplicationInfo.this.bundleContext.getService(this.configManagerRef);
        }

        private SynchronyProcessManager getProcessManager() {
            return (SynchronyProcessManager)ConfluenceApplicationInfo.this.bundleContext.getService(this.processManagerRef);
        }
    }
}


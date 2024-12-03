/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Throwables
 *  javax.annotation.Nonnull
 *  javax.servlet.http.HttpServletRequest
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.FrameworkUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.stp.salext;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.troubleshooting.api.supportzip.SupportZipBundle;
import com.atlassian.troubleshooting.stp.hercules.HerculesRegexResource;
import com.atlassian.troubleshooting.stp.hercules.ScanItem;
import com.atlassian.troubleshooting.stp.hercules.regex.cacheables.SavedExternalResourceService;
import com.atlassian.troubleshooting.stp.properties.MultiValuePropertyStore;
import com.atlassian.troubleshooting.stp.properties.PropertyStore;
import com.atlassian.troubleshooting.stp.properties.SupportDataAppenderManager;
import com.atlassian.troubleshooting.stp.properties.SupportDataXmlKeyResolver;
import com.atlassian.troubleshooting.stp.salext.FileSanitizerPatternManager;
import com.atlassian.troubleshooting.stp.salext.StringMappedSisyphusPatternSource;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.salext.bundle.SupportZipBundleAccessor;
import com.atlassian.troubleshooting.stp.salext.mail.MailUtility;
import com.atlassian.troubleshooting.stp.salext.output.XmlSupportDataFormatter;
import com.atlassian.troubleshooting.stp.spi.SupportDataDetail;
import com.atlassian.util.concurrent.ConcurrentOperationMap;
import com.atlassian.util.concurrent.ConcurrentOperationMapImpl;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Throwables;
import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSupportApplicationInfo
implements SupportApplicationInfo {
    public static final List<String> EXECUTABLE_EXTENSIONS = Arrays.asList(".ade .adp .bat .chm .cmd .com .cpl .exe .hta .ins .isp .jar .jse .lib .lnk .mde .msc .msp .mst .pif .scr .sct .sh .shb .sys .vb .vbe .vbs .vxd .wsc .wsf .wsh".split(" "));
    private static final Logger LOG = LoggerFactory.getLogger(AbstractSupportApplicationInfo.class);
    protected final ApplicationProperties applicationProperties;
    protected final I18nResolver i18nResolver;
    protected final TemplateRenderer renderer;
    protected final SupportDataAppenderManager supportDataAppenderManager;
    protected final SupportDataXmlKeyResolver supportDataXmlKeyResolver;
    protected final PluginAccessor pluginAccessor;
    protected final MailUtility mailUtility;
    protected final ConcurrentOperationMap<Object, PropertyStore> propertyStoreOperationMap;
    @VisibleForTesting
    protected final SupportZipBundleAccessor supportZipBundleAccessor;
    private final XmlSupportDataFormatter xmlSupportDataFormatter;
    private final SavedExternalResourceService cacheResolvingService;
    private final FileSanitizerPatternManager fileSanitizerPatternManager;

    protected AbstractSupportApplicationInfo(ApplicationProperties applicationProperties, I18nResolver i18nResolver, TemplateRenderer renderer, SupportDataAppenderManager supportDataAppenderManager, SupportDataXmlKeyResolver supportDataXmlKeyResolver, XmlSupportDataFormatter xmlSupportDataFormatter, PluginAccessor pluginAccessor, MailUtility mailUtility, SavedExternalResourceService cacheResolvingService, FileSanitizerPatternManager patternsForSanitization, SupportZipBundleAccessor supportZipBundleAccessor) {
        this(applicationProperties, i18nResolver, renderer, supportDataAppenderManager, supportDataXmlKeyResolver, xmlSupportDataFormatter, pluginAccessor, mailUtility, new ConcurrentOperationMapImpl<Object, PropertyStore>(), cacheResolvingService, patternsForSanitization, supportZipBundleAccessor);
    }

    protected AbstractSupportApplicationInfo(ApplicationProperties applicationProperties, I18nResolver i18nResolver, TemplateRenderer renderer, SupportDataAppenderManager supportDataAppenderManager, SupportDataXmlKeyResolver supportDataXmlKeyResolver, XmlSupportDataFormatter xmlSupportDataFormatter, PluginAccessor pluginAccessor, MailUtility mailUtility, ConcurrentOperationMap<Object, PropertyStore> propertyStoreOperationMap, SavedExternalResourceService cacheResolvingService, FileSanitizerPatternManager patternsForSanitization, SupportZipBundleAccessor supportZipBundleAccessor) {
        this.applicationProperties = applicationProperties;
        this.i18nResolver = i18nResolver;
        this.renderer = renderer;
        this.supportDataAppenderManager = supportDataAppenderManager;
        this.supportDataXmlKeyResolver = supportDataXmlKeyResolver;
        this.pluginAccessor = pluginAccessor;
        this.mailUtility = mailUtility;
        this.propertyStoreOperationMap = propertyStoreOperationMap;
        this.xmlSupportDataFormatter = xmlSupportDataFormatter;
        this.cacheResolvingService = cacheResolvingService;
        this.fileSanitizerPatternManager = patternsForSanitization;
        this.supportZipBundleAccessor = supportZipBundleAccessor;
    }

    @Nonnull
    public StringMappedSisyphusPatternSource getSourceFromCacheable(HerculesRegexResource savedExternalResource) {
        return new StringMappedSisyphusPatternSource(this.cacheResolvingService.resolve(savedExternalResource).getValue());
    }

    @Override
    @Nonnull
    public final List<SupportZipBundle> getSupportZipBundles() {
        return this.supportZipBundleAccessor.getBundles();
    }

    @Override
    public final Set<String> getDefaultBundleKeys() {
        return this.getSupportZipBundles().stream().filter(SupportZipBundle::isSelected).map(SupportZipBundle::getKey).collect(Collectors.toSet());
    }

    @Override
    public final PropertyStore loadProperties(SupportDataDetail detail) {
        try {
            return this.propertyStoreOperationMap.runOperation((Object)detail, () -> this.internalLoadProperties(detail));
        }
        catch (ExecutionException e) {
            throw Throwables.propagate((Throwable)e.getCause());
        }
    }

    protected PropertyStore internalLoadProperties(SupportDataDetail detail) {
        MultiValuePropertyStore store = new MultiValuePropertyStore();
        this.supportDataAppenderManager.addSupportData(store, detail);
        return store;
    }

    @Override
    public String getApplicationName() {
        return this.applicationProperties.getDisplayName();
    }

    @Override
    @Nonnull
    public Optional<String> getInstanceTitle() {
        return Optional.empty();
    }

    @Override
    public String getApplicationVersion() {
        return this.applicationProperties.getVersion();
    }

    @Override
    public String getApplicationBuildNumber() {
        return this.applicationProperties.getBuildNumber();
    }

    @Override
    public Date getApplicationBuildDate() {
        return this.applicationProperties.getBuildDate();
    }

    @Override
    public File getPrimaryApplicationLog() {
        List<ScanItem> applicationLogs = this.getApplicationLogFilePaths();
        if (applicationLogs == null || applicationLogs.isEmpty()) {
            throw new IllegalStateException("Couldn't find any application logs.");
        }
        return new File(applicationLogs.get(0).getPath());
    }

    @Override
    public String getApplicationLogDir() {
        try {
            return this.getPrimaryApplicationLog().getParentFile().getPath();
        }
        catch (IllegalStateException | NullPointerException e) {
            return this.getApplicationHome() + "/logs";
        }
    }

    @Override
    public String getApplicationHome() {
        return this.applicationProperties.getHomeDirectory().toString();
    }

    @Override
    public String getLocalApplicationHome() {
        return this.getApplicationHome();
    }

    @Override
    public String getText(String key) {
        return this.i18nResolver.getText(key);
    }

    @Override
    public String getText(String key, Serializable ... arguments) {
        return this.i18nResolver.getText(key, arguments);
    }

    @Override
    public XmlSupportDataFormatter getXmlSupportDataFormatter() {
        return this.xmlSupportDataFormatter;
    }

    @Override
    @Nonnull
    public List<SupportZipBundle> getSelectedSupportZipBundles(HttpServletRequest req) {
        return this.getSupportZipBundles().stream().filter(bundle -> Boolean.parseBoolean(req.getParameter(bundle.getKey()))).collect(Collectors.toList());
    }

    @Override
    public String saveProperties(SupportDataDetail detail) {
        PropertyStore supportInfoProperties = this.loadProperties(detail);
        Properties xmlElementNameMappings = this.supportDataXmlKeyResolver.getKeyMappings();
        return this.getXmlSupportDataFormatter().getFormattedProperties(supportInfoProperties, xmlElementNameMappings);
    }

    @Override
    public String getStpVersion() {
        Bundle bundle = FrameworkUtil.getBundle(this.getClass());
        if (bundle != null) {
            return bundle.getVersion().toString();
        }
        return this.getClass().getPackage().getImplementationVersion();
    }

    @Override
    public TemplateRenderer getTemplateRenderer() {
        return this.renderer;
    }

    @Override
    public String getBaseURL(HttpServletRequest req) {
        return req.getRequestURI().replaceFirst(req.getServletPath() + ".*", "");
    }

    @Override
    public String getBaseURL(UrlMode urlMode) {
        return this.applicationProperties.getBaseUrl(urlMode);
    }

    @Override
    public List<String> getSystemWarnings() {
        return Collections.emptyList();
    }

    @Override
    @Nonnull
    public File getTempDirectory() {
        return new File(this.getApplicationHome(), "temp");
    }

    @Override
    @Nonnull
    public File getExportDirectory() {
        return new File(this.getApplicationHome(), "export");
    }

    @Override
    @Nonnull
    public Optional<File> getExportFile(String filename) {
        return Optional.of(this.getExportDirectory()).filter(File::isDirectory).map(exportDir -> exportDir.listFiles((dir, name) -> name.equals(filename))).flatMap(files -> Arrays.stream(files).findFirst());
    }

    @Override
    public String getFromAddress() {
        return this.mailUtility.getDefaultFromAddress();
    }

    @Override
    public String getPlatformId() {
        return this.applicationProperties.getPlatformId();
    }

    @Override
    public String getTimeZoneRelativeToGMT() {
        String rawTimezone = System.getProperty("user.timezone");
        TimeZone timeZone = TimeZone.getTimeZone(rawTimezone);
        int offsetMS = timeZone.getRawOffset() + (timeZone.inDaylightTime(new Date()) ? timeZone.getDSTSavings() : 0);
        int offsetHour = offsetMS / 1000 / 60 / 60;
        return "GMT" + (offsetHour >= 0 ? "+" : "") + offsetHour;
    }

    protected <T> T callAndLogExceptions(Callable<T> task) {
        try {
            return task.call();
        }
        catch (Exception e) {
            LOG.warn(e.getMessage(), (Throwable)e);
            return null;
        }
    }

    protected FileSanitizerPatternManager getFileSanitizerPatternManager() {
        return this.fileSanitizerPatternManager;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 *  org.codehaus.jackson.map.JsonMappingException
 *  org.codehaus.jackson.map.ObjectMapper
 */
package com.atlassian.audit.frontend.data;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.audit.coverage.ProductLicenseChecker;
import com.atlassian.audit.file.CachingRetentionFileConfigService;
import com.atlassian.audit.frontend.data.AuditSettingsViewData;
import com.atlassian.audit.plugin.configuration.PropertiesProvider;
import com.atlassian.audit.service.TranslationService;
import com.atlassian.json.marshal.Jsonable;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.function.Function;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class AuditSettingsViewDataProvider
implements WebResourceDataProvider {
    private static final String PROP_FILE_PATH = "/atlassian-audit-frontend-settings.properties";
    private static final String PREFIX_KEY = "atlassian.audit.frontend";
    private static final String EXPORT_MAX_RECORDS_WARNING_KEY = "atlassian.audit.frontend.export.warning.maxRecord";
    private static final String AREA_KEY = "atlassian.audit.frontend.area";
    private static final String LICENSE_DC_KEY = ".dc";
    private static final String LICENSE_SERVER_KEY = ".server";
    private static final String AREA_LABEL_KEY = ".label";
    private static final String AREA_DESC_KEY = ".description";
    private static final String LEVELS_KEY = "atlassian.audit.frontend.levels";
    private static final String LEVEL_OFF_LABEL_KEY = "atlassian.audit.frontend.configuration.levelInformation.label.off";
    private static final String LEVEL_BASE_LABEL_KEY = "atlassian.audit.frontend.configuration.levelInformation.label.base";
    private static final String LEVEL_ADVANCED_LABEL_KEY = "atlassian.audit.frontend.configuration.levelInformation.label.advanced";
    private static final String LEVEL_FULL_LABEL_KEY = "atlassian.audit.frontend.configuration.levelInformation.label.full";
    private static final String SPLIT = ",";
    private final TranslationService translationService;
    private final ApplicationProperties applicationProperties;
    private final ProductLicenseChecker licenseChecker;
    private final Properties auditSettings;
    private final ObjectMapper objectMapper;
    private final CachingRetentionFileConfigService cachingRetentionFileConfigService;
    private final PropertiesProvider propertiesProvider;

    public AuditSettingsViewDataProvider(ProductLicenseChecker licenseChecker, ApplicationProperties applicationProperties, TranslationService translationService, ObjectMapper objectMapper, CachingRetentionFileConfigService cachingRetentionFileConfigService, PropertiesProvider propertiesProvider) throws IOException {
        this(licenseChecker, applicationProperties, translationService, objectMapper, PROP_FILE_PATH, cachingRetentionFileConfigService, propertiesProvider);
    }

    @VisibleForTesting
    protected AuditSettingsViewDataProvider(ProductLicenseChecker licenseChecker, ApplicationProperties applicationProperties, TranslationService translationService, ObjectMapper objectMapper, String propFilePath, CachingRetentionFileConfigService cachingRetentionFileConfigService, PropertiesProvider propertiesProvider) throws IOException {
        this.applicationProperties = applicationProperties;
        this.translationService = translationService;
        this.licenseChecker = licenseChecker;
        this.objectMapper = objectMapper;
        this.cachingRetentionFileConfigService = cachingRetentionFileConfigService;
        this.propertiesProvider = propertiesProvider;
        try (InputStream inputStream = this.getClass().getResourceAsStream(propFilePath);){
            this.auditSettings = new Properties();
            this.auditSettings.load(inputStream);
        }
    }

    public Jsonable get() {
        return writer -> {
            try {
                this.objectMapper.writeValue(writer, (Object)this.getData());
            }
            catch (Exception e) {
                throw new JsonMappingException(e.getMessage(), (Throwable)e);
            }
        };
    }

    private AuditSettingsViewData getData() {
        String[] areaKeys;
        AuditSettingsViewData settingsViewData = new AuditSettingsViewData().withFileLocation("<HOME_DIRECTORY>/log/audit").withMaxExportRecordsWarning(Integer.parseInt(this.auditSettings.getProperty(EXPORT_MAX_RECORDS_WARNING_KEY, "10000"))).withMaxRecordsInDb(this.propertiesProvider.getInteger("plugin.audit.db.limit.rows", 10000000)).withFileSizeLimitInMb(this.propertiesProvider.getInteger("plugin.audit.file.max.file.size", 100)).withMaxFileCount(this.cachingRetentionFileConfigService.getConfig().getMaxFileCount()).withExcludedActionsEnabled(this.isDcLicense());
        for (String areaKey : areaKeys = this.auditSettings.getProperty(AREA_KEY + (this.isDcLicense() ? LICENSE_DC_KEY : LICENSE_SERVER_KEY) + "." + this.getProductName()).split(SPLIT)) {
            settingsViewData.area(new AuditSettingsViewData.ConfigurationArea(areaKey, this.getAreaI18nText(areaKey, AREA_LABEL_KEY), this.getAreaI18nText(areaKey, AREA_DESC_KEY), this.getAvailableLevels(areaKey)));
        }
        settingsViewData.levels(new AuditSettingsViewData.ConfigurationLevel(this.getI18nText(LEVEL_OFF_LABEL_KEY), this.getI18nText(LEVEL_BASE_LABEL_KEY), this.getI18nText(LEVEL_ADVANCED_LABEL_KEY), this.getI18nText(LEVEL_FULL_LABEL_KEY)));
        settingsViewData.withLogFileEnabled(this.isDcLicense() || "bitbucket".equals(this.getProductName()) || "stash".equals(this.getProductName()));
        return settingsViewData;
    }

    private String getI18nText(String key) {
        return this.translationService.getUserLocaleWithApplicationLocaleFallbackText(key);
    }

    private String getAreaI18nText(String area, String label) {
        return this.returnFirstTranslation(this::getI18nText, PREFIX_KEY + (this.isDcLicense() ? LICENSE_DC_KEY : LICENSE_SERVER_KEY) + "." + this.getProductName() + "." + area + label, PREFIX_KEY + (this.isDcLicense() ? LICENSE_DC_KEY : LICENSE_SERVER_KEY) + "." + area + label, PREFIX_KEY + "." + area + label);
    }

    private String returnFirstTranslation(Function<String, String> func, String ... propKeys) {
        for (String propKey : propKeys) {
            String propValue = func.apply(propKey);
            if (propValue == null || propValue.equals(propKey)) continue;
            return propValue;
        }
        throw new RuntimeException("Not able to find property from : " + Arrays.toString(propKeys));
    }

    private String[] getAvailableLevels(String area) {
        return this.returnFirstTranslation(this.auditSettings::getProperty, LEVELS_KEY + (this.isDcLicense() ? LICENSE_DC_KEY : LICENSE_SERVER_KEY) + "." + this.getProductName() + "." + area, LEVELS_KEY + (this.isDcLicense() ? LICENSE_DC_KEY : LICENSE_SERVER_KEY) + "." + this.getProductName(), LEVELS_KEY + (this.isDcLicense() ? LICENSE_DC_KEY : LICENSE_SERVER_KEY)).split(SPLIT);
    }

    private boolean isDcLicense() {
        return !this.licenseChecker.isNotDcLicense();
    }

    private String getProductName() {
        return this.applicationProperties.getPlatformId();
    }
}


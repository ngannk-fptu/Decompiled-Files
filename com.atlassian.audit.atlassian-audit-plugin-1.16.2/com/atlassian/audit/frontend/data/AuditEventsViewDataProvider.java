/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 *  org.codehaus.jackson.map.JsonMappingException
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.audit.frontend.data;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.audit.ao.dao.AuditEntityDao;
import com.atlassian.audit.coverage.ProductLicenseChecker;
import com.atlassian.audit.frontend.data.AuditEventsViewData;
import com.atlassian.audit.model.AuditAction;
import com.atlassian.audit.model.AuditCategory;
import com.atlassian.audit.service.ActionsService;
import com.atlassian.audit.service.CategoriesService;
import com.atlassian.audit.service.TranslationService;
import com.atlassian.json.marshal.Jsonable;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuditEventsViewDataProvider
implements WebResourceDataProvider {
    static final String PROP_FILE_PATH = "/atlassian-audit-frontend-settings.properties";
    private static final String PREFIX_KEY = "atlassian.audit.frontend";
    private static final String TITLE_KEY = "atlassian.audit.frontend.title";
    private static final String LICENSE_DC_KEY = ".dc";
    private static final String LICENSE_SERVER_KEY = ".server";
    private static final String AFFECTED_OBJECTS_PLACEHOLDER = "atlassian.audit.frontend.affectedObjects.placeholder.";
    private static final String AFFECTED_OBJECTS_TYPE = "atlassian.audit.frontend.affectedObjects.type.";
    private static final String DELEGATED_VIEW_FILTER = "atlassian.audit.frontend.delegatedView.filter.";
    private static final String SPLIT = ",";
    private static final Logger log = LoggerFactory.getLogger(AuditEventsViewDataProvider.class);
    private final ActionsService actionsService;
    private final ApplicationProperties applicationProperties;
    private final Properties auditSettings;
    private final CategoriesService categoriesService;
    private final ObjectMapper objectMapper;
    private final ProductLicenseChecker productLicenseChecker;
    private final TimeZoneManager timeZoneManager;
    private final TranslationService translationService;
    private final AuditEntityDao auditEntityDao;

    public AuditEventsViewDataProvider(ActionsService actionsService, ApplicationProperties applicationProperties, CategoriesService categoriesService, ObjectMapper objectMapper, ProductLicenseChecker productLicenseChecker, TimeZoneManager timeZoneManager, TranslationService translationService, AuditEntityDao auditEntityDao) throws IOException {
        this(actionsService, applicationProperties, auditEntityDao, categoriesService, objectMapper, productLicenseChecker, PROP_FILE_PATH, timeZoneManager, translationService);
    }

    @VisibleForTesting
    protected AuditEventsViewDataProvider(ActionsService actionsService, ApplicationProperties applicationProperties, AuditEntityDao auditEntityDao, CategoriesService categoriesService, ObjectMapper objectMapper, ProductLicenseChecker productLicenseChecker, String propFilePath, TimeZoneManager timeZoneManager, TranslationService translationService) throws IOException {
        this.actionsService = Objects.requireNonNull(actionsService, "actionsService");
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
        this.auditEntityDao = Objects.requireNonNull(auditEntityDao, "auditEntityDao");
        this.categoriesService = Objects.requireNonNull(categoriesService, "categoriesService");
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper");
        this.productLicenseChecker = Objects.requireNonNull(productLicenseChecker, "productLicenseChecker");
        this.timeZoneManager = Objects.requireNonNull(timeZoneManager, "timeZoneManager");
        this.translationService = Objects.requireNonNull(translationService, "translationService");
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

    private AuditEventsViewData getData() {
        AuditEventsViewData eventsViewData = new AuditEventsViewData().withPageTitle(this.getI18nText(TITLE_KEY + (this.isDcLicense() ? LICENSE_DC_KEY : LICENSE_SERVER_KEY)));
        eventsViewData.withSelectiveExportEnabled(this.isDcLicense() || "conf".equals(this.getProductName())).withUserLocale(this.getUserLocale()).withUserTimeZone(this.getUserTimeZone()).withServerTimeZone(this.getServerTimeZone()).withAdvancedFilters(this.isDcLicense()).actionFilter(this.getSortedActions()).categoryFilter(this.getSortedCategories()).count(this.getCount());
        eventsViewData.affectedObjectsFilters(this.getAffectedObjectFiltersForProduct());
        eventsViewData.globalAffectedObjectsFilters(this.getGlobalAffectedObjectFiltersForProduct());
        eventsViewData.delegatedAffectedObjectsFilters(this.getDelegatedViewsAffectedObjectsFilters());
        return eventsViewData;
    }

    private long getCount() {
        return this.auditEntityDao.fastCountEstimate();
    }

    @VisibleForTesting
    LinkedHashMap<String, String> getSortedActions() {
        try {
            return this.actionsService.getActions().stream().sorted(Comparator.comparing(AuditAction::getAction)).collect(Collectors.toMap(AuditAction::getAction, AuditAction::getAction, (first, second) -> first, LinkedHashMap::new));
        }
        catch (Exception exception) {
            log.error("Failed to get action filters", (Throwable)exception);
            return new LinkedHashMap<String, String>();
        }
    }

    @VisibleForTesting
    LinkedHashMap<String, String> getSortedCategories() {
        try {
            return this.categoriesService.getCategories().stream().sorted(Comparator.comparing(AuditCategory::getCategory)).collect(Collectors.toMap(AuditCategory::getCategory, AuditCategory::getCategory, (first, second) -> first, LinkedHashMap::new));
        }
        catch (Exception exception) {
            log.error("Failed to get category filters", (Throwable)exception);
            return new LinkedHashMap<String, String>();
        }
    }

    private String getI18nText(String key) {
        return this.translationService.getUserLocaleWithApplicationLocaleFallbackText(key);
    }

    private String getServerTimeZone() {
        return this.timeZoneManager.getDefaultTimeZone().getID();
    }

    private String getUserTimeZone() {
        return this.timeZoneManager.getUserTimeZone().getID();
    }

    private String getUserLocale() {
        return this.translationService.getUserLocale().toLanguageTag();
    }

    private boolean isDcLicense() {
        return !this.productLicenseChecker.isNotDcLicense();
    }

    private String getProductName() {
        return this.applicationProperties.getPlatformId();
    }

    private Collection<AuditEventsViewData.AffectedObjectsFilter> getAffectedObjectFiltersForProduct() {
        String filtersStr = this.auditSettings.getProperty(AFFECTED_OBJECTS_TYPE + this.getProductName(), "");
        return Stream.of(filtersStr.split(SPLIT)).filter(str -> str != null && str.length() > 0).map(key -> new AuditEventsViewData.AffectedObjectsFilter(this.auditSettings.getProperty(AFFECTED_OBJECTS_TYPE + key), this.getI18nText(AFFECTED_OBJECTS_PLACEHOLDER + key))).collect(Collectors.toList());
    }

    private Collection<String> getGlobalAffectedObjectFiltersForProduct() {
        String filtersStr = this.auditSettings.getProperty(AFFECTED_OBJECTS_TYPE + this.getProductName(), "");
        return Stream.of(filtersStr.split(SPLIT)).filter(str -> str != null && str.length() > 0).map(key -> this.auditSettings.getProperty(AFFECTED_OBJECTS_TYPE + key)).collect(Collectors.toList());
    }

    private Map<String, List<String>> getDelegatedViewsAffectedObjectsFilters() {
        String filtersStr = this.auditSettings.getProperty(AFFECTED_OBJECTS_TYPE + this.getProductName(), "");
        return Stream.of(filtersStr.split(SPLIT)).filter(str -> str != null && str.length() > 0).collect(Collectors.toMap(key -> this.auditSettings.getProperty(AFFECTED_OBJECTS_TYPE + key), key -> Stream.of(this.auditSettings.getProperty(DELEGATED_VIEW_FILTER + this.getProductName() + "." + key, "").split(SPLIT)).filter(str -> str != null && str.length() > 0).map(filter -> this.auditSettings.getProperty(AFFECTED_OBJECTS_TYPE + filter, "")).collect(Collectors.toList())));
    }
}


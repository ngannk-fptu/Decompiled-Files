/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.rdbms.TransactionalExecutorFactory
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.google.common.annotations.VisibleForTesting
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.calendarstore;

import com.atlassian.confluence.extra.calendar3.ActiveObjectsServiceWrapper;
import com.atlassian.confluence.extra.calendar3.CalendarSettingsManager;
import com.atlassian.confluence.extra.calendar3.JodaIcal4jDateTimeConverter;
import com.atlassian.confluence.extra.calendar3.JodaIcal4jTimeZoneMapper;
import com.atlassian.confluence.extra.calendar3.calendarstore.QueryDSLWhereTransformer;
import com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers.SubCalendarEventTransformerFactory;
import com.atlassian.confluence.extra.calendar3.calendarstore.filtermappers.PropertyToDBFieldMapperSupplier;
import com.atlassian.confluence.extra.calendar3.ical4j.VEventMapper;
import com.atlassian.confluence.extra.calendar3.querydsl.QueryDSLMapper;
import com.atlassian.confluence.extra.calendar3.util.AsynchronousTaskExecutor;
import com.atlassian.confluence.extra.calendar3.wrapper.UserAccessorWrapper;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.rdbms.TransactionalExecutorFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component(value="dataStoreCommonPropertyAccessor")
public class DataStoreCommonPropertyAccessor {
    private final ActiveObjectsServiceWrapper activeObjectsServiceWrapper;
    protected final SettingsManager settingsManager;
    protected final UserAccessor userAccessor;
    private final SpaceManager spaceManager;
    private final SpacePermissionManager spacePermissionManager;
    private final LocaleManager localeManager;
    private final CalendarSettingsManager calendarSettingsManager;
    private final I18NBeanFactory i18NBeanFactory;
    protected final SubCalendarEventTransformerFactory subCalendarEventTransformerFactory;
    protected final JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper;
    protected final JodaIcal4jDateTimeConverter jodaIcal4jDateTimeConverter;
    private final QueryDSLMapper queryDSLMapper;
    private final TransactionTemplate transactionTemplate;
    private final AsynchronousTaskExecutor executor;
    private final VEventMapper vEventMapper;
    private final QueryDSLWhereTransformer queryDSLWhereTransformer;
    private final PropertyToDBFieldMapperSupplier propertyToDBFieldMapperSupplier;
    private final UserAccessorWrapper cachingUserAccessorHelper;
    private final TransactionalExecutorFactory transactionalExecutorFactory;
    private final SystemInformationService systemInformationService;

    @VisibleForTesting
    public DataStoreCommonPropertyAccessor() {
        this.activeObjectsServiceWrapper = null;
        this.settingsManager = null;
        this.userAccessor = null;
        this.spaceManager = null;
        this.spacePermissionManager = null;
        this.localeManager = null;
        this.calendarSettingsManager = null;
        this.i18NBeanFactory = null;
        this.jodaIcal4jTimeZoneMapper = null;
        this.subCalendarEventTransformerFactory = null;
        this.queryDSLMapper = null;
        this.transactionTemplate = null;
        this.executor = null;
        this.vEventMapper = null;
        this.queryDSLWhereTransformer = null;
        this.propertyToDBFieldMapperSupplier = null;
        this.cachingUserAccessorHelper = null;
        this.jodaIcal4jDateTimeConverter = null;
        this.transactionalExecutorFactory = null;
        this.systemInformationService = null;
    }

    @Autowired
    public DataStoreCommonPropertyAccessor(ActiveObjectsServiceWrapper activeObjectsServiceWrapper, @ComponentImport SettingsManager settingsManager, @ComponentImport UserAccessor userAccessor, @ComponentImport SpaceManager spaceManager, @ComponentImport SpacePermissionManager spacePermissionManager, @ComponentImport LocaleManager localeManager, CalendarSettingsManager calendarSettingsManager, @ComponentImport I18NBeanFactory i18NBeanFactory, JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper, SubCalendarEventTransformerFactory subCalendarEventTransformerFactory, QueryDSLMapper queryDSLMapper, @ComponentImport TransactionTemplate transactionTemplate, AsynchronousTaskExecutor executor, VEventMapper vEventMapper, QueryDSLWhereTransformer queryDSLWhereTransformer, PropertyToDBFieldMapperSupplier propertyToDBFieldMapperSupplier, @Qualifier(value="cachingUserAccessorWrapper") UserAccessorWrapper cachingUserAccessorHelper, JodaIcal4jDateTimeConverter jodaIcal4jDateTimeConverter, TransactionalExecutorFactory transactionalExecutorFactory, SystemInformationService systemInformationService) {
        this.activeObjectsServiceWrapper = activeObjectsServiceWrapper;
        this.settingsManager = settingsManager;
        this.userAccessor = userAccessor;
        this.spaceManager = spaceManager;
        this.spacePermissionManager = spacePermissionManager;
        this.localeManager = localeManager;
        this.calendarSettingsManager = calendarSettingsManager;
        this.i18NBeanFactory = i18NBeanFactory;
        this.jodaIcal4jTimeZoneMapper = jodaIcal4jTimeZoneMapper;
        this.subCalendarEventTransformerFactory = subCalendarEventTransformerFactory;
        this.queryDSLMapper = queryDSLMapper;
        this.transactionTemplate = transactionTemplate;
        this.executor = executor;
        this.vEventMapper = vEventMapper;
        this.queryDSLWhereTransformer = queryDSLWhereTransformer;
        this.propertyToDBFieldMapperSupplier = propertyToDBFieldMapperSupplier;
        this.cachingUserAccessorHelper = cachingUserAccessorHelper;
        this.jodaIcal4jDateTimeConverter = jodaIcal4jDateTimeConverter;
        this.transactionalExecutorFactory = transactionalExecutorFactory;
        this.systemInformationService = systemInformationService;
    }

    public PropertyToDBFieldMapperSupplier getPropertyToDBFieldMapperSupplier() {
        return this.propertyToDBFieldMapperSupplier;
    }

    public QueryDSLWhereTransformer getQueryDSLWhereTransformer() {
        return this.queryDSLWhereTransformer;
    }

    public ActiveObjectsServiceWrapper getActiveObjectsServiceWrapper() {
        return this.activeObjectsServiceWrapper;
    }

    public SettingsManager getSettingsManager() {
        return this.settingsManager;
    }

    public UserAccessor getUserAccessor() {
        return this.userAccessor;
    }

    public UserAccessorWrapper getCachingUserAccessorHelper() {
        return this.cachingUserAccessorHelper;
    }

    public SpaceManager getSpaceManager() {
        return this.spaceManager;
    }

    public SpacePermissionManager getSpacePermissionManager() {
        return this.spacePermissionManager;
    }

    public LocaleManager getLocaleManager() {
        return this.localeManager;
    }

    public CalendarSettingsManager getCalendarSettingsManager() {
        return this.calendarSettingsManager;
    }

    public I18NBeanFactory getI18NBeanFactory() {
        return this.i18NBeanFactory;
    }

    public SubCalendarEventTransformerFactory getSubCalendarEventTransformerFactory() {
        return this.subCalendarEventTransformerFactory;
    }

    public JodaIcal4jTimeZoneMapper getJodaIcal4jTimeZoneMapper() {
        return this.jodaIcal4jTimeZoneMapper;
    }

    public QueryDSLMapper getQueryDSLMapper() {
        return this.queryDSLMapper;
    }

    public TransactionTemplate getTransactionTemplate() {
        return this.transactionTemplate;
    }

    public AsynchronousTaskExecutor getExecutor() {
        return this.executor;
    }

    public VEventMapper getvEventMapper() {
        return this.vEventMapper;
    }

    public JodaIcal4jDateTimeConverter getJodaIcal4jDateTimeConverter() {
        return this.jodaIcal4jDateTimeConverter;
    }

    public TransactionalExecutorFactory getTransactionalExecutorFactory() {
        return this.transactionalExecutorFactory;
    }

    public SystemInformationService getSystemInformationService() {
        return this.systemInformationService;
    }
}


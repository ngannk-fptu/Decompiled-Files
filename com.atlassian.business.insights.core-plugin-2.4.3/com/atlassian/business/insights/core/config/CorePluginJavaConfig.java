/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.business.insights.api.cluster.ClusterInfo
 *  com.atlassian.business.insights.api.config.PropertiesProvider
 *  com.atlassian.business.insights.api.customfields.CustomFieldListService
 *  com.atlassian.business.insights.api.dataset.DatasetProvider
 *  com.atlassian.business.insights.api.filter.OptOutEntitiesLookupService
 *  com.atlassian.business.insights.api.filter.OptOutEntitiesTransformationService
 *  com.atlassian.business.insights.api.user.RequestContext
 *  com.atlassian.business.insights.api.writer.FileFormat
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugins.osgi.javaconfig.ExportOptions
 *  com.atlassian.plugins.osgi.javaconfig.OsgiServices
 *  com.atlassian.plugins.osgi.javaconfig.conditions.product.JiraOnly
 *  com.atlassian.plugins.osgi.javaconfig.conditions.product.RefappOnly
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  org.codehaus.jackson.JsonGenerator$Feature
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Conditional
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.business.insights.core.config;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.audit.api.AuditService;
import com.atlassian.business.insights.api.cluster.ClusterInfo;
import com.atlassian.business.insights.api.config.PropertiesProvider;
import com.atlassian.business.insights.api.customfields.CustomFieldListService;
import com.atlassian.business.insights.api.dataset.DatasetProvider;
import com.atlassian.business.insights.api.filter.OptOutEntitiesLookupService;
import com.atlassian.business.insights.api.filter.OptOutEntitiesTransformationService;
import com.atlassian.business.insights.api.user.RequestContext;
import com.atlassian.business.insights.api.writer.FileFormat;
import com.atlassian.business.insights.core.ao.dao.AoDataPipelineConfigDao;
import com.atlassian.business.insights.core.ao.dao.AoDataPipelineJobDao;
import com.atlassian.business.insights.core.ao.dao.AoEntityOptOutIdentifierDao;
import com.atlassian.business.insights.core.ao.dao.DefaultAoDataPipelineConfigDao;
import com.atlassian.business.insights.core.ao.dao.DefaultAoDataPipelineJobDao;
import com.atlassian.business.insights.core.ao.dao.DefaultAoEntityOptOutIdentifierDao;
import com.atlassian.business.insights.core.config.DataPipelinePluginSystemProperties;
import com.atlassian.business.insights.core.config.NoopPropertiesProvider;
import com.atlassian.business.insights.core.config.condition.NonRefapp;
import com.atlassian.business.insights.core.frontend.data.KbArticleLinkDataProvider;
import com.atlassian.business.insights.core.frontend.data.KbArticleLinkResolver;
import com.atlassian.business.insights.core.frontend.data.ProductDataProvider;
import com.atlassian.business.insights.core.mapper.DefaultLogRecordMapper;
import com.atlassian.business.insights.core.mapper.api.LogRecordMapper;
import com.atlassian.business.insights.core.plugin.CorePluginInfo;
import com.atlassian.business.insights.core.rest.exception.mapper.DiagnosticDescriptionTranslator;
import com.atlassian.business.insights.core.service.CountLimitedEntityOptOutService;
import com.atlassian.business.insights.core.service.DataExportJobExecutor;
import com.atlassian.business.insights.core.service.DbExportJobStateService;
import com.atlassian.business.insights.core.service.DefaultConfigService;
import com.atlassian.business.insights.core.service.DefaultDataExportOrchestrator;
import com.atlassian.business.insights.core.service.DefaultEntityOptOutService;
import com.atlassian.business.insights.core.service.DiskSpaceValidator;
import com.atlassian.business.insights.core.service.ExportEventPublisherService;
import com.atlassian.business.insights.core.service.LicenseChecker;
import com.atlassian.business.insights.core.service.api.ConfigService;
import com.atlassian.business.insights.core.service.api.DataExportOrchestrator;
import com.atlassian.business.insights.core.service.api.EntityOptOutService;
import com.atlassian.business.insights.core.service.api.EventPublisherService;
import com.atlassian.business.insights.core.service.api.ExportJobStateService;
import com.atlassian.business.insights.core.service.api.ExportScheduleService;
import com.atlassian.business.insights.core.service.api.ScheduleConfigService;
import com.atlassian.business.insights.core.service.scheduler.DefaultExportScheduleService;
import com.atlassian.business.insights.core.service.scheduler.DefaultScheduleConfigService;
import com.atlassian.business.insights.core.service.scheduler.ExportScheduleJobRunner;
import com.atlassian.business.insights.core.service.scheduler.ExportScheduleLauncher;
import com.atlassian.business.insights.core.spi.NoopRequestContext;
import com.atlassian.business.insights.core.util.DateConversionUtil;
import com.atlassian.business.insights.core.writer.DatasetFileSystemConfig;
import com.atlassian.business.insights.core.writer.FilesystemDatasetWriterFactory;
import com.atlassian.business.insights.core.writer.api.DatasetWriterFactory;
import com.atlassian.business.insights.core.writer.api.FileSystemConfig;
import com.atlassian.business.insights.core.writer.convert.JsonValueConverter;
import com.atlassian.business.insights.core.writer.convert.ValueConverter;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugins.osgi.javaconfig.ExportOptions;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.JiraOnly;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.RefappOnly;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CorePluginJavaConfig {
    @Bean
    public TimeZoneManager timeZoneManager() {
        return (TimeZoneManager)OsgiServices.importOsgiService(TimeZoneManager.class);
    }

    @Bean
    public DateConversionUtil dateConversionUtil(TimeZoneManager timeZoneManager) {
        return new DateConversionUtil(timeZoneManager);
    }

    @Bean
    public ActiveObjects activeObjects() {
        return (ActiveObjects)OsgiServices.importOsgiService(ActiveObjects.class);
    }

    @Bean
    public AoDataPipelineConfigDao aoDataPipelineConfigDao(ActiveObjects ao) {
        return new DefaultAoDataPipelineConfigDao(ao);
    }

    @Bean
    public AoDataPipelineJobDao aoDataPipelineJobDao(ActiveObjects ao) {
        return new DefaultAoDataPipelineJobDao(ao);
    }

    @Bean
    public ApplicationProperties applicationProperties() {
        return (ApplicationProperties)OsgiServices.importOsgiService(ApplicationProperties.class);
    }

    @Bean
    public AuditService auditService() {
        return (AuditService)OsgiServices.importOsgiService(AuditService.class);
    }

    @Bean
    public ClusterInfo clusterInfo() {
        return (ClusterInfo)OsgiServices.importOsgiService(ClusterInfo.class);
    }

    @Bean
    public OptOutEntitiesLookupService optOutResourcesLookupService() {
        return (OptOutEntitiesLookupService)OsgiServices.importOsgiService(OptOutEntitiesLookupService.class);
    }

    @Bean
    public OptOutEntitiesTransformationService optOutResourcesTransformationService() {
        return (OptOutEntitiesTransformationService)OsgiServices.importOsgiService(OptOutEntitiesTransformationService.class);
    }

    @Bean
    public CorePluginInfo corePluginInfo(PluginAccessor pluginAccessor) {
        return new CorePluginInfo(pluginAccessor);
    }

    @Bean
    public ConfigService configService(AoDataPipelineConfigDao aoDataPipelineConfigDao, ApplicationProperties applicationProperties, EventPublisherService eventPublisherService, AuditService auditService) {
        return new DefaultConfigService(aoDataPipelineConfigDao, applicationProperties, eventPublisherService, auditService);
    }

    @Bean
    public DiskSpaceValidator availableDiskSpaceValidator(PropertiesProvider propertiesProvider, I18nResolver i18nResolver) {
        return new DiskSpaceValidator(propertiesProvider, i18nResolver);
    }

    @Bean
    public DataExportOrchestrator dataExportService(AuditService auditService, ExportJobStateService exportJobStateService, DataExportJobExecutor exportJobExecutor, DatasetWriterFactory writerFactory, LogRecordMapper logRecordMapper, EventPublisherService eventPublisherService, RequestContext requestContext, ConfigService configService, ApplicationProperties applicationProperties, DiskSpaceValidator diskSpaceValidator, DatasetProvider datasetProvider, EntityOptOutService optOutService) {
        return new DefaultDataExportOrchestrator(auditService, exportJobStateService, exportJobExecutor, writerFactory, logRecordMapper, eventPublisherService, requestContext, configService, applicationProperties, diskSpaceValidator, datasetProvider, optOutService);
    }

    @Bean
    public DataPipelinePluginSystemProperties dataPipelinePluginSystemProperties() {
        return new DataPipelinePluginSystemProperties();
    }

    @Bean
    public DataExportJobExecutor dbExportJobExecutor(DataPipelinePluginSystemProperties dataPipelinePluginSystemProperties) {
        return new DataExportJobExecutor(dataPipelinePluginSystemProperties);
    }

    @Bean
    public DatasetWriterFactory writerFactory(FileSystemConfig fileSystemConfig, ValueConverter jsonValueConverter, PropertiesProvider propertiesProvider) {
        List<ValueConverter> mandatoryConverters = Collections.singletonList(jsonValueConverter);
        return new FilesystemDatasetWriterFactory(fileSystemConfig, mandatoryConverters, propertiesProvider);
    }

    @Bean
    public ExportJobStateService exportJobStateService(AuditService auditService, AoDataPipelineJobDao ajo, TransactionTemplate transactionTemplate, ClusterInfo clusterInfo) {
        return new DbExportJobStateService(auditService, ajo, transactionTemplate, clusterInfo);
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportLifecycleAwareDataExportOrchestrator(DataExportOrchestrator dataExportOrchestrator) {
        return OsgiServices.exportOsgiService((Object)dataExportOrchestrator, (ExportOptions)ExportOptions.as(LifecycleAware.class, (Class[])new Class[0]));
    }

    @Bean
    public EventPublisher eventPublisher() {
        return (EventPublisher)OsgiServices.importOsgiService(EventPublisher.class);
    }

    @Bean
    public FileSystemConfig fileSystemConfig(DateConversionUtil dateConversionUtil) {
        return new DatasetFileSystemConfig(FileFormat.CSV, dateConversionUtil);
    }

    @Bean
    public I18nResolver i18nResolver() {
        return (I18nResolver)OsgiServices.importOsgiService(I18nResolver.class);
    }

    @Bean
    public LicenseHandler licenseHandler() {
        return (LicenseHandler)OsgiServices.importOsgiService(LicenseHandler.class);
    }

    @Bean
    public LocaleResolver localeResolver() {
        return (LocaleResolver)OsgiServices.importOsgiService(LocaleResolver.class);
    }

    @Bean
    public LogRecordMapper logRecordMapper() {
        return new DefaultLogRecordMapper();
    }

    @Bean
    public PluginAccessor pluginAccessor() {
        return (PluginAccessor)OsgiServices.importOsgiService(PluginAccessor.class);
    }

    @Bean
    public UserManager userManager() {
        return (UserManager)OsgiServices.importOsgiService(UserManager.class);
    }

    @Bean
    public ValueConverter jsonValueConverter() {
        return new JsonValueConverter(new ObjectMapper());
    }

    @Bean
    public WebSudoManager webSudoManager() {
        return (WebSudoManager)OsgiServices.importOsgiService(WebSudoManager.class);
    }

    @Bean
    @Conditional(value={NonRefapp.class})
    public PropertiesProvider noopPropertiesProvider() {
        return (PropertiesProvider)OsgiServices.importOsgiService(PropertiesProvider.class);
    }

    @Bean
    @Conditional(value={RefappOnly.class})
    public PropertiesProvider propertiesProvider() {
        return new NoopPropertiesProvider();
    }

    @Bean
    public EventPublisherService eventPublisherService(EventPublisher eventPublisher, CorePluginInfo corePluginInfo) {
        return new ExportEventPublisherService(eventPublisher, corePluginInfo);
    }

    @Bean
    public SoyTemplateRenderer soyTemplateRenderer() {
        return (SoyTemplateRenderer)OsgiServices.importOsgiService(SoyTemplateRenderer.class);
    }

    @Bean
    public LoginUriProvider loginUriProvider() {
        return (LoginUriProvider)OsgiServices.importOsgiService(LoginUriProvider.class);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        return objectMapper;
    }

    @Bean
    public DarkFeatureManager darkFeatureManager() {
        return (DarkFeatureManager)OsgiServices.importOsgiService(DarkFeatureManager.class);
    }

    @Bean
    public ProductDataProvider productDataProvider(ApplicationProperties applicationProperties, TimeZoneManager timeZoneManager, LocaleResolver localeResolver, CorePluginInfo corePluginInfo) {
        return new ProductDataProvider(new ObjectMapper(), applicationProperties, timeZoneManager, localeResolver, corePluginInfo);
    }

    @Bean
    public KbArticleLinkResolver kbArticleLinkResolver(ApplicationProperties applicationProperties) throws IOException {
        return new KbArticleLinkResolver(applicationProperties);
    }

    @Bean
    public KbArticleLinkDataProvider kbArticleLinkDataProvider(KbArticleLinkResolver kbArticleLinkResolver) {
        return new KbArticleLinkDataProvider(new ObjectMapper(), kbArticleLinkResolver);
    }

    @Bean
    @Conditional(value={NonRefapp.class})
    public RequestContext requestContext() {
        return (RequestContext)OsgiServices.importOsgiService(RequestContext.class);
    }

    @Bean
    @Conditional(value={RefappOnly.class})
    public RequestContext noopRequestContext() {
        return new NoopRequestContext();
    }

    @Bean
    @Conditional(value={JiraOnly.class})
    public CustomFieldListService jiraCustomFieldsListService() {
        return (CustomFieldListService)OsgiServices.importOsgiService(CustomFieldListService.class);
    }

    @Bean
    public DiagnosticDescriptionTranslator diagnosticDescriptionTranslator(I18nResolver i18nResolver) {
        return new DiagnosticDescriptionTranslator(i18nResolver);
    }

    @Bean
    public DatasetProvider datasetProvider() {
        return (DatasetProvider)OsgiServices.importOsgiService(DatasetProvider.class);
    }

    @Bean
    public SchedulerService schedulerService() {
        return (SchedulerService)OsgiServices.importOsgiService(SchedulerService.class);
    }

    @Bean
    public ExportScheduleJobRunner exportScheduleJobRunner(DataExportOrchestrator dataExportOrchestrator, ScheduleConfigService scheduleConfigService, LicenseChecker licenseChecker, EventPublisherService eventPublisherService) {
        return new ExportScheduleJobRunner(dataExportOrchestrator, scheduleConfigService, licenseChecker, eventPublisherService);
    }

    @Bean
    public ExportScheduleService exportScheduleService(ExportScheduleJobRunner exportScheduleJobRunner, SchedulerService schedulerService) {
        return new DefaultExportScheduleService(exportScheduleJobRunner, schedulerService);
    }

    @Bean
    public ExportScheduleLauncher exportScheduleLauncher(ScheduleConfigService scheduleConfigService, ExportScheduleService exportScheduleService, EventPublisher eventPublisher, CorePluginInfo corePluginInfo, ApplicationProperties applicationProperties) {
        return new ExportScheduleLauncher(scheduleConfigService, exportScheduleService, eventPublisher, corePluginInfo, applicationProperties);
    }

    @Bean
    public ScheduleConfigService schedulerConfigService(AoDataPipelineConfigDao aoDataPipelineConfigDao, ObjectMapper objectMapper, AuditService auditService, SchedulerService schedulerService) {
        return new DefaultScheduleConfigService(aoDataPipelineConfigDao, objectMapper, schedulerService, auditService);
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportLifecycleAwareScheduler(ExportScheduleLauncher exportScheduleLauncher) {
        return OsgiServices.exportOsgiService((Object)exportScheduleLauncher, (ExportOptions)ExportOptions.as(LifecycleAware.class, (Class[])new Class[0]));
    }

    @Bean
    public LicenseChecker licenseChecker(LicenseHandler licenseHandler) {
        return new LicenseChecker(licenseHandler);
    }

    @Bean
    public TransactionTemplate transactionTemplate() {
        return (TransactionTemplate)OsgiServices.importOsgiService(TransactionTemplate.class);
    }

    @Bean
    public AoEntityOptOutIdentifierDao aoEntityOptOutIdentifierDao(ActiveObjects activeObjects) {
        return new DefaultAoEntityOptOutIdentifierDao(activeObjects);
    }

    @Bean
    public EntityOptOutService entityOptOutService(AoEntityOptOutIdentifierDao aoEntityOptOutIdentifierDao, OptOutEntitiesLookupService lookupService, OptOutEntitiesTransformationService transformationService, PropertiesProvider propertiesProvider, I18nResolver i18nResolver) {
        return new CountLimitedEntityOptOutService(new DefaultEntityOptOutService(aoEntityOptOutIdentifierDao, lookupService, transformationService), propertiesProvider, i18nResolver);
    }
}


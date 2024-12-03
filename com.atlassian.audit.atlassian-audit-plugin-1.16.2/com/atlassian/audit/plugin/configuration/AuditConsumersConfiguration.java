/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.audit.api.AuditConsumer
 *  com.atlassian.audit.spi.feature.DatabaseAuditingFeature
 *  com.atlassian.audit.spi.feature.FileAuditingFeature
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.audit.plugin.configuration;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.audit.ao.consumer.DatabaseAuditConsumer;
import com.atlassian.audit.ao.dao.AffectedObjectsSerializer;
import com.atlassian.audit.ao.dao.AoAuditEntityDao;
import com.atlassian.audit.ao.dao.AoAuditEntityMapper;
import com.atlassian.audit.ao.dao.AttributesSerializer;
import com.atlassian.audit.ao.dao.AuditEntityDao;
import com.atlassian.audit.ao.dao.AuditEntityMapper;
import com.atlassian.audit.ao.dao.AuditQueryMapper;
import com.atlassian.audit.ao.dao.ChangedValuesSerializer;
import com.atlassian.audit.ao.dao.JacksonAffectedObjectsSerializer;
import com.atlassian.audit.ao.dao.JacksonAttributesSerializer;
import com.atlassian.audit.ao.dao.JacksonChangedValuesSerializer;
import com.atlassian.audit.ao.service.CachedActionsService;
import com.atlassian.audit.ao.service.CachedCategoriesService;
import com.atlassian.audit.api.AuditConsumer;
import com.atlassian.audit.file.AuditRetentionFileConfigService;
import com.atlassian.audit.file.CachingRetentionFileConfigService;
import com.atlassian.audit.file.FileAuditConsumer;
import com.atlassian.audit.file.FileMessagePublisher;
import com.atlassian.audit.file.RotatingFileManager;
import com.atlassian.audit.plugin.configuration.PermissionsNotEnforced;
import com.atlassian.audit.plugin.configuration.PropertiesProvider;
import com.atlassian.audit.spi.feature.DatabaseAuditingFeature;
import com.atlassian.audit.spi.feature.FileAuditingFeature;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugins.osgi.javaconfig.ExportOptions;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import org.codehaus.jackson.map.ObjectMapper;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditConsumersConfiguration {
    @Bean(value={"consumerType-db"})
    public DatabaseAuditConsumer dbConsumer(AuditEntityDao auditEntityDao, CachedActionsService cachedActionsService, CachedCategoriesService cachedCategoriesService, DatabaseAuditingFeature databaseAuditingFeature) {
        return new DatabaseAuditConsumer(auditEntityDao, cachedActionsService, cachedCategoriesService, databaseAuditingFeature);
    }

    @Bean
    public RotatingFileManager getFilePathSupplier(ApplicationProperties appProperties, CachingRetentionFileConfigService cachingRetentionFileConfigService) {
        return new RotatingFileManager(appProperties, "log/audit", cachingRetentionFileConfigService);
    }

    @Bean
    public FileMessagePublisher rotatingFileHandler(RotatingFileManager filePathSupplier) {
        return new FileMessagePublisher(filePathSupplier);
    }

    @Bean
    public CachingRetentionFileConfigService cachingRetentionFileConfigService(EventPublisher eventPublisher, @PermissionsNotEnforced AuditRetentionFileConfigService retentionFileConfigService, PropertiesProvider propertiesProvider) {
        int coverageCacheExpiration = propertiesProvider.getInteger("plugin.audit.retention.file.configuration.expiration.seconds", 300);
        return new CachingRetentionFileConfigService(eventPublisher, retentionFileConfigService, coverageCacheExpiration);
    }

    @Bean(value={"consumerType-file"})
    public FileAuditConsumer fileConsumer(FileAuditingFeature fileAuditingFeature, FileMessagePublisher fileMessagePublisher) {
        return new FileAuditConsumer(fileAuditingFeature, fileMessagePublisher);
    }

    @Bean
    public ChangedValuesSerializer changedValuesSerializer(ObjectMapper objectMapper) {
        return new JacksonChangedValuesSerializer(objectMapper);
    }

    @Bean
    public AttributesSerializer attributesSerializer(ObjectMapper objectMapper) {
        return new JacksonAttributesSerializer(objectMapper);
    }

    @Bean
    public AffectedObjectsSerializer affectedObjectsSerializer(ObjectMapper objectMapper) {
        return new JacksonAffectedObjectsSerializer(objectMapper);
    }

    @Bean
    public AuditEntityMapper auditEntityMapper(ChangedValuesSerializer changedValuesSerializer, AttributesSerializer attributesSerializer, AffectedObjectsSerializer affectedObjectsSerializer) {
        return new AuditEntityMapper(changedValuesSerializer, attributesSerializer, affectedObjectsSerializer);
    }

    @Bean
    public AoAuditEntityMapper aoAuditEntityMapper(ChangedValuesSerializer changedValuesSerializer, AttributesSerializer attributesSerializer, AffectedObjectsSerializer affectedObjectsSerializer) {
        return new AoAuditEntityMapper(changedValuesSerializer, attributesSerializer, affectedObjectsSerializer);
    }

    @Bean
    public AuditEntityDao auditEntityDao(ActiveObjects ao, TransactionTemplate transactionTemplate, AuditQueryMapper auditQueryMapper, AuditEntityMapper auditEntityMapper, AoAuditEntityMapper aoAuditEntityMapper, PropertiesProvider propertiesProvider) {
        return new AoAuditEntityDao(ao, transactionTemplate, auditQueryMapper, auditEntityMapper, aoAuditEntityMapper, propertiesProvider);
    }

    @Bean
    public AuditQueryMapper auditQueryMapper() {
        return new AuditQueryMapper();
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportDbAuditConsumer(@Qualifier(value="consumerType-db") AuditConsumer auditConsumer) {
        return OsgiServices.exportOsgiService(auditConsumer, ExportOptions.as(AuditConsumer.class, new Class[0]).withProperty("consumerType", "db"));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportFileAuditConsumer(@Qualifier(value="consumerType-file") AuditConsumer auditConsumer) {
        return OsgiServices.exportOsgiService(auditConsumer, ExportOptions.as(AuditConsumer.class, new Class[0]).withProperty("consumerType", "file"));
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.troubleshooting.confluence.spring;

import com.atlassian.confluence.compat.struts2.servletactioncontext.ServletActionContextCompatManager;
import com.atlassian.plugins.osgi.javaconfig.ExportOptions;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.troubleshooting.api.healthcheck.LicenseService;
import com.atlassian.troubleshooting.confluence.ConfluenceApplicationInfo;
import com.atlassian.troubleshooting.confluence.ConfluenceClusterMessagingProvider;
import com.atlassian.troubleshooting.confluence.ConfluenceClusterService;
import com.atlassian.troubleshooting.confluence.ConfluenceFileSanitizerPatternManager;
import com.atlassian.troubleshooting.confluence.ConfluenceMailUtility;
import com.atlassian.troubleshooting.confluence.healthcheck.ConfluenceDatabaseService;
import com.atlassian.troubleshooting.confluence.healthcheck.ConfluenceIndexInfoService;
import com.atlassian.troubleshooting.confluence.healthcheck.ConfluenceLicenseService;
import com.atlassian.troubleshooting.confluence.healthcheck.conditions.HasSynchronyCondition;
import com.atlassian.troubleshooting.confluence.healthcheck.conditions.LicenseLimitFeatureFlagCondition;
import com.atlassian.troubleshooting.confluence.healthcheck.conditions.MySQLCondition;
import com.atlassian.troubleshooting.confluence.healthcheck.conditions.OpenFilesCondition;
import com.atlassian.troubleshooting.confluence.healthcheck.conditions.SQLServerCondition;
import com.atlassian.troubleshooting.confluence.healthcheck.database.mysql.CharacterSetCheck;
import com.atlassian.troubleshooting.confluence.healthcheck.database.mysql.CollationCheck;
import com.atlassian.troubleshooting.confluence.healthcheck.database.mysql.InnoDBLogFileSizeCheck;
import com.atlassian.troubleshooting.confluence.healthcheck.database.mysql.MaxAllowedPacketsCheck;
import com.atlassian.troubleshooting.confluence.healthcheck.database.mysql.SqlModeCheck;
import com.atlassian.troubleshooting.confluence.healthcheck.database.mysql.StorageEngineCheck;
import com.atlassian.troubleshooting.confluence.healthcheck.database.sqlserver.JTDSDriverUpgradedCheck;
import com.atlassian.troubleshooting.confluence.healthcheck.directory.internal.AuthenticatorProvider;
import com.atlassian.troubleshooting.confluence.healthcheck.directory.internal.InternalAdminCheck;
import com.atlassian.troubleshooting.confluence.healthcheck.directory.internal.InternalAdminCheckFallback;
import com.atlassian.troubleshooting.confluence.healthcheck.filesystem.OpenFilesLimitCheck;
import com.atlassian.troubleshooting.confluence.healthcheck.jdk.JdkHealthCheck;
import com.atlassian.troubleshooting.confluence.healthcheck.license.LicenseHealthCheck;
import com.atlassian.troubleshooting.confluence.healthcheck.support.CollabEditingModeSupportHealthCheck;
import com.atlassian.troubleshooting.confluence.healthcheck.support.HsqlHealthCheck;
import com.atlassian.troubleshooting.confluence.healthcheck.support.LuceneHealthCheck;
import com.atlassian.troubleshooting.confluence.jfr.ConfluenceJfrServiceProductSupport;
import com.atlassian.troubleshooting.confluence.pup.ConfluenceChecksumResource;
import com.atlassian.troubleshooting.confluence.pup.ConfluenceModzDetection;
import com.atlassian.troubleshooting.confluence.pup.ConfluencePlatformsChecker;
import com.atlassian.troubleshooting.confluence.pup.ConfluencePupPlatformAccessorImp;
import com.atlassian.troubleshooting.confluence.pup.ServletContextConfig;
import com.atlassian.troubleshooting.confluence.spring.ConfluenceSpecificImportedOsgiServiceBeans;
import com.atlassian.troubleshooting.confluence.spring.ConfluenceSupportZipBundleBeans;
import com.atlassian.troubleshooting.confluence.task.ConfluenceTaskMonitorRepositoryFactory;
import com.atlassian.troubleshooting.confluence.whisper.OverrideLicenseHealthCheckCondition;
import com.atlassian.troubleshooting.confluence.zip.ConfluenceClusteredSupportZipService;
import com.atlassian.troubleshooting.confluence.zip.ConfluenceNodeLocalSupportZipTaskInfoDtoFactory;
import com.atlassian.troubleshooting.confluence.zip.SupportZipEventListener;
import com.atlassian.troubleshooting.healthcheck.checks.vuln.BaseProductCveProvider;
import com.atlassian.troubleshooting.healthcheck.checks.vuln.CveExternalResource;
import com.atlassian.troubleshooting.healthcheck.checks.vuln.CveProvider;
import com.atlassian.troubleshooting.healthcheck.spring.CommonHealthcheckBeans;
import com.atlassian.troubleshooting.jfr.config.JfrServiceProductSupport;
import com.atlassian.troubleshooting.preupgrade.ConfluenceClusteredUpgradePathSectionFactory;
import com.atlassian.troubleshooting.preupgrade.ConfluenceNonClusteredUpgradePathSectionFactory;
import com.atlassian.troubleshooting.spring.CommonBeans;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value={CommonBeans.class, CommonHealthcheckBeans.class, ConfluenceSpecificImportedOsgiServiceBeans.class, ConfluenceSupportZipBundleBeans.class, HasSynchronyCondition.class, MySQLCondition.class, SQLServerCondition.class, CharacterSetCheck.class, CollationCheck.class, JTDSDriverUpgradedCheck.class, InternalAdminCheck.class, InternalAdminCheckFallback.class, JdkHealthCheck.class, HsqlHealthCheck.class, LuceneHealthCheck.class, ConfluenceDatabaseService.class, ConfluenceIndexInfoService.class, ConfluenceLicenseService.class, ConfluencePlatformsChecker.class, ServletContextConfig.class, ConfluenceTaskMonitorRepositoryFactory.class, OverrideLicenseHealthCheckCondition.class, ConfluenceClusteredSupportZipService.class, SupportZipEventListener.class, ConfluenceApplicationInfo.class, ConfluenceClusterMessagingProvider.class, ConfluenceMailUtility.class, ConfluenceClusteredUpgradePathSectionFactory.class, ConfluenceNonClusteredUpgradePathSectionFactory.class, ConfluenceClusterService.class, OpenFilesCondition.class, InnoDBLogFileSizeCheck.class, MaxAllowedPacketsCheck.class, SqlModeCheck.class, StorageEngineCheck.class, AuthenticatorProvider.class, OpenFilesLimitCheck.class, LicenseHealthCheck.class, CollabEditingModeSupportHealthCheck.class, ConfluenceChecksumResource.class, ConfluenceModzDetection.class, ConfluencePupPlatformAccessorImp.class, ConfluenceNodeLocalSupportZipTaskInfoDtoFactory.class, ConfluenceFileSanitizerPatternManager.class, ConfluenceJfrServiceProductSupport.class, LicenseLimitFeatureFlagCondition.class})
public class ConfluenceSpecificBeans {
    @Bean
    public FactoryBean<ServiceRegistration> exportConfluenceLicenseService(ConfluenceLicenseService licenseService) {
        return OsgiServices.exportOsgiService(licenseService, ExportOptions.as(LicenseService.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportSupportZipEventListener(SupportZipEventListener supportZipEventListener) {
        return OsgiServices.exportOsgiService(supportZipEventListener, ExportOptions.as(LifecycleAware.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportConfluenceClusterMessagingProvider(ConfluenceClusterMessagingProvider messagingProvider) {
        return OsgiServices.exportOsgiService(messagingProvider, ExportOptions.as(LifecycleAware.class, new Class[0]));
    }

    @Bean
    public CveProvider confluenceCvesProvider(SupportApplicationInfo info) {
        return new BaseProductCveProvider(CveExternalResource.CONFLUENCE_CVES, info);
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportJfrEnabled(JfrServiceProductSupport jfrServiceProductSupport) {
        return OsgiServices.exportOsgiService(jfrServiceProductSupport, ExportOptions.as(LifecycleAware.class, new Class[0]));
    }

    @Bean
    public ServletActionContextCompatManager servletActionContextCompatManager() {
        return new ServletActionContextCompatManager();
    }
}


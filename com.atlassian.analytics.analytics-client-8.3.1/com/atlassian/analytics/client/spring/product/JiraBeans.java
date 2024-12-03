/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.listener.ProductAnalyticsEventListener
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.jira.application.ApplicationRoleManager
 *  com.atlassian.jira.bc.license.JiraLicenseService
 *  com.atlassian.jira.bc.user.search.UserSearchService
 *  com.atlassian.jira.cluster.ClusterManager
 *  com.atlassian.jira.config.IssueTypeManager
 *  com.atlassian.jira.config.StatusManager
 *  com.atlassian.jira.event.type.EventTypeManager
 *  com.atlassian.jira.issue.IssueManager
 *  com.atlassian.jira.license.JiraLicenseManager
 *  com.atlassian.jira.ofbiz.OfBizDelegator
 *  com.atlassian.jira.project.ProjectManager
 *  com.atlassian.jira.workflow.WorkflowManager
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.web.context.HttpContext
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Conditional
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.analytics.client.spring.product;

import com.atlassian.analytics.api.listener.ProductAnalyticsEventListener;
import com.atlassian.analytics.client.base.BaseDataLogger;
import com.atlassian.analytics.client.base.JiraBaseDataLogger;
import com.atlassian.analytics.client.cluster.ClusterInformationProvider;
import com.atlassian.analytics.client.cluster.JiraClusterInformationProvider;
import com.atlassian.analytics.client.configuration.AnalyticsConfig;
import com.atlassian.analytics.client.detect.JiraSystemShutdownDetector;
import com.atlassian.analytics.client.detect.ProgrammaticAnalyticsDetector;
import com.atlassian.analytics.client.detect.SalProgrammaticAnalyticsDetector;
import com.atlassian.analytics.client.detect.SystemShutdownDetector;
import com.atlassian.analytics.client.eventfilter.whitelist.WhitelistFilter;
import com.atlassian.analytics.client.extractor.JiraPropertyExtractor;
import com.atlassian.analytics.client.extractor.PropertyExtractor;
import com.atlassian.analytics.client.license.JiraLicenseProvider;
import com.atlassian.analytics.client.license.LicenseProvider;
import com.atlassian.analytics.client.listener.JiraEventListener;
import com.atlassian.analytics.client.properties.AnalyticsPropertyService;
import com.atlassian.analytics.client.properties.DefaultPropertyService;
import com.atlassian.analytics.client.sen.DefaultSenProvider;
import com.atlassian.analytics.client.sen.SenProvider;
import com.atlassian.analytics.client.session.SalSessionIdProvider;
import com.atlassian.analytics.client.session.SessionIdProvider;
import com.atlassian.analytics.client.spring.shared.SharedExports;
import com.atlassian.analytics.client.upload.PeriodicEventUploaderScheduler;
import com.atlassian.analytics.client.uuid.ProductUUIDProvider;
import com.atlassian.cache.CacheManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.application.ApplicationRoleManager;
import com.atlassian.jira.bc.license.JiraLicenseService;
import com.atlassian.jira.bc.user.search.UserSearchService;
import com.atlassian.jira.cluster.ClusterManager;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.config.StatusManager;
import com.atlassian.jira.event.type.EventTypeManager;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.license.JiraLicenseManager;
import com.atlassian.jira.ofbiz.OfBizDelegator;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.workflow.WorkflowManager;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.JiraOnly;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.web.context.HttpContext;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(value={JiraOnly.class})
public class JiraBeans {
    @Bean
    public AnalyticsConfig analyticsConfig(PluginSettingsFactory pluginSettingsFactory, EventPublisher eventPublisher, CacheManager cacheManager) {
        return new AnalyticsConfig(pluginSettingsFactory, eventPublisher, cacheManager);
    }

    @Bean
    public AnalyticsPropertyService analyticsPropertyService(ApplicationProperties applicationProperties) {
        return new DefaultPropertyService(applicationProperties);
    }

    @Bean
    public BaseDataLogger baseDataLogger(EventPublisher eventPublisher, JiraLicenseManager jiraLicenseManager, StatusManager statusManager, IssueTypeManager issueTypeManager, ProjectManager projectManager, OfBizDelegator ofBizDelegator) {
        return new JiraBaseDataLogger(eventPublisher, jiraLicenseManager, statusManager, issueTypeManager, projectManager, ofBizDelegator);
    }

    @Bean
    public ClusterInformationProvider clusterInformationProvider(ClusterManager clusterManager) {
        return new JiraClusterInformationProvider(clusterManager);
    }

    @Bean
    public JiraEventListener jiraEventListener(EventPublisher eventPublisher, ProductAnalyticsEventListener productAnalyticsEventListener, PeriodicEventUploaderScheduler periodicEventUploaderScheduler, WhitelistFilter whitelistFilter, ProductUUIDProvider productUUIDProvider) {
        return new JiraEventListener(eventPublisher, productAnalyticsEventListener, periodicEventUploaderScheduler, whitelistFilter, productUUIDProvider);
    }

    @Bean
    public LicenseProvider licenseProvider(JiraLicenseManager jiraLicenseManager) {
        return new JiraLicenseProvider(jiraLicenseManager);
    }

    @Bean
    public ProgrammaticAnalyticsDetector programmaticAnalyticsDetector(DarkFeatureManager darkFeatureManager) {
        return new SalProgrammaticAnalyticsDetector(darkFeatureManager);
    }

    @Bean
    public PropertyExtractor propertyExtractor(EventTypeManager eventTypeManager, IssueTypeManager issueTypeManager, ProjectManager projectManager, ApplicationRoleManager applicationRoleManager) {
        return new JiraPropertyExtractor(eventTypeManager, issueTypeManager, projectManager, applicationRoleManager);
    }

    @Bean
    public SenProvider senProvider(LicenseHandler licenseHandler) {
        return new DefaultSenProvider(licenseHandler);
    }

    @Bean
    public SessionIdProvider sessionIdProvider(HttpContext httpContext) {
        return new SalSessionIdProvider(httpContext);
    }

    @Bean
    public SystemShutdownDetector systemShutdownDetector() {
        return new JiraSystemShutdownDetector();
    }

    @Bean
    public ApplicationRoleManager applicationRoleManager() {
        return OsgiServices.importOsgiService(ApplicationRoleManager.class);
    }

    @Bean
    public ClusterManager clusterManager() {
        return OsgiServices.importOsgiService(ClusterManager.class);
    }

    @Bean
    public EventTypeManager eventTypeManager() {
        return OsgiServices.importOsgiService(EventTypeManager.class);
    }

    @Bean
    public IssueManager issueManager() {
        return OsgiServices.importOsgiService(IssueManager.class);
    }

    @Bean
    public IssueTypeManager issueTypeManager() {
        return OsgiServices.importOsgiService(IssueTypeManager.class);
    }

    @Bean
    public JiraLicenseManager jiraLicenseManager() {
        return OsgiServices.importOsgiService(JiraLicenseManager.class);
    }

    @Bean
    public JiraLicenseService jiraLicenseService() {
        return OsgiServices.importOsgiService(JiraLicenseService.class);
    }

    @Bean
    public OfBizDelegator ofBizDelegator() {
        return OsgiServices.importOsgiService(OfBizDelegator.class);
    }

    @Bean
    public ProjectManager projectManager() {
        return OsgiServices.importOsgiService(ProjectManager.class);
    }

    @Bean
    public StatusManager statusManager() {
        return OsgiServices.importOsgiService(StatusManager.class);
    }

    @Bean
    public UserSearchService userSearchService() {
        return OsgiServices.importOsgiService(UserSearchService.class);
    }

    @Bean
    public WorkflowManager workflowManager() {
        return OsgiServices.importOsgiService(WorkflowManager.class);
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportJiraEventListener(JiraEventListener jiraEventListener) {
        return SharedExports.exportAsLifecycleAware(jiraEventListener);
    }
}


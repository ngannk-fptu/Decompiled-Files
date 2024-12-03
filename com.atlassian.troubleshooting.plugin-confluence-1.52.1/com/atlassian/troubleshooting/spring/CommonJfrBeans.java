/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Conditional
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.troubleshooting.spring;

import com.atlassian.plugins.osgi.javaconfig.ExportOptions;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.troubleshooting.api.supportzip.SupportZipBundle;
import com.atlassian.troubleshooting.healthcheck.impl.DefaultLocalHomeFileSystemInfo;
import com.atlassian.troubleshooting.jfr.cluster.ClusterJfrRecordingRestartListener;
import com.atlassian.troubleshooting.jfr.cluster.ClusterJfrStateListener;
import com.atlassian.troubleshooting.jfr.config.JfrConfigurationRegistry;
import com.atlassian.troubleshooting.jfr.config.JfrProperties;
import com.atlassian.troubleshooting.jfr.config.JfrPropertiesFactory;
import com.atlassian.troubleshooting.jfr.config.JfrPropertyStore;
import com.atlassian.troubleshooting.jfr.listener.JfrDumpCreatedListener;
import com.atlassian.troubleshooting.jfr.listener.JfrFeatureFlagStateListener;
import com.atlassian.troubleshooting.jfr.listener.JfrLocalStateChangedListener;
import com.atlassian.troubleshooting.jfr.listener.JfrPropertiesChangedListener;
import com.atlassian.troubleshooting.jfr.manager.DefaultJfrRecordingManager;
import com.atlassian.troubleshooting.jfr.manager.JfrRecordingManager;
import com.atlassian.troubleshooting.jfr.manager.NoopJfrRecordingManager;
import com.atlassian.troubleshooting.jfr.scheduler.JfrDumpingScheduler;
import com.atlassian.troubleshooting.jfr.scheduler.JfrSettingsAnalyticsSendingScheduler;
import com.atlassian.troubleshooting.jfr.service.DefaultJfrEventExtractorService;
import com.atlassian.troubleshooting.jfr.service.DefaultJfrPropertiesService;
import com.atlassian.troubleshooting.jfr.service.DefaultJfrRecordingCleanUpService;
import com.atlassian.troubleshooting.jfr.service.DefaultJfrRecordingService;
import com.atlassian.troubleshooting.jfr.service.DefaultJfrSettingsService;
import com.atlassian.troubleshooting.jfr.service.JfrAlwaysOnRecordingService;
import com.atlassian.troubleshooting.jfr.service.JfrSettingsService;
import com.atlassian.troubleshooting.jfr.service.NoopJfrPropertiesService;
import com.atlassian.troubleshooting.jfr.supportzip.JfrDumpBundle;
import com.atlassian.troubleshooting.jfr.util.JfrConditionUtils;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value={JfrPropertiesFactory.class, JfrPropertyStore.class})
public class CommonJfrBeans {
    @Bean
    public JfrProperties jfrProperties(JfrPropertiesFactory jfrPropertiesFactory) {
        return jfrPropertiesFactory.create();
    }

    @Conditional(value={JfrConditionUtils.OnJfrUnsupportedCondition.class})
    @Configuration
    @Import(value={NoopJfrRecordingManager.class, NoopJfrPropertiesService.class, JfrSettingsAnalyticsSendingScheduler.class})
    public static class JfrFallBackConfiguration {
        @Bean
        public FactoryBean<ServiceRegistration> exportJfrRecordingManager(JfrRecordingManager jfrRecordingManager) {
            return OsgiServices.exportOsgiService(jfrRecordingManager, ExportOptions.as(LifecycleAware.class, new Class[0]));
        }

        @Bean
        public FactoryBean<ServiceRegistration> exportJfrSettingsAnalyticsSendingScheduler(JfrSettingsAnalyticsSendingScheduler jfrSettingsAnalyticsSendingScheduler) {
            return OsgiServices.exportOsgiService(jfrSettingsAnalyticsSendingScheduler, ExportOptions.as(LifecycleAware.class, new Class[0]));
        }
    }

    @Conditional(value={JfrConditionUtils.OnJfrSupportedCondition.class})
    @Configuration
    @Import(value={DefaultJfrRecordingService.class, JfrConfigurationRegistry.class, DefaultJfrSettingsService.class, DefaultJfrPropertiesService.class, DefaultJfrRecordingManager.class, JfrAlwaysOnRecordingService.class, DefaultJfrRecordingCleanUpService.class, DefaultJfrEventExtractorService.class, JfrDumpingScheduler.class, JfrSettingsAnalyticsSendingScheduler.class, JfrDumpBundle.class, JfrPropertiesChangedListener.class, JfrFeatureFlagStateListener.class, JfrLocalStateChangedListener.class, JfrDumpCreatedListener.class, ClusterJfrStateListener.class, ClusterJfrRecordingRestartListener.class, DefaultLocalHomeFileSystemInfo.class})
    public static class JfrConfiguration {
        @Bean
        public FactoryBean<ServiceRegistration> exportJfrRecordingManager(JfrRecordingManager jfrRecordingManager) {
            return OsgiServices.exportOsgiService(jfrRecordingManager, ExportOptions.as(LifecycleAware.class, new Class[0]));
        }

        @Bean
        public FactoryBean<ServiceRegistration> exportJfrSettingsService(JfrSettingsService jfrSettingsService) {
            return OsgiServices.exportOsgiService(jfrSettingsService, ExportOptions.as(LifecycleAware.class, new Class[0]));
        }

        @Bean
        public FactoryBean<ServiceRegistration> exportJfrDumpBundle(JfrDumpBundle jfrDumpBundle) {
            return OsgiServices.exportOsgiService(jfrDumpBundle, ExportOptions.as(SupportZipBundle.class, new Class[0]));
        }

        @Bean
        public FactoryBean<ServiceRegistration> exportJfrDumpingScheduler(JfrDumpingScheduler jfrDumpingScheduler) {
            return OsgiServices.exportOsgiService(jfrDumpingScheduler, ExportOptions.as(LifecycleAware.class, new Class[0]));
        }

        @Bean
        public FactoryBean<ServiceRegistration> exportJfrSettingsAnalyticsSendingScheduler(JfrSettingsAnalyticsSendingScheduler jfrSettingsAnalyticsSendingScheduler) {
            return OsgiServices.exportOsgiService(jfrSettingsAnalyticsSendingScheduler, ExportOptions.as(LifecycleAware.class, new Class[0]));
        }

        @Bean
        public FactoryBean<ServiceRegistration> exportJfrPropertiesChangedListener(JfrPropertiesChangedListener listener) {
            return OsgiServices.exportOsgiService(listener, ExportOptions.as(LifecycleAware.class, new Class[0]));
        }

        @Bean
        public FactoryBean<ServiceRegistration> exportJfrClusterStateListener(JfrFeatureFlagStateListener listener) {
            return OsgiServices.exportOsgiService(listener, ExportOptions.as(LifecycleAware.class, new Class[0]));
        }

        @Bean
        public FactoryBean<ServiceRegistration> exportJfrLocalStateChangedListener(JfrLocalStateChangedListener listener) {
            return OsgiServices.exportOsgiService(listener, ExportOptions.as(LifecycleAware.class, new Class[0]));
        }

        @Bean
        public FactoryBean<ServiceRegistration> exportJfrDumpCreatedListener(JfrDumpCreatedListener listener) {
            return OsgiServices.exportOsgiService(listener, ExportOptions.as(LifecycleAware.class, new Class[0]));
        }

        @Bean
        public FactoryBean<ServiceRegistration> exportClusterJfrStateListener(ClusterJfrStateListener listener) {
            return OsgiServices.exportOsgiService(listener, ExportOptions.as(LifecycleAware.class, new Class[0]));
        }

        @Bean
        public FactoryBean<ServiceRegistration> exportClusterJfrRecordingRestartListener(ClusterJfrRecordingRestartListener listener) {
            return OsgiServices.exportOsgiService(listener, ExportOptions.as(LifecycleAware.class, new Class[0]));
        }
    }
}


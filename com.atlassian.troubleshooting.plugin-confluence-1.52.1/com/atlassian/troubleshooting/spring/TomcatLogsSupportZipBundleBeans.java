/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.apache.commons.lang3.StringUtils
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.troubleshooting.spring;

import com.atlassian.plugins.osgi.javaconfig.ExportOptions;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.troubleshooting.api.supportzip.BundleCategory;
import com.atlassian.troubleshooting.api.supportzip.SupportZipBundle;
import com.atlassian.troubleshooting.spring.CommonSupportZipBundleBeans;
import com.atlassian.troubleshooting.stp.salext.bundle.BundleManifest;
import com.atlassian.troubleshooting.stp.salext.bundle.FileSetsBundle;
import com.atlassian.troubleshooting.stp.salext.bundle.fileset.RegexFileSet;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatLogsSupportZipBundleBeans {
    public static final Pattern TOMCAT_ACCESS_LOG_PATTERN = Pattern.compile(".*access_log.*");
    private static final Pattern TOMCAT_LOG_PATTERN = Pattern.compile(".*gc.*\\.log.*|^.*\\.(log|out)$");
    private final String tomcatLogsDir = CommonSupportZipBundleBeans.findTomcatFileOrDirectory("logs");
    @Autowired
    private I18nResolver i18nResolver;

    @Bean
    public FactoryBean<ServiceRegistration> exportTomcatLogsBundle() {
        if (StringUtils.isNotBlank((CharSequence)this.tomcatLogsDir)) {
            return OsgiServices.exportOsgiService(FileSetsBundle.builder(BundleManifest.TOMCAT_LOGS, "stp.zip.include.tomcat.logs", "stp.zip.include.tomcat.logs.description", BundleCategory.LOGS, this.i18nResolver).fileSet(new RegexFileSet.Builder().withDirectory(this.tomcatLogsDir).withPattern(TOMCAT_LOG_PATTERN).build()).build(), ExportOptions.as(SupportZipBundle.class, new Class[0]));
        }
        return null;
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportTomcatAccessLogsBundle() {
        if (StringUtils.isNotBlank((CharSequence)this.tomcatLogsDir)) {
            return OsgiServices.exportOsgiService(FileSetsBundle.builder(BundleManifest.TOMCAT_ACCESS_LOGS, "stp.zip.include.tomcat.access.logs", "stp.zip.include.tomcat.access.logs.description", BundleCategory.LOGS, this.i18nResolver).fileSet(new RegexFileSet.Builder().withDirectory(this.tomcatLogsDir).withPattern(TOMCAT_ACCESS_LOG_PATTERN).build()).build(), ExportOptions.as(SupportZipBundle.class, new Class[0]));
        }
        return null;
    }
}


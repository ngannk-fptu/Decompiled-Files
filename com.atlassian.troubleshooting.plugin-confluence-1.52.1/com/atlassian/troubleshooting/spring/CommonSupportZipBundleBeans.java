/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.troubleshooting.spring;

import com.atlassian.plugins.osgi.javaconfig.ExportOptions;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.troubleshooting.api.supportzip.SupportZipBundle;
import com.atlassian.troubleshooting.stp.salext.bundle.ApplicationPropertiesInfoBundle;
import com.atlassian.troubleshooting.stp.salext.bundle.OsgiSupportZipBundleAccessor;
import com.atlassian.troubleshooting.stp.salext.bundle.ThreadDumpBundle;
import java.io.File;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value={ThreadDumpBundle.class, ApplicationPropertiesInfoBundle.class, OsgiSupportZipBundleAccessor.class})
public class CommonSupportZipBundleBeans {
    public static final String ZIP_INCLUDE_CACHE_CFG = "stp.zip.include.cache.cfg";
    public static final String ZIP_INCLUDE_CACHE_CFG_DESCRIPTION = "stp.zip.include.cache.cfg.description";
    public static final String ZIP_INCLUDE_TOMCAT_CONF = "stp.zip.include.tomcat.conf";
    public static final String ZIP_INCLUDE_TOMCAT_CONF_DESCRIPTION = "stp.zip.include.tomcat.conf.description";
    public static final String ZIP_INCLUDE_TOMCAT_LOGS = "stp.zip.include.tomcat.logs";
    public static final String ZIP_INCLUDE_TOMCAT_LOGS_DESCRIPTION = "stp.zip.include.tomcat.logs.description";
    public static final String ZIP_INCLUDE_TOMCAT_ACCESS_LOGS = "stp.zip.include.tomcat.access.logs";
    public static final String ZIP_INCLUDE_TOMCAT_ACCESS_LOGS_DESCRIPTION = "stp.zip.include.tomcat.access.logs.description";

    @Bean
    public FactoryBean<ServiceRegistration> exportThreadDumpBundle(ThreadDumpBundle threadDumpBundle) {
        return OsgiServices.exportOsgiService(threadDumpBundle, ExportOptions.as(SupportZipBundle.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportApplicationPropertiesInfoBundle(ApplicationPropertiesInfoBundle applicationPropertiesInfoBundle) {
        return OsgiServices.exportOsgiService(applicationPropertiesInfoBundle, ExportOptions.as(SupportZipBundle.class, new Class[0]));
    }

    public static String findTomcatFileOrDirectory(String fileOrDirectoryName) {
        String catalinaBase = System.getProperty("catalina.base");
        File file = new File(catalinaBase, fileOrDirectoryName);
        if (file.exists()) {
            return file.getAbsolutePath();
        }
        String catalinaHome = System.getProperty("catalina.home");
        file = new File(catalinaHome, fileOrDirectoryName);
        if (file.exists()) {
            return file.getAbsolutePath();
        }
        String workingDirectory = System.getProperty("working.dir");
        file = new File(workingDirectory + "../", fileOrDirectoryName);
        if (file.exists()) {
            return file.getAbsolutePath();
        }
        return null;
    }
}


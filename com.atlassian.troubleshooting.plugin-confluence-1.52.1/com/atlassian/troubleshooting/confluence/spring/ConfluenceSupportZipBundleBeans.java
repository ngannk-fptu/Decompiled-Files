/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.troubleshooting.confluence.spring;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.plugins.osgi.javaconfig.ExportOptions;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.troubleshooting.api.supportzip.BundleCategory;
import com.atlassian.troubleshooting.api.supportzip.SupportZipBundle;
import com.atlassian.troubleshooting.confluence.ConfluenceApplicationInfo;
import com.atlassian.troubleshooting.confluence.bundle.ConfluenceCustomisationFileBundle;
import com.atlassian.troubleshooting.spring.CommonSupportZipBundleBeans;
import com.atlassian.troubleshooting.spring.TomcatLogsSupportZipBundleBeans;
import com.atlassian.troubleshooting.stp.salext.bundle.AuthenticationConfigurationFileBundle;
import com.atlassian.troubleshooting.stp.salext.bundle.BundleManifest;
import com.atlassian.troubleshooting.stp.salext.bundle.FileSetsBundle;
import com.atlassian.troubleshooting.stp.salext.bundle.fileset.ExactFileSet;
import com.atlassian.troubleshooting.stp.salext.bundle.fileset.RegexFileSet;
import java.util.regex.Pattern;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(value={ConfluenceCustomisationFileBundle.class, TomcatLogsSupportZipBundleBeans.class})
public class ConfluenceSupportZipBundleBeans {
    private static final String ZIP_INCLUDE_CONFLUENCE_LOGS_DESCRIPTION = "stp.zip.include.confluence.logs.description";
    private static final String ZIP_INCLUDE_CONFLUENCE_LOGS = "stp.zip.include.confluence.logs";
    private static final String ZIP_INCLUDE_SYNCHRONY_CFG = "stp.zip.include.synchrony.cfg";
    private static final String ZIP_INCLUDE_SYNCHRONY_CFG_DESCRIPTION = "stp.zip.include.synchrony.cfg.description";
    private static final String ZIP_EXCLUDED_SYNCHRONY_CFG_REASON_CLUSTER = "atst.zip.exclude.synchrony.cfg";
    private final String webInfDir = CommonSupportZipBundleBeans.findTomcatFileOrDirectory("confluence/WEB-INF");
    private final String webInfClassesDir = this.webInfDir + "/classes";
    private final String binDir = CommonSupportZipBundleBeans.findTomcatFileOrDirectory("bin");
    private final I18nResolver i18nResolver;
    private final ConfluenceApplicationInfo info;

    @Autowired
    public ConfluenceSupportZipBundleBeans(I18nResolver i18nResolver, ConfluenceApplicationInfo info) {
        this.i18nResolver = i18nResolver;
        this.info = info;
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportTomactConfigBundle() {
        return OsgiServices.exportOsgiService(FileSetsBundle.builder(BundleManifest.TOMCAT_CONFIG, "stp.zip.include.tomcat.conf", "stp.zip.include.tomcat.conf.description", BundleCategory.CONFIG, this.i18nResolver).fileSet(RegexFileSet.fromDirectoryPath(CommonSupportZipBundleBeans.findTomcatFileOrDirectory("conf"), Pattern.compile("^.*\\.(xml|properties|policy)$"))).build(), ExportOptions.as(SupportZipBundle.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportConfluenceCustomisationFileBundle(ConfluenceCustomisationFileBundle confluenceCustomisationFileBundle) {
        return OsgiServices.exportOsgiService(confluenceCustomisationFileBundle, ExportOptions.as(SupportZipBundle.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportAuthenticationConfigurationFileBundle(CrowdDirectoryService dirService) {
        return OsgiServices.exportOsgiService(new AuthenticationConfigurationFileBundle(this.info, dirService, this.i18nResolver, this.webInfClassesDir + "/seraph-config.xml", this.webInfClassesDir + "/seraph-paths.xml", this.webInfClassesDir + "/crowd.properties"), ExportOptions.as(SupportZipBundle.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportApplicationConfigBundle() {
        FileSetsBundle.Builder builder = FileSetsBundle.builder(BundleManifest.APPLICATION_CONFIG, "stp.zip.include.confluence.cfg", "stp.zip.include.confluence.cfg.description", BundleCategory.CONFIG, this.i18nResolver).fileSet(ExactFileSet.ofPaths(this.webInfClassesDir + "/confluence-init.properties", this.info.getApplicationHome() + "/confluence.cfg.xml", this.webInfDir + "/web.xml", this.webInfClassesDir + "/log4j.properties", this.webInfClassesDir + "/log4j-diagnostic.properties", this.webInfClassesDir + "/logging.properties", this.binDir + "/setenv.sh", this.binDir + "/setenv.bat", this.binDir + "/setclasspath.sh", this.binDir + "/setclasspath.bat", this.binDir + "/start-confluence.sh", this.binDir + "/start-confluence.bat", this.binDir + "/stop-confluence.sh", this.binDir + "/stop-confluence.bat", this.binDir + "/startup.sh", this.binDir + "/startup.bat", this.binDir + "/shutdown.sh", this.binDir + "/shutdown.bat"));
        builder.fileSet(ExactFileSet.ofPaths(this.info.getSharedApplicationHome() + "/confluence.cfg.xml"), "shared");
        return OsgiServices.exportOsgiService(builder.build(), ExportOptions.as(SupportZipBundle.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportCacheConfigBundle() {
        return OsgiServices.exportOsgiService(FileSetsBundle.builder(BundleManifest.CACHE_CONFIG, "stp.zip.include.cache.cfg", "stp.zip.include.cache.cfg.description", BundleCategory.CONFIG, this.i18nResolver).fileSet(ExactFileSet.ofPaths(this.info.getApplicationHome() + "/config/cache-settings-overrides.properties", this.info.getApplicationHome() + "/shared-home/config/cache-settings-overrides.properties", this.info.getApplicationHome() + "/config/ehcache.xml", this.info.getApplicationHome() + "/config/ehcache.properties")).build(), ExportOptions.as(SupportZipBundle.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportApplicationLogsBundle() {
        return OsgiServices.exportOsgiService(FileSetsBundle.builder(BundleManifest.APPLICATION_LOGS, ZIP_INCLUDE_CONFLUENCE_LOGS, ZIP_INCLUDE_CONFLUENCE_LOGS_DESCRIPTION, BundleCategory.LOGS, this.i18nResolver).fileSet(RegexFileSet.fromDirectoryPath(this.info.getApplicationLogDir(), Pattern.compile("^.*\\.log.*"))).build(), ExportOptions.as(SupportZipBundle.class, new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportSynchronyConfigBundle(ClusterManager clusterManager) {
        FileSetsBundle.Builder builder = FileSetsBundle.builder(BundleManifest.SYNCHRONY_CONFIG, ZIP_INCLUDE_SYNCHRONY_CFG, ZIP_INCLUDE_SYNCHRONY_CFG_DESCRIPTION, BundleCategory.CONFIG, this.i18nResolver).fileSet(ExactFileSet.ofPaths(this.info.getLocalApplicationHome() + "/synchrony-args.properties"), "local");
        if (clusterManager.isClustered()) {
            builder.fileSet(ExactFileSet.ofPaths(this.info.getSharedApplicationHome() + "/synchrony-args.properties"), "shared");
        }
        return OsgiServices.exportOsgiService(builder.build(), ExportOptions.as(SupportZipBundle.class, new Class[0]));
    }
}


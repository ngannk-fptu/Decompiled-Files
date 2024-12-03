/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.config.db.HibernateConfig
 *  com.atlassian.confluence.api.model.accessmode.AccessMode
 *  com.atlassian.confluence.util.tomcat.TomcatConfigHelper
 *  com.atlassian.dc.filestore.api.FileStore
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 *  com.atlassian.jdk.utilities.runtimeinformation.RuntimeInformation
 *  com.atlassian.modzdetector.Modifications
 *  com.atlassian.modzdetector.ModzRegistryException
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.util.concurrent.LazyReference
 *  com.atlassian.util.concurrent.Supplier
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Preconditions
 *  javax.persistence.PersistenceException
 *  org.apache.commons.lang3.StringUtils
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.cfg.Environment
 *  org.hibernate.engine.jndi.internal.JndiServiceImpl
 *  org.hibernate.engine.spi.SessionImplementor
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.orm.hibernate5.HibernateTemplate
 *  org.springframework.util.unit.DataSize
 *  oshi.SystemInfo
 */
package com.atlassian.confluence.status.service;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.config.ConfigurationException;
import com.atlassian.config.db.HibernateConfig;
import com.atlassian.confluence.api.model.accessmode.AccessMode;
import com.atlassian.confluence.core.ConfluenceSidManager;
import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.core.datetime.DateFormatterFactory;
import com.atlassian.confluence.impl.cluster.ClusterConfigurationHelperInternal;
import com.atlassian.confluence.impl.pages.attachments.objectstorage.S3ConfigFactory;
import com.atlassian.confluence.impl.startup.ConfluenceRuntimeInformationFactory;
import com.atlassian.confluence.internal.accessmode.AccessModeManager;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.status.service.CloudPlatformMetadataService;
import com.atlassian.confluence.status.service.ClusteredDatabasePlatformMetadataService;
import com.atlassian.confluence.status.service.HashRegistryCache;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.confluence.status.service.systeminfo.AttachmentStorageInfo;
import com.atlassian.confluence.status.service.systeminfo.AttachmentStorageType;
import com.atlassian.confluence.status.service.systeminfo.CloudPlatformMetadata;
import com.atlassian.confluence.status.service.systeminfo.CloudPlatformType;
import com.atlassian.confluence.status.service.systeminfo.ClusteredDatabasePlatformMetadata;
import com.atlassian.confluence.status.service.systeminfo.ConfluenceInfo;
import com.atlassian.confluence.status.service.systeminfo.DatabaseInfo;
import com.atlassian.confluence.status.service.systeminfo.HardwareInfo;
import com.atlassian.confluence.status.service.systeminfo.MemoryInfo;
import com.atlassian.confluence.status.service.systeminfo.SecurityInfo;
import com.atlassian.confluence.status.service.systeminfo.SystemInfo;
import com.atlassian.confluence.status.service.systeminfo.SystemInfoFromDb;
import com.atlassian.confluence.status.service.systeminfo.UsageInfo;
import com.atlassian.confluence.tenant.VacantException;
import com.atlassian.confluence.util.ClasspathUtils;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.i18n.DefaultI18NBeanFactory;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.util.tomcat.TomcatConfigHelper;
import com.atlassian.dc.filestore.api.FileStore;
import com.atlassian.extras.api.confluence.ConfluenceLicense;
import com.atlassian.jdk.utilities.runtimeinformation.RuntimeInformation;
import com.atlassian.modzdetector.Modifications;
import com.atlassian.modzdetector.ModzRegistryException;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.util.concurrent.LazyReference;
import com.atlassian.util.concurrent.Supplier;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import java.io.File;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.persistence.PersistenceException;
import javax.sql.DataSource;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Environment;
import org.hibernate.engine.jndi.internal.JndiServiceImpl;
import org.hibernate.engine.spi.SessionImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.util.unit.DataSize;

public class DefaultSystemInformationService
implements SystemInformationService {
    private static final Logger logger = LoggerFactory.getLogger(DefaultSystemInformationService.class);
    private static final String[] ISOLATION_LEVELS = new String[]{"Read committed", "Read uncommitted", "Read committed", null, "Repeatable read", null, null, null, "Serializable"};
    private BootstrapManager bootstrapManager;
    private FileStore sharedHome;
    private FileStore localHome;
    private SessionFactory sessionFactory;
    private PluginAccessor pluginAccessor;
    private GlobalSettingsManager settingsManager;
    private ConfluenceSidManager sidManager;
    private I18NBeanFactory i18NBeanFactory;
    private I18NBean i18NBean;
    private HashRegistryCache registry;
    private BandanaManager bandanaManager;
    private LicenseService licenseService;
    private DateFormatterFactory dateFormatterFactory;
    private HibernateConfig hibernateConfig;
    private AccessModeManager accessModeManager;
    private TomcatConfigHelper tomcatConfigHelper;
    private CloudPlatformMetadataService cloudPlatformMetadataService;
    private ClusteredDatabasePlatformMetadataService clusteredDatabasePlatformMetadataService;
    private ApplicationConfiguration applicationConfiguration;
    private ClusterConfigurationHelperInternal clusterConfigurationHelper;

    @Override
    public ConfluenceInfo getConfluenceInfo() {
        if (!this.isInitializedWithLogging()) {
            return null;
        }
        ConfluenceInfo infoBean = new ConfluenceInfo((Supplier<ResourceBundle>)this.getResourceBundleSupplier());
        infoBean.setHome(this.bootstrapManager.getConfiguredApplicationHome());
        infoBean.setStartTime(GeneralUtil.getSystemStartupTime());
        infoBean.setVersion(GeneralUtil.getVersionNumber());
        infoBean.setBuildNumber(GeneralUtil.getBuildNumber());
        infoBean.setGlobalSettings(this.settingsManager.getGlobalSettings());
        infoBean.setBaseUrl(this.settingsManager.getGlobalSettings().getBaseUrl());
        try {
            infoBean.setServerId(this.sidManager.getSid());
        }
        catch (ConfigurationException e) {
            infoBean.setServerId(e.getMessage());
        }
        infoBean.setEnabledPlugins(this.pluginAccessor.getEnabledPlugins());
        infoBean.setInstallationDate((Date)this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, "confluence.server.installation.date"));
        ConfluenceLicense license = this.licenseService.retrieve();
        infoBean.setSupportEntitlementNumber(license.getSupportEntitlementNumber());
        infoBean.setMaxUsers(license.getMaximumNumberOfUsers());
        return infoBean;
    }

    private LazyReference<ResourceBundle> getResourceBundleSupplier() {
        return new LazyReference<ResourceBundle>(){

            protected ResourceBundle create() throws Exception {
                return DefaultSystemInformationService.this.getI18NBean().getResourceBundle();
            }
        };
    }

    @Override
    public DatabaseInfo getDatabaseInfo() {
        if (!this.isInitializedWithLogging()) {
            return null;
        }
        HibernateTemplate hibernateTemplate = new HibernateTemplate(this.sessionFactory);
        return (DatabaseInfo)hibernateTemplate.executeWithNativeSession(this::getDatabaseInfo);
    }

    private DatabaseInfo getDatabaseInfo(Session session) {
        DatabaseInfo info = new DatabaseInfo();
        Connection connection = ((SessionImplementor)session).connection();
        this.retrieveConnectionBasedInformation(connection, info);
        this.retrieveConfluenceBasedInformation(info);
        this.findDatabaseQueryLatency(connection, info);
        return info;
    }

    @Override
    public DatabaseInfo getSafeDatabaseInfo() {
        if (!this.isInitializedWithLogging()) {
            return null;
        }
        DatabaseInfo info = new DatabaseInfo();
        this.retrieveConfluenceBasedInformation(info);
        return info;
    }

    @Override
    public SystemInfo getSystemProperties() {
        RuntimeInformation runtime = ConfluenceRuntimeInformationFactory.getRuntimeInformation();
        RuntimeInformation runtimeFiltered = ConfluenceRuntimeInformationFactory.getFilteredRuntimeInformation();
        SystemInfo info = new SystemInfo();
        Date now = new Date();
        DateFormatter dateFormatter = this.dateFormatterFactory.createGlobal();
        info.setDate(dateFormatter.formatDateFull(now));
        info.setTime(dateFormatter.formatTimeMedium(now));
        Properties sysProps = System.getProperties();
        info.setJavaVersion(sysProps.getProperty("java.version"));
        info.setJavaVendor(sysProps.getProperty("java.vendor"));
        info.setJavaSpecificationVersion(sysProps.getProperty("java.specification.version"));
        info.setJvmVersion(sysProps.getProperty("java.vm.specification.version"));
        info.setJvmVendor(sysProps.getProperty("java.vm.specification.vendor"));
        info.setJvmImplementationVersion(sysProps.getProperty("java.vm.version"));
        info.setJavaRuntime(sysProps.getProperty("java.runtime.name"));
        info.setJavaVm(sysProps.getProperty("java.vm.name"));
        info.setUserName(sysProps.getProperty("user.name"));
        info.setSystemLanguage(sysProps.getProperty("user.language"));
        info.setSystemTimezone(sysProps.getProperty("user.timezone"));
        info.setOperatingSystemName(sysProps.getProperty("os.name"));
        info.setOperatingSystemVersion(sysProps.getProperty("os.version"));
        info.setOperatingSystem(sysProps.getProperty("os.name") + " " + sysProps.getProperty("os.version"));
        info.setOperatingSystemArchitecture(sysProps.getProperty("os.arch"));
        info.setFileSystemEncoding(sysProps.getProperty("file.encoding"));
        info.setJvmInputArguments(runtime.getJvmInputArguments());
        info.setJvmInputArgumentsFiltered(runtimeFiltered.getJvmInputArguments());
        info.setWorkingDirectory(sysProps.getProperty("user.dir"));
        info.setTempDirectory(sysProps.getProperty("java.io.tmpdir"));
        return info;
    }

    @Override
    public AttachmentStorageInfo getAttachmentStorageProperties() {
        AttachmentStorageType attachmentStorageType = AttachmentStorageType.FILESYSTEM;
        if (S3ConfigFactory.getInstance(this.applicationConfiguration, this.clusterConfigurationHelper, this.licenseService).isPresent()) {
            attachmentStorageType = AttachmentStorageType.S3;
        }
        AttachmentStorageInfo info = new AttachmentStorageInfo(attachmentStorageType);
        return info;
    }

    @Override
    public MemoryInfo getMemoryInfo() {
        return new MemoryInfo();
    }

    @Override
    public UsageInfo getUsageInfo() {
        if (!this.isInitializedWithLogging()) {
            return null;
        }
        HibernateTemplate hibernateTemplate = new HibernateTemplate(this.sessionFactory);
        return (UsageInfo)hibernateTemplate.executeWithNativeSession(this::getUsageInfo);
    }

    private UsageInfo getUsageInfo(Session session) {
        try {
            return UsageInfo.getUsageInfo(((SessionImplementor)session).connection());
        }
        catch (PersistenceException e) {
            logger.error("Unable to retrieve usage information: " + e.getMessage(), (Throwable)e);
            return UsageInfo.errorInstance();
        }
    }

    @Override
    public SystemInfoFromDb getSystemInfoFromDb() {
        if (!this.isInitializedWithLogging()) {
            return null;
        }
        HibernateTemplate hibernateTemplate = new HibernateTemplate(this.sessionFactory);
        return (SystemInfoFromDb)hibernateTemplate.executeWithNativeSession(session -> new SystemInfoFromDb(this.getConfluenceInfo(), this.getDatabaseInfo(session), this.getUsageInfo(session)));
    }

    public void setBootstrapManager(BootstrapManager bootstrapManager) {
        this.bootstrapManager = bootstrapManager;
    }

    public void setSharedHome(FileStore sharedHome) {
        this.sharedHome = sharedHome;
    }

    public void setLocalHome(FileStore localHome) {
        this.localHome = localHome;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setPluginAccessor(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    @Deprecated
    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    public void setGlobalSettingsManager(GlobalSettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    public void setSidManager(ConfluenceSidManager sidManager) {
        this.sidManager = sidManager;
    }

    public void setRegistry(HashRegistryCache registry) {
        this.registry = registry;
    }

    public void setHibernateConfig(HibernateConfig hibernateConfig) {
        this.hibernateConfig = hibernateConfig;
    }

    private boolean isInitializedWithLogging() {
        if (this.pluginAccessor == null || this.bootstrapManager == null || this.sessionFactory == null) {
            logger.warn("The DefaultSystemInformationService has not been completely initialized so will not provide information about the system.");
            return false;
        }
        return true;
    }

    @Override
    public Map<String, String> getModifications() {
        String modifiedFilesDescription = "";
        String removedFilesDescription = "";
        try {
            Modifications modifications = this.registry.getModifications();
            modifiedFilesDescription = !modifications.modifiedFiles.isEmpty() ? StringUtils.join((Iterable)modifications.modifiedFiles, (String)", ") : "No files modified";
            removedFilesDescription = !modifications.removedFiles.isEmpty() ? StringUtils.join((Iterable)modifications.removedFiles, (String)", ") : "No files removed";
        }
        catch (ModzRegistryException | RuntimeException e) {
            logger.error(e.getMessage());
        }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("modified", modifiedFilesDescription);
        map.put("removed", removedFilesDescription);
        return map;
    }

    @Override
    public boolean isShowInfoOn500() {
        try {
            return this.settingsManager.getGlobalSettings().isShowSystemInfoIn500Page();
        }
        catch (VacantException ex) {
            logger.warn("Cannot determine isShowSystemInfoIn500Page setting when Confluence is vacant");
            return false;
        }
    }

    @Override
    public AccessMode getAccessMode() {
        return this.accessModeManager.getAccessMode();
    }

    @Override
    public Integer getMaxHTTPThreads() {
        return this.tomcatConfigHelper.getAllMaxHttpThreads().stream().map(threads -> threads.orElse(-1)).max(Integer::compareTo).orElse(-1);
    }

    @Override
    public Properties getHibernateProperties() {
        return this.hibernateConfig.getHibernateProperties();
    }

    @Override
    public HardwareInfo getHardwareInfo() {
        return new HardwareInfo(DefaultSystemInformationService.getAvailableProcessors(), DefaultSystemInformationService.getDiskSize(this.localHome).toMegabytes(), DefaultSystemInformationService.getDiskSize(this.sharedHome).toMegabytes(), DefaultSystemInformationService.getTotalMemory().toMegabytes());
    }

    private static int getAvailableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

    private static DataSize getTotalMemory() {
        return DataSize.ofBytes((long)new oshi.SystemInfo().getHardware().getMemory().getTotal());
    }

    private static DataSize getDiskSize(FileStore fileStore) {
        return fileStore.getTotalSpace().map(size -> DataSize.ofBytes((long)size.getBytes())).orElse(DataSize.ofBytes((long)0L));
    }

    @Override
    public Optional<CloudPlatformMetadata> getCloudPlatformMetadata() {
        for (CloudPlatformType platformType : CloudPlatformType.values()) {
            Optional<CloudPlatformMetadata> platformMetadata = this.cloudPlatformMetadataService.getCloudPlatformMetadata(platformType);
            if (!platformMetadata.isPresent()) continue;
            return platformMetadata;
        }
        return Optional.empty();
    }

    private void retrieveConnectionBasedInformation(Connection connection, DatabaseInfo info) {
        try {
            DatabaseMetaData data = connection.getMetaData();
            info.setDriverVersion(data.getDriverVersion());
            info.setVersion(data.getDatabaseProductVersion());
            info.setName(data.getDatabaseProductName());
            info.setUrl(data.getURL());
            info.setCatalogName((String)MoreObjects.firstNonNull((Object)connection.getCatalog(), (Object)data.getUserName()));
            int isolationLevel = connection.getTransactionIsolation();
            if (isolationLevel < 0 || isolationLevel >= ISOLATION_LEVELS.length) {
                info.setIsolationLevel(null);
            } else {
                info.setIsolationLevel(ISOLATION_LEVELS[isolationLevel]);
            }
            if (info.getIsolationLevel() == null) {
                info.setIsolationLevel(Integer.toString(isolationLevel));
            }
        }
        catch (Exception ex) {
            logger.warn("Exception while retrieving database connection information.", (Throwable)ex);
        }
    }

    @Override
    public Optional<ClusteredDatabasePlatformMetadata> getClusteredDatabaseInformation(CloudPlatformType cloudPlatformType) {
        return (Optional)new HibernateTemplate(this.sessionFactory).executeWithNativeSession(session -> {
            Connection connection = ((SessionImplementor)session).connection();
            return this.clusteredDatabasePlatformMetadataService.getClusteredDatabaseMetadataForPlatform(connection, cloudPlatformType);
        });
    }

    @Override
    public SecurityInfo getSecurityInfo() {
        SecurityInfo securityInfo = new SecurityInfo();
        Object cipherType = this.applicationConfiguration.getProperty((Object)"jdbc.password.decrypter.classname");
        if (cipherType != null) {
            securityInfo.setSecretStoreClass(cipherType.toString());
        }
        return securityInfo;
    }

    public I18NBean getI18NBean() {
        if (this.i18NBean == null) {
            return this.getI18NBeanFactory().getI18NBean();
        }
        return this.i18NBean;
    }

    public void setI18NBean(I18NBean i18NBean) {
        this.i18NBean = i18NBean;
    }

    private I18NBeanFactory getI18NBeanFactory() {
        if (this.i18NBeanFactory == null) {
            this.i18NBeanFactory = new DefaultI18NBeanFactory();
        }
        return this.i18NBeanFactory;
    }

    public void setI18NBeanFactory(I18NBeanFactory i18NBeanFactory) {
        this.i18NBeanFactory = i18NBeanFactory;
    }

    private void retrieveConfluenceBasedInformation(DatabaseInfo info) {
        if (info.getUrl() == null) {
            info.setUrl(this.bootstrapManager.getString("hibernate.connection.url"));
        }
        if (info.getUrl() == null) {
            info.setUrl(this.bootstrapManager.getString("hibernate.connection.datasource"));
        }
        info.setDialect(this.bootstrapManager.getString("hibernate.dialect"));
        if (this.bootstrapManager.getString("hibernate.connection.driver_class") != null) {
            info.setDriverName(this.bootstrapManager.getString("hibernate.connection.driver_class"));
        } else if (System.getProperty("confluence.status.service.jdbc.driver.name") != null) {
            info.setDriverName(System.getProperty("confluence.status.service.jdbc.driver.name"));
        } else {
            Properties properties = new Properties();
            properties.putAll((Map<?, ?>)Environment.getProperties());
            properties.putAll((Map<?, ?>)this.getHibernateProperties());
            if (properties.getProperty("hibernate.connection.datasource") != null && this.bootstrapManager.getString("hibernate.connection.datasource") != null) {
                try {
                    Method driverClassNameMethod;
                    String hibernateDatasource = this.bootstrapManager.getString("hibernate.connection.datasource");
                    JndiServiceImpl jndiService = new JndiServiceImpl((Map)properties);
                    DataSource ds = (DataSource)jndiService.locate(hibernateDatasource);
                    if (ds == null) {
                        logger.error("Cannot retrieve the datasource from {}: {}", (Object)"hibernate.connection.datasource", (Object)hibernateDatasource);
                        return;
                    }
                    String driverClassName = ds.getClass().getName();
                    logger.info("The driver class name retrieved from the datasource is: {}", (Object)driverClassName);
                    if (StringUtils.startsWithAny((CharSequence)driverClassName, (CharSequence[])new CharSequence[]{"org.apache.commons.", "org.apache.tomcat."}) && StringUtils.endsWithAny((CharSequence)driverClassName, (CharSequence[])new CharSequence[]{".dbcp.BasicDataSource", ".dbcp2.BasicDataSource"}) && (driverClassNameMethod = ds.getClass().getDeclaredMethod("getDriverClassName", new Class[0])) != null) {
                        info.setDriverName((String)driverClassNameMethod.invoke((Object)ds, new Object[0]));
                    }
                }
                catch (Exception e) {
                    logger.error("An error has occurred while retrieving the JDBC driver from the datasource: {}", (Throwable)e);
                }
            }
        }
        if (info.getDriverName() != null) {
            try {
                Optional<File> jarFile = ClasspathUtils.getJarFileFromClass(Class.forName(info.getDriverName()));
                jarFile.ifPresent(info::setDriverFile);
            }
            catch (ClassNotFoundException e) {
                logger.error("The class {} is not found", (Object)info.getDriverName());
                logger.debug("{}", (Throwable)e);
            }
        }
    }

    private void findDatabaseQueryLatency(Connection connection, DatabaseInfo info) {
        try (Statement statement = connection.createStatement();){
            long startTime = System.currentTimeMillis();
            ResultSet resultSet = statement.executeQuery("select * from CLUSTERSAFETY");
            resultSet.close();
            info.setExampleLatency(System.currentTimeMillis() - startTime);
        }
        catch (Exception ex) {
            logger.warn("Exception while testing database query latency.", (Throwable)ex);
        }
    }

    public void setBandanaManager(BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }

    public void setLicenseService(LicenseService licenseService) {
        this.licenseService = (LicenseService)Preconditions.checkNotNull((Object)licenseService);
    }

    public void setDateFormatterFactory(DateFormatterFactory dateFormatterFactory) {
        this.dateFormatterFactory = dateFormatterFactory;
    }

    public void setAccessModeManager(AccessModeManager accessModeManager) {
        this.accessModeManager = accessModeManager;
    }

    public void setTomcatConfigHelper(TomcatConfigHelper tomcatConfigHelper) {
        this.tomcatConfigHelper = tomcatConfigHelper;
    }

    public void setCloudPlatformMetadataService(CloudPlatformMetadataService cloudPlatformMetadataService) {
        this.cloudPlatformMetadataService = cloudPlatformMetadataService;
    }

    public void setClusteredDatabasePlatformMetadataService(ClusteredDatabasePlatformMetadataService clusteredDatabasePlatformMetadataService) {
        this.clusteredDatabasePlatformMetadataService = clusteredDatabasePlatformMetadataService;
    }

    public void setApplicationConfiguration(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    public void setClusterConfigurationHelper(ClusterConfigurationHelperInternal clusterConfigurationHelper) {
        this.clusterConfigurationHelper = clusterConfigurationHelper;
    }
}


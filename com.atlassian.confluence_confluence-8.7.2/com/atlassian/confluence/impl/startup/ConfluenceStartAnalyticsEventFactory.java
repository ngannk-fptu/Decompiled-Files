/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 *  com.atlassian.plugin.parsers.SafeModeCommandLineArguments
 *  com.atlassian.plugin.parsers.SafeModeCommandLineArgumentsFactory
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.hash.Hashing
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.impl.startup;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ClusterNodeInformation;
import com.atlassian.confluence.impl.startup.ConfluenceStartAnalyticsEvent;
import com.atlassian.confluence.impl.util.sandbox.SandboxPoolConfiguration;
import com.atlassian.confluence.impl.util.sandbox.misc.PluginSandboxCheck;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.setup.BuildInformation;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.confluence.status.service.systeminfo.AttachmentStorageInfo;
import com.atlassian.confluence.status.service.systeminfo.ClusteredDatabasePlatformMetadata;
import com.atlassian.confluence.status.service.systeminfo.DatabaseInfo;
import com.atlassian.confluence.status.service.systeminfo.HardwareInfo;
import com.atlassian.confluence.status.service.systeminfo.MemoryInfo;
import com.atlassian.confluence.status.service.systeminfo.SecurityInfo;
import com.atlassian.confluence.status.service.systeminfo.SystemInfo;
import com.atlassian.extras.api.confluence.ConfluenceLicense;
import com.atlassian.plugin.parsers.SafeModeCommandLineArguments;
import com.atlassian.plugin.parsers.SafeModeCommandLineArgumentsFactory;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.TimeZone;
import java.util.stream.Collectors;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

public class ConfluenceStartAnalyticsEventFactory {
    public static final String SERVER_PLATFORM = "server.platform";
    private final TransactionTemplate txTemplate;
    private final ClusterManager clusterManager;
    private final LicenseService licenseService;
    private final SafeModeCommandLineArguments safeModeCommandLineArguments;
    private final SystemInformationService systemInformationService;
    private final SandboxPoolConfiguration conversionSandboxConfig;

    public ConfluenceStartAnalyticsEventFactory(PlatformTransactionManager txManager, ClusterManager clusterManager, LicenseService licenseService, SafeModeCommandLineArgumentsFactory safeModeCommandLineArgumentsFactory, SystemInformationService systemInformationService, SandboxPoolConfiguration conversionSandboxConfig) {
        this.txTemplate = new TransactionTemplate(Objects.requireNonNull(txManager));
        this.txTemplate.setReadOnly(true);
        this.clusterManager = Objects.requireNonNull(clusterManager);
        this.licenseService = Objects.requireNonNull(licenseService);
        this.safeModeCommandLineArguments = Objects.requireNonNull(safeModeCommandLineArgumentsFactory).get();
        this.systemInformationService = Objects.requireNonNull(systemInformationService);
        this.conversionSandboxConfig = Objects.requireNonNull(conversionSandboxConfig);
    }

    ConfluenceStartAnalyticsEvent createConfluenceStartEvent() {
        return (ConfluenceStartAnalyticsEvent)this.txTemplate.execute(tx -> {
            try {
                ImmutableMap.Builder props = ImmutableMap.builder();
                props.putAll(this.populateBuildProperties());
                props.putAll(this.populateSystemProperties());
                props.putAll(this.populateDatabaseProperties());
                props.putAll(this.populateHibernateProperties());
                props.putAll(this.populateClusterProperties());
                props.putAll(this.populateSafeModeProperties());
                props.putAll(this.populateHardwareProperties());
                props.putAll(this.populatePlatformProperties());
                props.putAll(this.populateSandboxProperties());
                props.putAll(this.populateAttachmentStorageProperties());
                props.putAll(this.populateSecurityProperties());
                return new ConfluenceStartAnalyticsEvent((Map<String, Object>)props.build());
            }
            catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private Map<String, Object> populateSandboxProperties() {
        return ImmutableMap.builder().putAll(this.populatePluginSandboxUsage()).putAll(this.populateConversionSandboxProperties()).build();
    }

    private Map<String, Object> populatePluginSandboxUsage() {
        return ImmutableMap.builder().put((Object)"document.conversion.sandbox.disable", (Object)PluginSandboxCheck.documentConversionSandboxExplicitlyDisabled()).put((Object)"pdf.export.sandbox.disable", (Object)PluginSandboxCheck.pdfExportSandboxExplicitlyDisabled()).build();
    }

    private Map<String, Object> populateConversionSandboxProperties() {
        return ImmutableMap.builder().put((Object)"conversion.sandbox.pool.size", (Object)this.conversionSandboxConfig.getConcurrencyLevel()).put((Object)"conversion.sandbox.memory.limit.megabytes", (Object)this.conversionSandboxConfig.getMemoryInMegabytes()).put((Object)"conversion.sandbox.stack.limit.megabytes", (Object)this.conversionSandboxConfig.getStackInMegabytes()).build();
    }

    protected Map<String, Object> populateSafeModeProperties() {
        List keys = this.safeModeCommandLineArguments.getDisabledPlugins().orElse(Collections.emptyList()).stream().map(ConfluenceStartAnalyticsEventFactory::hash).collect(Collectors.toList());
        return ImmutableMap.of((Object)"plugins.addonsdisabledonstartup", (Object)String.join((CharSequence)":", keys), (Object)"plugins.nonsystemaddonsdisabledonstartup", (Object)this.safeModeCommandLineArguments.isSafeMode(), (Object)"plugins.specificaddonsweredisabledonstartup", (Object)this.safeModeCommandLineArguments.getDisabledPlugins().isPresent());
    }

    protected Map<String, Object> populateBuildProperties() {
        BuildInformation buildInfo = BuildInformation.INSTANCE;
        return ImmutableMap.of((Object)"build.date", (Object)SimpleDateFormat.getDateTimeInstance(2, 0, Locale.US).format(buildInfo.getBuildTimestamp()), (Object)"build.version", (Object)buildInfo.getVersionNumber(), (Object)"build.number", (Object)buildInfo.getBuildNumber());
    }

    protected Map<String, Object> populateHardwareProperties() {
        ImmutableMap.Builder props = ImmutableMap.builder();
        HardwareInfo hardwareInfo = this.systemInformationService.getHardwareInfo();
        MemoryInfo memoryInfo = this.systemInformationService.getMemoryInfo();
        try {
            props.put((Object)"jvm.max.heap", (Object)memoryInfo.getMaxHeap().megabytes());
            props.put((Object)"jvm.available.processors", (Object)hardwareInfo.getAvailableProcessors());
            props.put((Object)"disk.local.home.size", (Object)hardwareInfo.getLocalHomeTotalDiskSize());
            props.put((Object)"disk.shared.home.size", (Object)hardwareInfo.getSharedHomeTotalDiskSize());
            props.put((Object)"system.total.memory", (Object)hardwareInfo.getTotalSystemMemory());
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        return props.build();
    }

    protected Map<String, Object> populateSystemProperties() {
        ImmutableMap.Builder props = ImmutableMap.builder();
        SystemInfo sysInfo = this.systemInformationService.getSystemProperties();
        props.put((Object)"os.arch", (Object)sysInfo.getOperatingSystemArchitecture());
        props.put((Object)"os.name", (Object)sysInfo.getOperatingSystemName());
        props.put((Object)"os.version", (Object)sysInfo.getOperatingSystemVersion());
        props.put((Object)"java.version", (Object)sysInfo.getJavaVersion());
        props.put((Object)"java.vendor", (Object)sysInfo.getJavaVendor());
        props.put((Object)"java.vm.name", (Object)sysInfo.getJavaVm());
        props.put((Object)"java.specification.version", (Object)sysInfo.getJavaSpecificationVersion());
        props.put((Object)"java.opts", (Object)sysInfo.getJvmInputArgumentsFiltered());
        props.put((Object)"tomcat.maxHTTPThreads", (Object)this.systemInformationService.getMaxHTTPThreads());
        int utcOffset = TimeZone.getTimeZone(sysInfo.getSystemTimezone()).getOffset(new Date().getTime());
        ZoneOffset utcOffsetFormatted = ZoneOffset.ofTotalSeconds(utcOffset / 1000);
        props.put((Object)"server.timezone", (Object)utcOffsetFormatted.toString());
        return props.build();
    }

    protected Map<String, Object> populateDatabaseProperties() throws SQLException {
        ImmutableMap.Builder props = ImmutableMap.builder();
        DatabaseInfo dbInfo = this.systemInformationService.getDatabaseInfo();
        props.put((Object)"database.version", (Object)dbInfo.getVersion());
        props.put((Object)"database.driver.version", (Object)dbInfo.getDriverVersion());
        props.put((Object)"database.system", (Object)dbInfo.getName());
        return props.build();
    }

    protected Map<String, Object> populateAttachmentStorageProperties() {
        AttachmentStorageInfo attachmentStorageInfo = this.systemInformationService.getAttachmentStorageProperties();
        ImmutableMap.Builder props = ImmutableMap.builder();
        props.put((Object)"attachment.storage.type", (Object)attachmentStorageInfo.getStorageType());
        return props.build();
    }

    protected Map<String, Object> populateHibernateProperties() {
        ImmutableMap.Builder hibernateProperties = ImmutableMap.builder();
        Properties hibernateConfig = this.systemInformationService.getHibernateProperties();
        ImmutableList propertiesList = ImmutableList.of((Object)"hibernate.c3p0.acquire_increment", (Object)"hibernate.c3p0.idle_test_period", (Object)"hibernate.c3p0.max_size", (Object)"hibernate.c3p0.max_statements", (Object)"hibernate.c3p0.min_size", (Object)"hibernate.c3p0.timeout", (Object)"hibernate.dialect");
        propertiesList.stream().forEach(property -> hibernateProperties.put(property, (Object)hibernateConfig.getProperty((String)property, "")));
        hibernateProperties.put((Object)"hibernate.connection.datasource", (Object)(hibernateConfig.getProperty("hibernate.connection.datasource") != null ? 1 : 0));
        return hibernateProperties.build();
    }

    protected Map<String, Object> populateClusterProperties() throws SQLException {
        ImmutableMap.Builder props = ImmutableMap.builder();
        ConfluenceLicense license = this.licenseService.retrieve();
        boolean clusteringEnabled = license.isClusteringEnabled();
        props.put((Object)"clustering.enabled", (Object)clusteringEnabled);
        if (clusteringEnabled) {
            int numberOfNodes = this.clusterManager.getAllNodesInformation().size();
            String restartType = numberOfNodes == 1 ? "full" : "node";
            ClusterNodeInformation thisNodeInformation = this.clusterManager.getThisNodeInformation();
            String nodeId = thisNodeInformation != null ? thisNodeInformation.getAnonymizedNodeIdentifier() : "sndc";
            props.put((Object)"clustering.restartType", (Object)restartType);
            props.put((Object)"clustering.nodeId", (Object)nodeId);
        }
        return props.build();
    }

    protected Map<String, Object> populatePlatformProperties() {
        ImmutableMap.Builder platformProperties = ImmutableMap.builder();
        this.systemInformationService.getCloudPlatformMetadata().ifPresent(platformMetadata -> {
            platformProperties.put((Object)SERVER_PLATFORM, (Object)platformMetadata.getCloudPlatform());
            platformProperties.put((Object)"server.platform.instanceType", (Object)platformMetadata.getInstanceType());
            Optional<ClusteredDatabasePlatformMetadata> metadataHolder = this.systemInformationService.getClusteredDatabaseInformation(platformMetadata.getCloudPlatform());
            metadataHolder.ifPresent(metadata -> {
                platformProperties.put((Object)"database.cluster.version", (Object)metadata.getDatabaseVersion());
                platformProperties.put((Object)"database.cluster.engine", (Object)metadata.getDatabaseType());
            });
        });
        return platformProperties.build();
    }

    protected Map<String, Object> populateSecurityProperties() {
        ImmutableMap.Builder securityProperties = ImmutableMap.builder();
        SecurityInfo securityInfo = this.systemInformationService.getSecurityInfo();
        securityProperties.put((Object)"security.secretStore.jdbc.type", (Object)securityInfo.getSecretStoreType());
        return securityProperties.build();
    }

    private static String hash(String value) {
        return Long.toString(Hashing.sha256().hashString((CharSequence)value, StandardCharsets.UTF_8).asLong());
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.dc.filestore.impl.s3.S3Config
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.pages.attachments.objectstorage;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.config.ConfigurationException;
import com.atlassian.confluence.impl.cluster.ClusterConfigurationHelperInternal;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.dc.filestore.impl.s3.S3Config;
import java.net.URI;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class S3ConfigFactory {
    private static final Logger log = LoggerFactory.getLogger(S3ConfigFactory.class);
    public static final String S3_BUCKET_NAME_PROPERTY_NAME = "confluence.filestore.attachments.s3.bucket.name";
    public static final String S3_BUCKET_REGION_PROPERTY_NAME = "confluence.filestore.attachments.s3.bucket.region";
    public static final String S3_ENDPOINT_OVERRIDE = "confluence.filestore.attachments.s3.endpoint.override";
    public static final String S3_MAX_CONNECTIONS = "confluence.filestore.attachments.s3.max.connections";
    public static final String S3_CONNECTION_ACQUISITION_TIMEOUT_MILLIS = "confluence.filestore.attachments.s3.connection.acquisition.timeout.millis";

    private S3ConfigFactory() {
    }

    public static Optional<S3Config> getInstance(ApplicationConfiguration appConfig, ClusterConfigurationHelperInternal clusterConfigurationHelper, LicenseService licenseService) {
        String bucketName = S3ConfigFactory.getConfigProperty(appConfig, clusterConfigurationHelper, S3_BUCKET_NAME_PROPERTY_NAME);
        String bucketRegion = S3ConfigFactory.getConfigProperty(appConfig, clusterConfigurationHelper, S3_BUCKET_REGION_PROPERTY_NAME);
        String endpointOverride = S3ConfigFactory.getConfigProperty(appConfig, clusterConfigurationHelper, S3_ENDPOINT_OVERRIDE);
        String maxConnections = S3ConfigFactory.getConfigProperty(appConfig, clusterConfigurationHelper, S3_MAX_CONNECTIONS);
        String acquisitionTimeout = S3ConfigFactory.getConfigProperty(appConfig, clusterConfigurationHelper, S3_CONNECTION_ACQUISITION_TIMEOUT_MILLIS);
        if (StringUtils.isNotBlank((CharSequence)bucketName) && StringUtils.isNotBlank((CharSequence)bucketRegion) && licenseService.isLicensedForDataCenter()) {
            S3Config s3Config = S3Config.builder((String)bucketRegion, (String)bucketName).setEndpointOverride(StringUtils.isNotBlank((CharSequence)endpointOverride) ? URI.create(endpointOverride) : null).setMaxConnectionsHttpClient(S3ConfigFactory.isValidMaxConnections(maxConnections)).setConnectionAcquisitionTimeoutHttpClient(S3ConfigFactory.isValidAcquisitionTimeout(acquisitionTimeout)).build();
            log.info(s3Config.toString());
            S3ConfigFactory.writePropertiesToConfigs(appConfig, clusterConfigurationHelper, s3Config);
            return Optional.of(s3Config);
        }
        return Optional.empty();
    }

    @Nullable
    public static String getConfigProperty(ApplicationConfiguration appConfig, ClusterConfigurationHelperInternal clusterConfigurationHelper, String propertyName) {
        String value = System.getProperty(propertyName);
        if (StringUtils.isBlank((CharSequence)value) && clusterConfigurationHelper.isClusterHomeConfigured()) {
            value = clusterConfigurationHelper.getSharedProperty(propertyName).orElse(null);
        }
        if (StringUtils.isBlank((CharSequence)value)) {
            value = (String)appConfig.getProperty((Object)propertyName);
        }
        return value;
    }

    private static void writePropertiesToConfigs(ApplicationConfiguration appConfig, ClusterConfigurationHelperInternal clusterConfigurationHelper, S3Config s3Config) {
        if (clusterConfigurationHelper.isClusterHomeConfigured()) {
            S3ConfigFactory.writePropertiesToSharedHome(clusterConfigurationHelper, s3Config);
        }
        S3ConfigFactory.writePropertiesToLocalHome(appConfig, s3Config);
    }

    private static void writePropertiesToLocalHome(ApplicationConfiguration appConfig, S3Config s3Config) {
        appConfig.setProperty((Object)S3_BUCKET_NAME_PROPERTY_NAME, (Object)s3Config.getBucketName());
        appConfig.setProperty((Object)S3_BUCKET_REGION_PROPERTY_NAME, (Object)s3Config.getRegion());
        if (Objects.nonNull(s3Config.getEndpointOverride()) && StringUtils.isNotBlank((CharSequence)s3Config.getEndpointOverride().toString())) {
            appConfig.setProperty((Object)S3_ENDPOINT_OVERRIDE, (Object)s3Config.getEndpointOverride().toString());
        }
        if (Objects.nonNull(s3Config.getMaxConnections())) {
            appConfig.setProperty((Object)S3_MAX_CONNECTIONS, (Object)s3Config.getMaxConnections().toString());
        }
        if (Objects.nonNull(s3Config.getConnectionAcquisitionTimeout())) {
            appConfig.setProperty((Object)S3_CONNECTION_ACQUISITION_TIMEOUT_MILLIS, (Object)Long.toString(s3Config.getConnectionAcquisitionTimeout().toMillis()));
        }
        try {
            appConfig.save();
        }
        catch (ConfigurationException e) {
            log.error("Failed to save S3 properties to confluence.cfg.xml in local home", (Throwable)e);
        }
    }

    private static void writePropertiesToSharedHome(ClusterConfigurationHelperInternal clusterConfigurationHelper, S3Config s3Config) {
        clusterConfigurationHelper.saveSharedProperty(S3_BUCKET_NAME_PROPERTY_NAME, s3Config.getBucketName());
        clusterConfigurationHelper.saveSharedProperty(S3_BUCKET_REGION_PROPERTY_NAME, s3Config.getRegion());
        if (Objects.nonNull(s3Config.getEndpointOverride()) && StringUtils.isNotBlank((CharSequence)s3Config.getEndpointOverride().toString())) {
            clusterConfigurationHelper.saveSharedProperty(S3_ENDPOINT_OVERRIDE, s3Config.getEndpointOverride().toString());
        }
        if (Objects.nonNull(s3Config.getMaxConnections())) {
            clusterConfigurationHelper.saveSharedProperty(S3_MAX_CONNECTIONS, s3Config.getMaxConnections().toString());
        }
        if (Objects.nonNull(s3Config.getConnectionAcquisitionTimeout())) {
            clusterConfigurationHelper.saveSharedProperty(S3_CONNECTION_ACQUISITION_TIMEOUT_MILLIS, Long.toString(s3Config.getConnectionAcquisitionTimeout().toMillis()));
        }
    }

    private static Integer isValidMaxConnections(String configVal) {
        if (StringUtils.isEmpty((CharSequence)configVal)) {
            return null;
        }
        try {
            return Integer.parseInt(configVal);
        }
        catch (NumberFormatException ex) {
            log.error("S3 HTTP config value for max connections not valid [{}]. Integer val required.", (Object)configVal);
            return null;
        }
    }

    private static Duration isValidAcquisitionTimeout(String configVal) {
        if (StringUtils.isEmpty((CharSequence)configVal)) {
            return null;
        }
        try {
            return Duration.ofMillis(Integer.parseInt(configVal));
        }
        catch (NumberFormatException ex) {
            log.error("S3 HTTP config value for connection acquisition timeout not valid [{}]. Integer val required.", (Object)configVal);
            return null;
        }
    }
}


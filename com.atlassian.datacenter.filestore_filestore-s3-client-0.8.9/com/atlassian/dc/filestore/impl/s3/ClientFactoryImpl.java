/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  software.amazon.awssdk.awscore.defaultsmode.DefaultsMode
 *  software.amazon.awssdk.http.SdkHttpClient$Builder
 *  software.amazon.awssdk.http.apache.ApacheHttpClient
 *  software.amazon.awssdk.http.apache.ApacheHttpClient$Builder
 *  software.amazon.awssdk.regions.Region
 *  software.amazon.awssdk.services.s3.S3Client
 *  software.amazon.awssdk.services.s3.S3ClientBuilder
 */
package com.atlassian.dc.filestore.impl.s3;

import com.atlassian.dc.filestore.impl.s3.ClientFactory;
import com.atlassian.dc.filestore.impl.s3.S3Config;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.awscore.defaultsmode.DefaultsMode;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

public final class ClientFactoryImpl
implements ClientFactory {
    private static final int DEFAULT_CONNECTION_POOL_SIZE = 500;
    private static final Logger log = LoggerFactory.getLogger(ClientFactoryImpl.class);
    private final S3Config s3Config;

    public ClientFactoryImpl(S3Config s3Config) {
        this.s3Config = Objects.requireNonNull(s3Config);
    }

    @Override
    public S3Client getClient() {
        log.info("Building S3Client");
        ApacheHttpClient.Builder httpClientBuilder = ApacheHttpClient.builder();
        if (this.s3Config.getMaxConnections() != null) {
            httpClientBuilder.maxConnections(this.s3Config.getMaxConnections());
        } else {
            httpClientBuilder.maxConnections(Integer.valueOf(500));
        }
        if (this.s3Config.getConnectionAcquisitionTimeout() != null) {
            httpClientBuilder.connectionAcquisitionTimeout(this.s3Config.getConnectionAcquisitionTimeout());
        }
        S3ClientBuilder s3ClientBuilder = (S3ClientBuilder)((S3ClientBuilder)((S3ClientBuilder)((S3ClientBuilder)S3Client.builder().httpClientBuilder((SdkHttpClient.Builder)httpClientBuilder)).region(Region.of((String)this.s3Config.getRegion()))).defaultsMode(DefaultsMode.AUTO)).credentialsProvider(this.s3Config.getCredentialsProviderFactory().build());
        if (this.s3Config.getEndpointOverride() != null) {
            s3ClientBuilder.forcePathStyle(Boolean.valueOf(true));
            s3ClientBuilder.endpointOverride(this.s3Config.getEndpointOverride());
        }
        return (S3Client)s3ClientBuilder.build();
    }
}


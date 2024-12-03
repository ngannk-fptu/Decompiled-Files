/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
 *  software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
 */
package com.atlassian.dc.filestore.impl.s3;

import com.atlassian.dc.filestore.impl.s3.ClientFactoryImpl;
import com.atlassian.dc.filestore.impl.s3.OperationExecutor;
import com.atlassian.dc.filestore.impl.s3.OperationExecutorImpl;
import java.net.URI;
import java.time.Duration;
import javax.annotation.Nullable;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;

public class S3Config {
    private final String region;
    private final String bucketName;
    private final URI endpointOverride;
    private final Integer maxConnections;
    private final Duration connectionAcquisitionTimeout;
    private final CredentialsProviderFactory credentialsProviderFactory;
    private final OperationExecutor operationExecutor;

    private S3Config(Builder builder) {
        this.region = builder.region;
        this.bucketName = builder.bucketName;
        this.endpointOverride = builder.endpointOverride;
        this.maxConnections = builder.maxConnections;
        this.connectionAcquisitionTimeout = builder.connectionAcquisitionTimeout;
        this.credentialsProviderFactory = builder.credentialsProviderFactory == null ? () -> DefaultCredentialsProvider.builder().build() : builder.credentialsProviderFactory;
        this.operationExecutor = builder.operationExecutor == null ? new OperationExecutorImpl(new ClientFactoryImpl(this)) : builder.operationExecutor;
    }

    public String getRegion() {
        return this.region;
    }

    public String getBucketName() {
        return this.bucketName;
    }

    @Nullable
    public URI getEndpointOverride() {
        return this.endpointOverride;
    }

    public CredentialsProviderFactory getCredentialsProviderFactory() {
        return this.credentialsProviderFactory;
    }

    @Nullable
    public Integer getMaxConnections() {
        return this.maxConnections;
    }

    @Nullable
    public Duration getConnectionAcquisitionTimeout() {
        return this.connectionAcquisitionTimeout;
    }

    public OperationExecutor getOperationExecutor() {
        return this.operationExecutor;
    }

    public static Builder builder(String region, String bucketName) {
        return new Builder(region, bucketName);
    }

    public String toString() {
        return String.format("S3 Configuration - Bucket name: [%s] Region: [%s] Endpoint override: [%s] S3 HTTP client max conns [%d] S3 HTTP client acquisition timeout [%d]", this.getBucketName(), this.getRegion(), this.getEndpointOverride(), this.getMaxConnections(), this.connectionAcquisitionTimeout != null ? Long.valueOf(this.connectionAcquisitionTimeout.toMillis()) : null);
    }

    public static class Builder {
        private final String region;
        private final String bucketName;
        private URI endpointOverride;
        private Integer maxConnections;
        private Duration connectionAcquisitionTimeout;
        private CredentialsProviderFactory credentialsProviderFactory;
        private OperationExecutor operationExecutor;

        public S3Config build() {
            return new S3Config(this);
        }

        private Builder(String region, String bucketName) {
            this.region = region;
            this.bucketName = bucketName;
        }

        public Builder setEndpointOverride(@Nullable URI endpointOverride) {
            this.endpointOverride = endpointOverride;
            return this;
        }

        public Builder setMaxConnectionsHttpClient(@Nullable Integer maxConnectionsHttpClient) {
            this.maxConnections = maxConnectionsHttpClient;
            return this;
        }

        public Builder setConnectionAcquisitionTimeoutHttpClient(@Nullable Duration connectionAcquisitionTimeout) {
            this.connectionAcquisitionTimeout = connectionAcquisitionTimeout;
            return this;
        }

        public Builder setCredentialsProviderFactory(CredentialsProviderFactory credentialsProviderFactory) {
            this.credentialsProviderFactory = credentialsProviderFactory;
            return this;
        }

        public Builder setOperationExecutor(OperationExecutor operationExecutor) {
            this.operationExecutor = operationExecutor;
            return this;
        }
    }

    public static interface CredentialsProviderFactory {
        public AwsCredentialsProvider build();
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpEntity
 *  org.apache.http.client.config.RequestConfig
 *  org.apache.http.client.methods.CloseableHttpResponse
 *  org.apache.http.client.methods.HttpGet
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.impl.client.CloseableHttpClient
 *  org.apache.http.util.EntityUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.status.service;

import com.atlassian.confluence.status.service.HttpClientFactory;
import com.atlassian.confluence.status.service.systeminfo.CloudPlatform;
import com.atlassian.confluence.status.service.systeminfo.CloudPlatformMetadata;
import com.atlassian.confluence.status.service.systeminfo.CloudPlatformType;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloudPlatformMetadataService {
    private final Logger logger = LoggerFactory.getLogger(CloudPlatformMetadataService.class);
    private final HttpClientFactory httpClientFactory;
    static final int SOCKET_TIMEOUT = 3000;
    static final int CONNECTION_TIMEOUT = 3000;

    public CloudPlatformMetadataService(HttpClientFactory httpClientFactory) {
        this.httpClientFactory = Objects.requireNonNull(httpClientFactory);
    }

    public Optional<CloudPlatformMetadata> getCloudPlatformMetadata(CloudPlatformType platformType) {
        if (platformType == null) {
            return Optional.empty();
        }
        return this.getCloudPlatformMetadata(platformType.getCloudPlatform());
    }

    private String parseInstanceType(CloudPlatform cloudPlatform, HttpEntity entity) {
        String responseBody = "";
        try {
            responseBody = cloudPlatform.parseInstanceType(EntityUtils.toString((HttpEntity)entity, (String)"UTF-8"));
        }
        catch (IOException | RuntimeException e) {
            this.logger.debug("Error parsing response body", (Throwable)e);
        }
        return responseBody;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    Optional<CloudPlatformMetadata> getCloudPlatformMetadata(CloudPlatform cloudPlatform) {
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(3000).setConnectTimeout(3000).setCookieSpec("standard").build();
        try (CloseableHttpClient httpClient = this.httpClientFactory.getInstance(requestConfig);){
            CloudPlatformType platformType = cloudPlatform.getPlatformType();
            HttpGet metadataGet = new HttpGet(cloudPlatform.getInstanceTypeMetadataEndpoint());
            cloudPlatform.getMetadataHeaders().forEach((header, value) -> metadataGet.setHeader(header, value));
            try {
                CloseableHttpResponse response;
                block19: {
                    response = httpClient.execute((HttpUriRequest)metadataGet);
                    if (response.getStatusLine().getStatusCode() != 200) break block19;
                    CloudPlatformMetadata.Builder builder = CloudPlatformMetadata.builder();
                    builder.cloudPlatform(platformType);
                    builder.instanceType(this.parseInstanceType(cloudPlatform, response.getEntity()));
                    Optional<CloudPlatformMetadata> optional = Optional.of(builder.build());
                    if (response == null) return optional;
                    response.close();
                    return optional;
                }
                try {
                    this.logger.debug("Metadata endpoint status was {}", (Object)response.getStatusLine().getStatusCode());
                    return Optional.empty();
                }
                finally {
                    if (response != null) {
                        response.close();
                    }
                }
            }
            catch (IOException e) {
                this.logger.debug("Unexpected error trying to reach {} metadata endpoint", (Object)platformType, (Object)e);
                return Optional.empty();
            }
        }
        catch (IOException | IllegalArgumentException e) {
            this.logger.debug("Unexpected error with HttpClient", (Throwable)e);
        }
        return Optional.empty();
    }
}


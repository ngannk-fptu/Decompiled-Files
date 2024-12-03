/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.internal;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.SdkClientException;
import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.annotation.SdkTestInternalApi;
import com.amazonaws.internal.ConnectionUtils;
import com.amazonaws.internal.EC2ResourceFetcher;
import com.amazonaws.retry.internal.CredentialsEndpointRetryPolicy;
import com.amazonaws.util.EC2MetadataUtils;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SdkInternalApi
public final class InstanceMetadataServiceResourceFetcher
extends EC2ResourceFetcher {
    private static final Log LOG = LogFactory.getLog(InstanceMetadataServiceResourceFetcher.class);
    private static final String EC2_TOKEN_ROOT = "/latest/api/token";
    private static final String TOKEN_TTL_HEADER = "x-aws-ec2-metadata-token-ttl-seconds";
    private static final String TOKEN_HEADER = "x-aws-ec2-metadata-token";
    private static final String DEFAULT_TOKEN_TTL = "21600";

    private InstanceMetadataServiceResourceFetcher() {
    }

    @SdkTestInternalApi
    InstanceMetadataServiceResourceFetcher(ConnectionUtils connectionUtils) {
        super(connectionUtils);
    }

    public static InstanceMetadataServiceResourceFetcher getInstance() {
        return InstanceMetadataServiceResourceFetcherHolder.INSTANCE;
    }

    @Override
    public String readResource(URI endpoint, CredentialsEndpointRetryPolicy retryPolicy, Map<String, String> headers) {
        if (SDKGlobalConfiguration.isEc2MetadataDisabled()) {
            throw new AmazonClientException("EC2 Instance Metadata Service is disabled");
        }
        Map<String, String> newHeaders = this.addDefaultHeaders(headers);
        String token = this.getToken();
        if (token != null) {
            newHeaders.put(TOKEN_HEADER, token);
        }
        return this.doReadResource(endpoint, retryPolicy, newHeaders);
    }

    private String getToken() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put(TOKEN_TTL_HEADER, DEFAULT_TOKEN_TTL);
        String token = null;
        String host = EC2MetadataUtils.getHostAddressForEC2MetadataService();
        try {
            token = this.doReadResource(URI.create(host + EC2_TOKEN_ROOT), CredentialsEndpointRetryPolicy.NO_RETRY, headers, "PUT");
        }
        catch (SdkClientException e) {
            this.handleException(e);
        }
        return token;
    }

    private void handleException(SdkClientException e) {
        if (this.isTokenUnsupported(e)) {
            LOG.debug((Object)"Token is not supported. Ignoring ");
            return;
        }
        LOG.warn((Object)"Fail to retrieve token ", (Throwable)e);
        throw e;
    }

    private boolean isTokenUnsupported(SdkClientException sdkClientException) {
        if (sdkClientException instanceof AmazonServiceException) {
            AmazonServiceException serviceException = (AmazonServiceException)sdkClientException;
            return serviceException.getStatusCode() != 400 && !this.isRetryable(serviceException.getCause()) && !this.isRetryable(serviceException.getStatusCode());
        }
        return sdkClientException.getCause() instanceof SocketTimeoutException || sdkClientException.getMessage().contains("The requested metadata is not found at ");
    }

    private boolean isRetryable(int statusCode) {
        return statusCode >= 500 && statusCode < 600;
    }

    private boolean isRetryable(Throwable exception) {
        return exception instanceof IOException;
    }

    private static final class InstanceMetadataServiceResourceFetcherHolder {
        private static final InstanceMetadataServiceResourceFetcher INSTANCE = new InstanceMetadataServiceResourceFetcher();

        private InstanceMetadataServiceResourceFetcherHolder() {
        }
    }
}


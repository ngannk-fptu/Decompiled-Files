/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.internal;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.annotation.SdkTestInternalApi;
import com.amazonaws.internal.ConnectionUtils;
import com.amazonaws.retry.internal.CredentialsEndpointRetryParameters;
import com.amazonaws.retry.internal.CredentialsEndpointRetryPolicy;
import com.amazonaws.util.IOUtils;
import com.amazonaws.util.VersionInfoUtils;
import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SdkInternalApi
public abstract class EC2ResourceFetcher {
    private static final Log LOG = LogFactory.getLog(EC2ResourceFetcher.class);
    private final ConnectionUtils connectionUtils;
    private static final String USER_AGENT = VersionInfoUtils.getUserAgent();

    EC2ResourceFetcher() {
        this.connectionUtils = ConnectionUtils.getInstance();
    }

    @SdkTestInternalApi
    EC2ResourceFetcher(ConnectionUtils connectionUtils) {
        this.connectionUtils = connectionUtils;
    }

    public static EC2ResourceFetcher defaultResourceFetcher() {
        return DefaultEC2ResourceFetcher.DEFAULT_BASE_RESOURCE_FETCHER;
    }

    public abstract String readResource(URI var1, CredentialsEndpointRetryPolicy var2, Map<String, String> var3);

    public final String readResource(URI endpoint) {
        return this.readResource(endpoint, CredentialsEndpointRetryPolicy.NO_RETRY, null);
    }

    public final String readResource(URI endpoint, CredentialsEndpointRetryPolicy retryPolicy) {
        return this.readResource(endpoint, retryPolicy, null);
    }

    final String doReadResource(URI endpoint, CredentialsEndpointRetryPolicy retryPolicy, Map<String, String> headers) {
        return this.doReadResource(endpoint, retryPolicy, headers, "GET");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final String doReadResource(URI endpoint, CredentialsEndpointRetryPolicy retryPolicy, Map<String, String> headers, String method) {
        int retriesAttempted = 0;
        InputStream inputStream = null;
        Map<String, String> headersToSent = this.addDefaultHeaders(headers);
        while (true) {
            block9: {
                int statusCode;
                HttpURLConnection connection;
                block8: {
                    connection = this.connectionUtils.connectToEndpoint(endpoint, headersToSent, method);
                    statusCode = connection.getResponseCode();
                    if (statusCode != 200) break block8;
                    inputStream = connection.getInputStream();
                    String string = IOUtils.toString(inputStream);
                    IOUtils.closeQuietly(inputStream, LOG);
                    return string;
                }
                try {
                    if (statusCode == 404) {
                        throw new SdkClientException("The requested metadata is not found at " + connection.getURL());
                    }
                    if (retryPolicy.shouldRetry(retriesAttempted++, CredentialsEndpointRetryParameters.builder().withStatusCode(statusCode).build())) break block9;
                    inputStream = connection.getErrorStream();
                    this.handleErrorResponse(inputStream, statusCode, connection.getResponseMessage());
                }
                catch (IOException ioException) {
                    try {
                        if (!retryPolicy.shouldRetry(retriesAttempted++, CredentialsEndpointRetryParameters.builder().withException(ioException).build())) {
                            throw new SdkClientException("Failed to connect to service endpoint: ", ioException);
                        }
                        LOG.debug((Object)("An IOException occurred when connecting to service endpoint: " + endpoint + "\n Retrying to connect again."));
                    }
                    catch (Throwable throwable) {
                        IOUtils.closeQuietly(inputStream, LOG);
                        throw throwable;
                    }
                    IOUtils.closeQuietly(inputStream, LOG);
                    continue;
                }
            }
            IOUtils.closeQuietly(inputStream, LOG);
            continue;
            break;
        }
    }

    protected final Map<String, String> addDefaultHeaders(Map<String, String> headers) {
        HashMap<String, String> map = new HashMap<String, String>();
        if (headers != null) {
            map.putAll(headers);
        }
        this.putIfAbsent(map, "User-Agent", USER_AGENT);
        this.putIfAbsent(map, "Accept", "*/*");
        this.putIfAbsent(map, "Connection", "keep-alive");
        return map;
    }

    private <K, V> void putIfAbsent(Map<K, V> map, K key, V value) {
        if (map.get(key) == null) {
            map.put(key, value);
        }
    }

    private void handleErrorResponse(InputStream errorStream, int statusCode, String responseMessage) throws IOException {
        String errorCode = null;
        if (errorStream != null) {
            String errorResponse = IOUtils.toString(errorStream);
            try {
                JsonNode node = Jackson.jsonNodeOf(errorResponse);
                JsonNode code = node.get("code");
                JsonNode message = node.get("message");
                if (code != null && message != null) {
                    errorCode = code.asText();
                    responseMessage = message.asText();
                }
            }
            catch (Exception exception) {
                LOG.debug((Object)"Unable to parse error stream");
            }
        }
        AmazonServiceException ase = new AmazonServiceException(responseMessage);
        ase.setStatusCode(statusCode);
        ase.setErrorCode(errorCode);
        throw ase;
    }

    static final class DefaultEC2ResourceFetcher
    extends EC2ResourceFetcher {
        private static final DefaultEC2ResourceFetcher DEFAULT_BASE_RESOURCE_FETCHER = new DefaultEC2ResourceFetcher();

        DefaultEC2ResourceFetcher() {
        }

        @SdkTestInternalApi
        DefaultEC2ResourceFetcher(ConnectionUtils connectionUtils) {
            super(connectionUtils);
        }

        @Override
        public String readResource(URI endpoint, CredentialsEndpointRetryPolicy retryPolicy, Map<String, String> headers) {
            return this.doReadResource(endpoint, retryPolicy, headers);
        }
    }
}


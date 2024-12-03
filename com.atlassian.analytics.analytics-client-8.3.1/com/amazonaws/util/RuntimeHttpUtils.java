/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpHost
 *  org.apache.http.auth.AuthScope
 *  org.apache.http.auth.Credentials
 *  org.apache.http.auth.NTCredentials
 *  org.apache.http.client.HttpClient
 *  org.apache.http.client.methods.CloseableHttpResponse
 *  org.apache.http.client.methods.HttpGet
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.impl.client.DefaultHttpClient
 *  org.apache.http.params.BasicHttpParams
 *  org.apache.http.params.HttpConnectionParams
 *  org.apache.http.params.HttpParams
 *  org.apache.http.params.HttpProtocolParams
 */
package com.amazonaws.util;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.Request;
import com.amazonaws.SdkClientException;
import com.amazonaws.annotation.SdkProtectedApi;
import com.amazonaws.retry.RetryMode;
import com.amazonaws.util.HttpClientWrappingInputStream;
import com.amazonaws.util.SdkHttpUtils;
import com.amazonaws.util.StringUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

public class RuntimeHttpUtils {
    private static final String COMMA = ", ";
    private static final String SPACE = " ";
    private static final String AWS_EXECUTION_ENV_PREFIX = "exec-env/";
    private static final String AWS_EXECUTION_ENV_NAME = "AWS_EXECUTION_ENV";
    private static final String RETRY_MODE_PREFIX = "cfg/retry-mode/";
    private static final String TRACE_ID_ENVIRONMENT_VARIABLE = "_X_AMZN_TRACE_ID";
    private static final String LAMBDA_FUNCTION_NAME_ENVIRONMENT_VARIABLE = "AWS_LAMBDA_FUNCTION_NAME";

    public static InputStream fetchFile(URI uri, ClientConfiguration config) throws IOException {
        CloseableHttpResponse response;
        BasicHttpParams httpClientParams = new BasicHttpParams();
        HttpProtocolParams.setUserAgent((HttpParams)httpClientParams, (String)RuntimeHttpUtils.getUserAgent(config, null));
        HttpConnectionParams.setConnectionTimeout((HttpParams)httpClientParams, (int)RuntimeHttpUtils.getConnectionTimeout(config));
        HttpConnectionParams.setSoTimeout((HttpParams)httpClientParams, (int)RuntimeHttpUtils.getSocketTimeout(config));
        DefaultHttpClient httpclient = new DefaultHttpClient((HttpParams)httpClientParams);
        if (config != null) {
            String proxyHost = config.getProxyHost();
            int proxyPort = config.getProxyPort();
            if (proxyHost != null && proxyPort > 0) {
                HttpHost proxy = new HttpHost(proxyHost, proxyPort);
                httpclient.getParams().setParameter("http.route.default-proxy", (Object)proxy);
                if (config.getProxyUsername() != null && config.getProxyPassword() != null) {
                    httpclient.getCredentialsProvider().setCredentials(new AuthScope(proxyHost, proxyPort), (Credentials)new NTCredentials(config.getProxyUsername(), config.getProxyPassword(), config.getProxyWorkstation(), config.getProxyDomain()));
                }
            }
        }
        if ((response = httpclient.execute((HttpUriRequest)new HttpGet(uri))).getStatusLine().getStatusCode() != 200) {
            throw new IOException("Error fetching file from " + uri + ": " + response);
        }
        return new HttpClientWrappingInputStream((HttpClient)httpclient, response.getEntity().getContent());
    }

    public static String getUserAgent(ClientConfiguration config, String userAgentMarker) {
        String userDefinedPrefix = "";
        String userDefinedSuffix = "";
        String retryModeName = "";
        String awsExecutionEnvironment = RuntimeHttpUtils.getEnvironmentVariable(AWS_EXECUTION_ENV_NAME);
        if (config != null) {
            userDefinedPrefix = config.getUserAgentPrefix();
            userDefinedSuffix = config.getUserAgentSuffix();
            RetryMode retryMode = config.getRetryMode() == null ? config.getRetryPolicy().getRetryMode() : config.getRetryMode();
            retryModeName = retryMode != null ? retryMode.getName() : "";
        }
        StringBuilder userAgent = new StringBuilder(userDefinedPrefix.trim());
        if (!ClientConfiguration.DEFAULT_USER_AGENT.equals(userDefinedPrefix)) {
            userAgent.append(COMMA).append(ClientConfiguration.DEFAULT_USER_AGENT);
        }
        if (StringUtils.hasValue(retryModeName)) {
            userAgent.append(SPACE).append(RETRY_MODE_PREFIX).append(retryModeName.trim());
        }
        if (StringUtils.hasValue(userDefinedSuffix)) {
            userAgent.append(COMMA).append(userDefinedSuffix.trim());
        }
        if (StringUtils.hasValue(awsExecutionEnvironment)) {
            userAgent.append(SPACE).append(AWS_EXECUTION_ENV_PREFIX).append(awsExecutionEnvironment.trim());
        }
        if (StringUtils.hasValue(userAgentMarker)) {
            userAgent.append(SPACE).append(userAgentMarker.trim());
        }
        return userAgent.toString();
    }

    private static String getEnvironmentVariable(String environmentVariableName) {
        try {
            return System.getenv(environmentVariableName);
        }
        catch (Exception e) {
            return "";
        }
    }

    private static int getConnectionTimeout(ClientConfiguration config) {
        if (config != null) {
            return config.getConnectionTimeout();
        }
        return 10000;
    }

    private static int getSocketTimeout(ClientConfiguration config) {
        if (config != null) {
            return config.getSocketTimeout();
        }
        return 50000;
    }

    public static URI toUri(String endpoint, ClientConfiguration config) {
        if (config == null) {
            throw new IllegalArgumentException("ClientConfiguration cannot be null");
        }
        return RuntimeHttpUtils.toUri(endpoint, config.getProtocol());
    }

    public static URI toUri(String endpoint, Protocol protocol) {
        if (endpoint == null) {
            throw new IllegalArgumentException("endpoint cannot be null");
        }
        if (!endpoint.contains("://")) {
            endpoint = protocol.toString() + "://" + endpoint;
        }
        try {
            return new URI(endpoint);
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @SdkProtectedApi
    public static URL convertRequestToUrl(Request<?> request, boolean removeLeadingSlashInResourcePath, boolean urlEncode) {
        String resourcePath;
        String string = resourcePath = urlEncode ? SdkHttpUtils.urlEncode(request.getResourcePath(), true) : request.getResourcePath();
        if (removeLeadingSlashInResourcePath && resourcePath.startsWith("/")) {
            resourcePath = resourcePath.substring(1);
        }
        String urlPath = "/" + resourcePath;
        urlPath = urlPath.replaceAll("(?<=/)/", "%2F");
        StringBuilder url = new StringBuilder(request.getEndpoint().toString());
        url.append(urlPath);
        StringBuilder queryParams = new StringBuilder();
        Map<String, List<String>> requestParams = request.getParameters();
        for (Map.Entry<String, List<String>> entry : requestParams.entrySet()) {
            for (String value : entry.getValue()) {
                queryParams = queryParams.length() > 0 ? queryParams.append("&") : queryParams.append("?");
                queryParams.append(SdkHttpUtils.urlEncode(entry.getKey(), false)).append("=").append(SdkHttpUtils.urlEncode(value, false));
            }
        }
        url.append(queryParams.toString());
        try {
            return new URL(url.toString());
        }
        catch (MalformedURLException e) {
            throw new SdkClientException("Unable to convert request to well formed URL: " + e.getMessage(), e);
        }
    }

    public static String getLambdaEnvironmentTraceId() {
        String lambdafunctionName = RuntimeHttpUtils.getEnvironmentVariable(LAMBDA_FUNCTION_NAME_ENVIRONMENT_VARIABLE);
        String traceId = RuntimeHttpUtils.getEnvironmentVariable(TRACE_ID_ENVIRONMENT_VARIABLE);
        if (!StringUtils.isNullOrEmpty(lambdafunctionName) && !StringUtils.isNullOrEmpty(traceId)) {
            return traceId;
        }
        return null;
    }
}


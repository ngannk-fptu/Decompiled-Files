/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  org.apache.hc.client5.http.auth.AuthScope
 *  org.apache.hc.client5.http.auth.Credentials
 *  org.apache.hc.client5.http.auth.CredentialsProvider
 *  org.apache.hc.client5.http.auth.UsernamePasswordCredentials
 *  org.apache.hc.client5.http.config.ConnectionConfig
 *  org.apache.hc.client5.http.config.RequestConfig
 *  org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider
 *  org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager
 *  org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder
 *  org.apache.hc.client5.http.nio.AsyncClientConnectionManager
 *  org.apache.hc.core5.http.HttpHost
 *  org.opensearch.client.opensearch.OpenSearchClient
 *  org.opensearch.client.transport.OpenSearchTransport
 *  org.opensearch.client.transport.aws.AwsSdk2Transport
 *  org.opensearch.client.transport.aws.AwsSdk2TransportOptions
 *  org.opensearch.client.transport.httpclient5.ApacheHttpClient5TransportBuilder
 *  software.amazon.awssdk.http.SdkHttpClient
 *  software.amazon.awssdk.http.apache.ApacheHttpClient
 *  software.amazon.awssdk.regions.Region
 */
package com.atlassian.confluence.plugins.opensearch;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.plugins.opensearch.OpenSearchConfig;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.client5.http.nio.AsyncClientConnectionManager;
import org.apache.hc.core5.http.HttpHost;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.aws.AwsSdk2Transport;
import org.opensearch.client.transport.aws.AwsSdk2TransportOptions;
import org.opensearch.client.transport.httpclient5.ApacheHttpClient5TransportBuilder;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;

@ParametersAreNonnullByDefault
public class OpenSearchClientFactory {
    private final OpenSearchConfig config;

    public OpenSearchClientFactory(OpenSearchConfig config) {
        this.config = Objects.requireNonNull(config, "config is required");
    }

    public OpenSearchClient createClient() throws URISyntaxException {
        OpenSearchTransport transport = this.config.getAWSRegion().isPresent() ? this.createAwsTransport(this.config.getAWSRegion().get()) : this.createApacheHttpClientTransport();
        return new OpenSearchClient(transport);
    }

    private List<HttpHost> parseUrls(String urls) {
        return Arrays.stream(urls.split(",")).map(url -> {
            try {
                return HttpHost.create((String)url);
            }
            catch (URISyntaxException e) {
                throw new IllegalStateException(e);
            }
        }).collect(Collectors.toList());
    }

    private OpenSearchTransport createApacheHttpClientTransport() {
        List<HttpHost> hosts = this.parseUrls(this.config.getHttpUrl());
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        this.config.getUsernamePassword().ifPresent(usernamePassword -> hosts.forEach(host -> credentialsProvider.setCredentials(new AuthScope(host), (Credentials)new UsernamePasswordCredentials((String)usernamePassword.getLeft(), ((String)usernamePassword.getRight()).toCharArray()))));
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(this.config.getConnectRequestTimeout().longValue(), TimeUnit.SECONDS).build();
        ConnectionConfig connectionConfig = ConnectionConfig.custom().setConnectTimeout(this.config.getConnectTimout().longValue(), TimeUnit.SECONDS).setSocketTimeout(this.config.getSocketTimeout().intValue(), TimeUnit.SECONDS).build();
        PoolingAsyncClientConnectionManager connectionManager = PoolingAsyncClientConnectionManagerBuilder.create().setDefaultConnectionConfig(connectionConfig).build();
        return ApacheHttpClient5TransportBuilder.builder((HttpHost[])hosts.toArray(new HttpHost[0])).setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder.setConnectionManager((AsyncClientConnectionManager)connectionManager).setDefaultRequestConfig(requestConfig).setDefaultCredentialsProvider((CredentialsProvider)credentialsProvider)).build();
    }

    private OpenSearchTransport createAwsTransport(String awsRegion) {
        SdkHttpClient client = ApacheHttpClient.builder().connectionAcquisitionTimeout(Duration.ofSeconds(this.config.getConnectRequestTimeout())).connectionTimeout(Duration.ofSeconds(this.config.getConnectTimout())).socketTimeout(Duration.ofSeconds(this.config.getSocketTimeout().intValue())).build();
        return new AwsSdk2Transport(client, this.config.getHttpUrl(), "es", Region.of((String)awsRegion), AwsSdk2TransportOptions.builder().build());
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpEntity
 *  org.apache.http.client.methods.CloseableHttpResponse
 *  org.apache.http.client.methods.HttpPost
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.entity.ContentType
 *  org.apache.http.entity.mime.MultipartEntityBuilder
 *  org.apache.http.entity.mime.content.ContentBody
 *  org.apache.http.entity.mime.content.StringBody
 *  org.apache.http.impl.client.CloseableHttpClient
 *  org.apache.http.impl.client.HttpClients
 *  org.apache.http.util.EntityUtils
 */
package com.atlassian.logging.log4j.appender.fluentd;

import com.atlassian.logging.log4j.appender.fluentd.FluentdRetryableException;
import com.atlassian.logging.log4j.appender.fluentd.FluentdSender;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class FluentdHttpSender
implements FluentdSender {
    private static final int EXPECTED_RESPONSE_CODE = 200;
    private static final int MAX_CONNECTIONS_PER_ROUTE = 1;
    private final String fluentdEndpoint;
    private final CloseableHttpClient httpClient = HttpClients.custom().setMaxConnPerRoute(1).build();

    public FluentdHttpSender(String fluentdEndpoint) {
        this.fluentdEndpoint = this.resolveHostName(fluentdEndpoint);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized void send(String payload) throws FluentdRetryableException {
        HttpPost httpPost = new HttpPost(this.fluentdEndpoint);
        HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("json", (ContentBody)new StringBody(payload, ContentType.APPLICATION_JSON)).build();
        httpPost.setEntity(reqEntity);
        try (CloseableHttpResponse response = this.httpClient.execute((HttpUriRequest)httpPost);){
            EntityUtils.consume((HttpEntity)response.getEntity());
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new FluentdRetryableException("Bad status code return from fluentD: " + response.getStatusLine());
            }
        }
        catch (IOException e) {
            throw new FluentdRetryableException(e);
        }
    }

    private String resolveHostName(String endpoint) {
        try {
            String hostName = new URL(endpoint).getHost();
            InetAddress[] inetAddressArray = InetAddress.getAllByName(hostName);
            int n = inetAddressArray.length;
            int n2 = 0;
            if (n2 < n) {
                InetAddress address = inetAddressArray[n2];
                return endpoint.replace(hostName, address.getHostAddress());
            }
        }
        catch (MalformedURLException | UnknownHostException exc) {
            System.err.println("Cannot resolve hostname of the endpoint");
            exc.printStackTrace(System.err);
        }
        return endpoint;
    }
}


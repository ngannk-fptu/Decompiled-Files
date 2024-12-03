/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.http.HttpHeader
 *  org.eclipse.jetty.io.EndPoint
 *  org.eclipse.jetty.util.Callback
 *  org.eclipse.jetty.util.Promise
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.client.http;

import java.util.HashMap;
import java.util.List;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpClientTransport;
import org.eclipse.jetty.client.HttpDestination;
import org.eclipse.jetty.client.HttpRequest;
import org.eclipse.jetty.client.HttpResponse;
import org.eclipse.jetty.client.HttpResponseException;
import org.eclipse.jetty.client.HttpUpgrader;
import org.eclipse.jetty.client.Origin;
import org.eclipse.jetty.client.dynamic.HttpClientTransportDynamic;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtocolHttpUpgrader
implements HttpUpgrader {
    private static final Logger LOG = LoggerFactory.getLogger(ProtocolHttpUpgrader.class);
    private final HttpDestination destination;
    private final String protocol;

    public ProtocolHttpUpgrader(HttpDestination destination, String protocol) {
        this.destination = destination;
        this.protocol = protocol;
    }

    @Override
    public void prepare(HttpRequest request) {
    }

    @Override
    public void upgrade(HttpResponse response, EndPoint endPoint, Callback callback) {
        if (response.getHeaders().contains(HttpHeader.UPGRADE, this.protocol)) {
            HttpClient httpClient = this.destination.getHttpClient();
            HttpClientTransport transport = httpClient.getTransport();
            if (transport instanceof HttpClientTransportDynamic) {
                HttpClientTransportDynamic dynamicTransport = (HttpClientTransportDynamic)transport;
                Origin origin = this.destination.getOrigin();
                Origin newOrigin = new Origin(origin.getScheme(), origin.getAddress(), origin.getTag(), new Origin.Protocol(List.of(this.protocol), false));
                HttpDestination newDestination = httpClient.resolveDestination(newOrigin);
                HashMap<String, Object> context = new HashMap<String, Object>();
                context.put("org.eclipse.jetty.client.destination", newDestination);
                context.put(HttpResponse.class.getName(), response);
                context.put("org.eclipse.jetty.client.connection.promise", Promise.from(y -> callback.succeeded(), arg_0 -> ((Callback)callback).failed(arg_0)));
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Upgrading {} on {}", (Object)response.getRequest(), (Object)endPoint);
                }
                dynamicTransport.upgrade(endPoint, context);
            } else {
                callback.failed((Throwable)new HttpResponseException(HttpClientTransportDynamic.class.getName() + " required to upgrade to: " + this.protocol, response));
            }
        } else {
            callback.failed((Throwable)new HttpResponseException("Not an upgrade to: " + this.protocol, response));
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.channel.EventLoopGroup;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.async.AsyncExecuteRequest;
import software.amazon.awssdk.http.async.SdkAsyncHttpResponseHandler;
import software.amazon.awssdk.http.nio.netty.internal.NettyConfiguration;
import software.amazon.awssdk.http.nio.netty.internal.SdkChannelPool;
import software.amazon.awssdk.metrics.MetricCollector;
import software.amazon.awssdk.metrics.NoOpMetricCollector;

@SdkInternalApi
public final class RequestContext {
    private final SdkChannelPool channelPool;
    private final EventLoopGroup eventLoopGroup;
    private final AsyncExecuteRequest executeRequest;
    private final NettyConfiguration configuration;
    private final MetricCollector metricCollector;

    public RequestContext(SdkChannelPool channelPool, EventLoopGroup eventLoopGroup, AsyncExecuteRequest executeRequest, NettyConfiguration configuration) {
        this.channelPool = channelPool;
        this.eventLoopGroup = eventLoopGroup;
        this.executeRequest = executeRequest;
        this.configuration = configuration;
        this.metricCollector = executeRequest.metricCollector().orElseGet(NoOpMetricCollector::create);
    }

    public SdkChannelPool channelPool() {
        return this.channelPool;
    }

    public EventLoopGroup eventLoopGroup() {
        return this.eventLoopGroup;
    }

    public AsyncExecuteRequest executeRequest() {
        return this.executeRequest;
    }

    public SdkAsyncHttpResponseHandler handler() {
        return this.executeRequest().responseHandler();
    }

    public NettyConfiguration configuration() {
        return this.configuration;
    }

    public MetricCollector metricCollector() {
        return this.metricCollector;
    }
}


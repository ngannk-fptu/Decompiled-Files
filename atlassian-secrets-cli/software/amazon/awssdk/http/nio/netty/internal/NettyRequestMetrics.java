/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.channel.Channel;
import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.Http2Stream;
import io.netty.util.concurrent.Future;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.Http2Metric;
import software.amazon.awssdk.http.nio.netty.internal.ChannelAttributeKey;
import software.amazon.awssdk.metrics.MetricCollector;
import software.amazon.awssdk.metrics.NoOpMetricCollector;

@SdkInternalApi
public class NettyRequestMetrics {
    private NettyRequestMetrics() {
    }

    public static boolean metricsAreEnabled(MetricCollector metricCollector) {
        return metricCollector != null && !(metricCollector instanceof NoOpMetricCollector);
    }

    public static void ifMetricsAreEnabled(MetricCollector metrics, Consumer<MetricCollector> metricsConsumer) {
        if (NettyRequestMetrics.metricsAreEnabled(metrics)) {
            metricsConsumer.accept(metrics);
        }
    }

    public static void publishHttp2StreamMetrics(MetricCollector metricCollector, Channel channel) {
        if (!NettyRequestMetrics.metricsAreEnabled(metricCollector)) {
            return;
        }
        NettyRequestMetrics.getHttp2Connection(channel).ifPresent(http2Connection -> NettyRequestMetrics.writeHttp2RequestMetrics(metricCollector, channel, http2Connection));
    }

    private static Optional<Http2Connection> getHttp2Connection(Channel channel) {
        Channel parentChannel = channel.parent();
        if (parentChannel == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(parentChannel.attr(ChannelAttributeKey.HTTP2_CONNECTION).get());
    }

    private static void writeHttp2RequestMetrics(MetricCollector metricCollector, Channel channel, Http2Connection http2Connection) {
        int streamId = channel.attr(ChannelAttributeKey.HTTP2_FRAME_STREAM).get().id();
        Http2Stream stream = http2Connection.stream(streamId);
        metricCollector.reportMetric(Http2Metric.LOCAL_STREAM_WINDOW_SIZE_IN_BYTES, http2Connection.local().flowController().windowSize(stream));
        metricCollector.reportMetric(Http2Metric.REMOTE_STREAM_WINDOW_SIZE_IN_BYTES, http2Connection.remote().flowController().windowSize(stream));
    }

    public static void measureTimeTaken(Future<?> future, Consumer<Duration> onDone) {
        Instant start = Instant.now();
        future.addListener(f -> {
            Duration elapsed = Duration.between(start, Instant.now());
            onDone.accept(elapsed);
        });
    }
}


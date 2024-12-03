/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.ToString
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.channel.Channel;
import java.time.Duration;
import java.time.Instant;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.utils.ToString;

@SdkInternalApi
public class ChannelDiagnostics {
    private final Channel channel;
    private final Instant channelCreationTime;
    private int requestCount = 0;
    private int responseCount = 0;
    private long idleStart = -1L;
    private long idleStop = -1L;

    public ChannelDiagnostics(Channel channel) {
        this.channel = channel;
        this.channelCreationTime = Instant.now();
    }

    public void incrementRequestCount() {
        ++this.requestCount;
    }

    public void incrementResponseCount() {
        ++this.responseCount;
    }

    public int responseCount() {
        return this.responseCount;
    }

    public void startIdleTimer() {
        this.idleStart = System.nanoTime();
    }

    public void stopIdleTimer() {
        this.idleStop = System.nanoTime();
    }

    public Duration lastIdleDuration() {
        if (this.idleStart > 0L && this.idleStop > this.idleStart) {
            return Duration.ofNanos(this.idleStop - this.idleStart);
        }
        if (this.idleStart > 0L) {
            return Duration.ofNanos(System.nanoTime() - this.idleStart);
        }
        return null;
    }

    public String toString() {
        return ToString.builder((String)"ChannelDiagnostics").add("channel", (Object)this.channel).add("channelAge", (Object)Duration.between(this.channelCreationTime, Instant.now())).add("requestCount", (Object)this.requestCount).add("responseCount", (Object)this.responseCount).add("lastIdleDuration", (Object)this.lastIdleDuration()).build();
    }
}


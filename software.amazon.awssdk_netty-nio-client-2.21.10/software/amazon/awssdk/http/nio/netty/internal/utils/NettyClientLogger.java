/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.annotations.SdkTestInternalApi
 */
package software.amazon.awssdk.http.nio.netty.internal.utils;

import io.netty.channel.Channel;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;

@SdkInternalApi
public final class NettyClientLogger {
    private final Logger delegateLogger;

    @SdkTestInternalApi
    NettyClientLogger(Logger delegateLogger) {
        this.delegateLogger = delegateLogger;
    }

    public static NettyClientLogger getLogger(Class<?> clzz) {
        Logger delegate = LoggerFactory.getLogger(clzz);
        return new NettyClientLogger(delegate);
    }

    public void debug(Channel channel, Supplier<String> msgSupplier) {
        this.debug(channel, msgSupplier, null);
    }

    public void debug(Channel channel, Supplier<String> msgSupplier, Throwable t) {
        if (!this.delegateLogger.isDebugEnabled()) {
            return;
        }
        String finalMessage = this.prependChannelInfo(msgSupplier, channel);
        this.delegateLogger.debug(finalMessage, t);
    }

    public void warn(Channel channel, Supplier<String> msgSupplier) {
        this.warn(channel, msgSupplier, null);
    }

    public void error(Channel channel, Supplier<String> msgSupplier, Throwable t) {
        if (!this.delegateLogger.isErrorEnabled()) {
            return;
        }
        String finalMessage = this.prependChannelInfo(msgSupplier, channel);
        this.delegateLogger.error(finalMessage, t);
    }

    public void error(Channel channel, Supplier<String> msgSupplier) {
        this.warn(channel, msgSupplier, null);
    }

    public void warn(Channel channel, Supplier<String> msgSupplier, Throwable t) {
        if (!this.delegateLogger.isWarnEnabled()) {
            return;
        }
        String finalMessage = this.prependChannelInfo(msgSupplier, channel);
        this.delegateLogger.warn(finalMessage, t);
    }

    public void trace(Channel channel, Supplier<String> msgSupplier) {
        if (!this.delegateLogger.isTraceEnabled()) {
            return;
        }
        String finalMessage = this.prependChannelInfo(msgSupplier, channel);
        this.delegateLogger.trace(finalMessage);
    }

    private String prependChannelInfo(Supplier<String> msgSupplier, Channel channel) {
        if (channel == null) {
            return msgSupplier.get();
        }
        String id = !this.delegateLogger.isDebugEnabled() ? channel.id().asShortText() : channel.toString();
        return String.format("[Channel: %s] %s", id, msgSupplier.get());
    }
}


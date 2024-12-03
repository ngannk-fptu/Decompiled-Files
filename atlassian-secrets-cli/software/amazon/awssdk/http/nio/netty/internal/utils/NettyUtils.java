/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal.utils;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.EventLoop;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.SucceededFuture;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.nio.netty.internal.ChannelAttributeKey;
import software.amazon.awssdk.http.nio.netty.internal.ChannelDiagnostics;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyClientLogger;
import software.amazon.awssdk.utils.FunctionalUtils;
import software.amazon.awssdk.utils.Logger;

@SdkInternalApi
public final class NettyUtils {
    public static final SucceededFuture<?> SUCCEEDED_FUTURE = new SucceededFuture<Object>(null, null);
    public static final String CLOSED_CHANNEL_ERROR_MESSAGE = "The connection was closed during the request. The request will usually succeed on a retry, but if it does not: consider disabling any proxies you have configured, enabling debug logging, or performing a TCP dump to identify the root cause. If this is a streaming operation, validate that data is being read or written in a timely manner.";
    private static final Logger log = Logger.loggerFor(NettyUtils.class);

    private NettyUtils() {
    }

    public static Throwable decorateException(Channel channel, Throwable originalCause) {
        if (NettyUtils.isAcquireTimeoutException(originalCause)) {
            return new Throwable(NettyUtils.getMessageForAcquireTimeoutException(), originalCause);
        }
        if (NettyUtils.isTooManyPendingAcquiresException(originalCause)) {
            return new Throwable(NettyUtils.getMessageForTooManyAcquireOperationsError(), originalCause);
        }
        if (originalCause instanceof ReadTimeoutException) {
            return new IOException("Read timed out", originalCause);
        }
        if (originalCause instanceof WriteTimeoutException) {
            return new IOException("Write timed out", originalCause);
        }
        if (originalCause instanceof ClosedChannelException || NettyUtils.isConnectionResetException(originalCause)) {
            return new IOException(NettyUtils.closedChannelMessage(channel), originalCause);
        }
        return originalCause;
    }

    private static boolean isConnectionResetException(Throwable originalCause) {
        String message = originalCause.getMessage();
        return originalCause instanceof IOException && message != null && message.contains("Connection reset by peer");
    }

    private static boolean isAcquireTimeoutException(Throwable originalCause) {
        String message = originalCause.getMessage();
        return originalCause instanceof TimeoutException && message != null && message.contains("Acquire operation took longer");
    }

    private static boolean isTooManyPendingAcquiresException(Throwable originalCause) {
        String message = originalCause.getMessage();
        return originalCause instanceof IllegalStateException && message != null && message.contains("Too many outstanding acquire operations");
    }

    private static String getMessageForAcquireTimeoutException() {
        return "Acquire operation took longer than the configured maximum time. This indicates that a request cannot get a connection from the pool within the specified maximum time. This can be due to high request rate.\nConsider taking any of the following actions to mitigate the issue: increase max connections, increase acquire timeout, or slowing the request rate.\nIncreasing the max connections can increase client throughput (unless the network interface is already fully utilized), but can eventually start to hit operation system limitations on the number of file descriptors used by the process. If you already are fully utilizing your network interface or cannot further increase your connection count, increasing the acquire timeout gives extra time for requests to acquire a connection before timing out. If the connections doesn't free up, the subsequent requests will still timeout.\nIf the above mechanisms are not able to fix the issue, try smoothing out your requests so that large traffic bursts cannot overload the client, being more efficient with the number of times you need to call AWS, or by increasing the number of hosts sending requests.";
    }

    private static String getMessageForTooManyAcquireOperationsError() {
        return "Maximum pending connection acquisitions exceeded. The request rate is too high for the client to keep up.\nConsider taking any of the following actions to mitigate the issue: increase max connections, increase max pending acquire count, decrease connection acquisition timeout, or slow the request rate.\nIncreasing the max connections can increase client throughput (unless the network interface is already fully utilized), but can eventually start to hit operation system limitations on the number of file descriptors used by the process. If you already are fully utilizing your network interface or cannot further increase your connection count, increasing the pending acquire count allows extra requests to be buffered by the client, but can cause additional request latency and higher memory usage. If your request latency or memory usage is already too high, decreasing the lease timeout will allow requests to fail more quickly, reducing the number of pending connection acquisitions, but likely won't decrease the total number of failed requests.\nIf the above mechanisms are not able to fix the issue, try smoothing out your requests so that large traffic bursts cannot overload the client, being more efficient with the number of times you need to call AWS, or by increasing the number of hosts sending requests.";
    }

    public static String closedChannelMessage(Channel channel) {
        ChannelDiagnostics channelDiagnostics = channel != null && channel.attr(ChannelAttributeKey.CHANNEL_DIAGNOSTICS) != null ? channel.attr(ChannelAttributeKey.CHANNEL_DIAGNOSTICS).get() : null;
        ChannelDiagnostics parentChannelDiagnostics = channel != null && channel.parent() != null && channel.parent().attr(ChannelAttributeKey.CHANNEL_DIAGNOSTICS) != null ? channel.parent().attr(ChannelAttributeKey.CHANNEL_DIAGNOSTICS).get() : null;
        StringBuilder error = new StringBuilder();
        error.append(CLOSED_CHANNEL_ERROR_MESSAGE);
        if (channelDiagnostics != null) {
            error.append(" Channel Information: ").append(channelDiagnostics);
            if (parentChannelDiagnostics != null) {
                error.append(" Parent Channel Information: ").append(parentChannelDiagnostics);
            }
        }
        return error.toString();
    }

    public static <SuccessT, PromiseT> BiConsumer<SuccessT, ? super Throwable> promiseNotifyingBiConsumer(Function<SuccessT, PromiseT> successFunction, Promise<PromiseT> promise) {
        return (success, fail) -> {
            if (fail != null) {
                promise.setFailure((Throwable)fail);
            } else {
                try {
                    promise.setSuccess(successFunction.apply(success));
                }
                catch (Throwable e) {
                    promise.setFailure(e);
                }
            }
        };
    }

    public static <SuccessT, PromiseT> BiConsumer<SuccessT, ? super Throwable> asyncPromiseNotifyingBiConsumer(BiConsumer<SuccessT, Promise<PromiseT>> successConsumer, Promise<PromiseT> promise) {
        return (success, fail) -> {
            if (fail != null) {
                promise.setFailure((Throwable)fail);
            } else {
                try {
                    successConsumer.accept(success, promise);
                }
                catch (Throwable e) {
                    promise.setFailure(e);
                }
            }
        };
    }

    public static <T> GenericFutureListener<Future<T>> promiseNotifyingListener(Promise<T> channelPromise) {
        return future -> {
            if (future.isSuccess()) {
                channelPromise.setSuccess(future.getNow());
            } else {
                channelPromise.setFailure(future.cause());
            }
        };
    }

    public static Future<?> doInEventLoop(EventExecutor eventExecutor, Runnable runnable) {
        if (eventExecutor.inEventLoop()) {
            try {
                runnable.run();
                return eventExecutor.newSucceededFuture(null);
            }
            catch (Throwable t) {
                return eventExecutor.newFailedFuture(t);
            }
        }
        return eventExecutor.submit(runnable);
    }

    public static void doInEventLoop(EventExecutor eventExecutor, Runnable runnable, Promise<?> promise) {
        try {
            if (eventExecutor.inEventLoop()) {
                runnable.run();
            } else {
                eventExecutor.submit(() -> {
                    try {
                        runnable.run();
                    }
                    catch (Throwable e) {
                        promise.setFailure(e);
                    }
                });
            }
        }
        catch (Throwable e) {
            promise.setFailure(e);
        }
    }

    public static void warnIfNotInEventLoop(EventLoop loop) {
        assert (loop.inEventLoop());
        if (!loop.inEventLoop()) {
            IllegalStateException exception = new IllegalStateException("Execution is not in the expected event loop. Please report this issue to the AWS SDK for Java team on GitHub, because it could result in race conditions.");
            log.warn(() -> "Execution is happening outside of the expected event loop.", exception);
        }
    }

    public static <T> AttributeKey<T> getOrCreateAttributeKey(String attr) {
        if (AttributeKey.exists(attr)) {
            return AttributeKey.valueOf(attr);
        }
        return AttributeKey.newInstance(attr);
    }

    public static SslHandler newSslHandler(SslContext sslContext, ByteBufAllocator alloc, String peerHost, int peerPort, Duration handshakeTimeout) {
        SslHandler sslHandler = sslContext.newHandler(alloc, peerHost, peerPort);
        sslHandler.setHandshakeTimeout(handshakeTimeout.toMillis(), TimeUnit.MILLISECONDS);
        NettyUtils.configureSslEngine(sslHandler.engine());
        return sslHandler;
    }

    private static void configureSslEngine(SSLEngine sslEngine) {
        SSLParameters sslParameters = sslEngine.getSSLParameters();
        sslParameters.setEndpointIdentificationAlgorithm("HTTPS");
        sslEngine.setSSLParameters(sslParameters);
    }

    public static <T> GenericFutureListener<Future<T>> consumeOrPropagate(Promise<?> destination, Consumer<T> onSuccess) {
        return f -> {
            if (f.isSuccess()) {
                try {
                    Object result = f.getNow();
                    onSuccess.accept(result);
                }
                catch (Throwable t) {
                    destination.tryFailure(t);
                }
            } else if (f.isCancelled()) {
                destination.cancel(false);
            } else {
                destination.tryFailure(f.cause());
            }
        };
    }

    public static <T> GenericFutureListener<Future<T>> runOrPropagate(Promise<?> destination, Runnable onSuccess) {
        return f -> {
            if (f.isSuccess()) {
                try {
                    onSuccess.run();
                }
                catch (Throwable t) {
                    destination.tryFailure(t);
                }
            } else if (f.isCancelled()) {
                destination.cancel(false);
            } else {
                destination.tryFailure(f.cause());
            }
        };
    }

    public static void runAndLogError(NettyClientLogger log, String errorMsg, FunctionalUtils.UnsafeRunnable runnable) {
        try {
            runnable.run();
        }
        catch (Exception e) {
            log.error(null, () -> errorMsg, e);
        }
    }
}


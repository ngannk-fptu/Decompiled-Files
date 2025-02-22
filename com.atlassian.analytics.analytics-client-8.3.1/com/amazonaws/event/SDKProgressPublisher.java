/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.event;

import com.amazonaws.event.DeliveryMode;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressEventType;
import com.amazonaws.event.ProgressListener;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import org.apache.commons.logging.LogFactory;

public class SDKProgressPublisher {
    private static volatile Future<?> latestFutureTask;

    public static Future<?> publishProgress(ProgressListener listener, ProgressEventType type) {
        if (listener == ProgressListener.NOOP || listener == null || type == null) {
            return null;
        }
        return SDKProgressPublisher.deliverEvent(listener, new ProgressEvent(type));
    }

    private static Future<?> deliverEvent(final ProgressListener listener, final ProgressEvent event) {
        DeliveryMode mode;
        if (listener instanceof DeliveryMode && (mode = (DeliveryMode)((Object)listener)).isSyncCallSafe()) {
            return SDKProgressPublisher.quietlyCallListener(listener, event);
        }
        latestFutureTask = LazyHolder.executor.submit(new Runnable(){

            @Override
            public void run() {
                listener.progressChanged(event);
            }
        });
        return latestFutureTask;
    }

    private static Future<?> quietlyCallListener(ProgressListener listener, ProgressEvent event) {
        try {
            listener.progressChanged(event);
        }
        catch (Throwable t) {
            LogFactory.getLog(SDKProgressPublisher.class).debug((Object)"Failure from the event listener", t);
        }
        return null;
    }

    public static Future<?> publishRequestContentLength(ProgressListener listener, long bytes) {
        return SDKProgressPublisher.publishByteCountEvent(listener, ProgressEventType.REQUEST_CONTENT_LENGTH_EVENT, bytes);
    }

    public static Future<?> publishResponseContentLength(ProgressListener listener, long bytes) {
        return SDKProgressPublisher.publishByteCountEvent(listener, ProgressEventType.RESPONSE_CONTENT_LENGTH_EVENT, bytes);
    }

    public static Future<?> publishRequestBytesTransferred(ProgressListener listener, long bytes) {
        return SDKProgressPublisher.publishByteCountEvent(listener, ProgressEventType.REQUEST_BYTE_TRANSFER_EVENT, bytes);
    }

    public static Future<?> publishResponseBytesTransferred(ProgressListener listener, long bytes) {
        return SDKProgressPublisher.publishByteCountEvent(listener, ProgressEventType.RESPONSE_BYTE_TRANSFER_EVENT, bytes);
    }

    private static Future<?> publishByteCountEvent(ProgressListener listener, ProgressEventType type, long bytes) {
        if (listener == ProgressListener.NOOP || listener == null || bytes <= 0L) {
            return null;
        }
        return SDKProgressPublisher.deliverEvent(listener, new ProgressEvent(type, bytes));
    }

    public static Future<?> publishRequestReset(ProgressListener listener, long bytesReset) {
        return SDKProgressPublisher.publishResetEvent(listener, ProgressEventType.HTTP_REQUEST_CONTENT_RESET_EVENT, bytesReset);
    }

    public static Future<?> publishResponseReset(ProgressListener listener, long bytesReset) {
        return SDKProgressPublisher.publishResetEvent(listener, ProgressEventType.HTTP_RESPONSE_CONTENT_RESET_EVENT, bytesReset);
    }

    public static Future<?> publishResponseBytesDiscarded(ProgressListener listener, long bytesDiscarded) {
        return SDKProgressPublisher.publishResetEvent(listener, ProgressEventType.RESPONSE_BYTE_DISCARD_EVENT, bytesDiscarded);
    }

    private static Future<?> publishResetEvent(ProgressListener listener, ProgressEventType resetEventType, long bytesReset) {
        if (bytesReset <= 0L) {
            return null;
        }
        if (listener == ProgressListener.NOOP || listener == null) {
            return null;
        }
        return SDKProgressPublisher.deliverEvent(listener, new ProgressEvent(resetEventType, bytesReset));
    }

    protected static ExecutorService getExecutorService() {
        return LazyHolder.executor;
    }

    protected static Future<?> setLatestFutureTask(Future<?> f) {
        latestFutureTask = f;
        return latestFutureTask;
    }

    @Deprecated
    public static void waitTillCompletion() throws InterruptedException, ExecutionException {
        if (latestFutureTask != null) {
            latestFutureTask.get();
        }
    }

    public static void shutdown(boolean now) {
        if (now) {
            LazyHolder.executor.shutdownNow();
        } else {
            LazyHolder.executor.shutdown();
        }
    }

    private static final class LazyHolder {
        private static final ExecutorService executor = LazyHolder.createNewExecutorService();

        private LazyHolder() {
        }

        private static ExecutorService createNewExecutorService() {
            return Executors.newSingleThreadExecutor(new ThreadFactory(){

                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setName("java-sdk-progress-listener-callback-thread");
                    t.setDaemon(true);
                    return t;
                }
            });
        }
    }
}


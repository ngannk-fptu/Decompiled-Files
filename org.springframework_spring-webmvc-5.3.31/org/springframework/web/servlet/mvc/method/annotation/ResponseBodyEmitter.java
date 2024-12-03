/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.MediaType
 *  org.springframework.http.server.ServerHttpResponse
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public class ResponseBodyEmitter {
    @Nullable
    private final Long timeout;
    @Nullable
    private Handler handler;
    private final Set<DataWithMediaType> earlySendAttempts = new LinkedHashSet<DataWithMediaType>(8);
    private boolean complete;
    @Nullable
    private Throwable failure;
    private boolean sendFailed;
    private final DefaultCallback timeoutCallback = new DefaultCallback();
    private final ErrorCallback errorCallback = new ErrorCallback();
    private final DefaultCallback completionCallback = new DefaultCallback();

    public ResponseBodyEmitter() {
        this.timeout = null;
    }

    public ResponseBodyEmitter(Long timeout) {
        this.timeout = timeout;
    }

    @Nullable
    public Long getTimeout() {
        return this.timeout;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    synchronized void initialize(Handler handler) throws IOException {
        this.handler = handler;
        try {
            for (DataWithMediaType sendAttempt : this.earlySendAttempts) {
                this.sendInternal(sendAttempt.getData(), sendAttempt.getMediaType());
            }
        }
        finally {
            this.earlySendAttempts.clear();
        }
        if (this.complete) {
            if (this.failure != null) {
                this.handler.completeWithError(this.failure);
            } else {
                this.handler.complete();
            }
        } else {
            this.handler.onTimeout(this.timeoutCallback);
            this.handler.onError(this.errorCallback);
            this.handler.onCompletion(this.completionCallback);
        }
    }

    synchronized void initializeWithError(Throwable ex) {
        this.complete = true;
        this.failure = ex;
        this.earlySendAttempts.clear();
        this.errorCallback.accept(ex);
    }

    protected void extendResponse(ServerHttpResponse outputMessage) {
    }

    public void send(Object object) throws IOException {
        this.send(object, null);
    }

    public synchronized void send(Object object, @Nullable MediaType mediaType) throws IOException {
        Assert.state((!this.complete ? 1 : 0) != 0, () -> "ResponseBodyEmitter has already completed" + (this.failure != null ? " with error: " + this.failure : ""));
        this.sendInternal(object, mediaType);
    }

    private void sendInternal(Object object, @Nullable MediaType mediaType) throws IOException {
        if (this.handler != null) {
            try {
                this.handler.send(object, mediaType);
            }
            catch (IOException ex) {
                this.sendFailed = true;
                throw ex;
            }
            catch (Throwable ex) {
                this.sendFailed = true;
                throw new IllegalStateException("Failed to send " + object, ex);
            }
        } else {
            this.earlySendAttempts.add(new DataWithMediaType(object, mediaType));
        }
    }

    public synchronized void complete() {
        if (this.sendFailed) {
            return;
        }
        this.complete = true;
        if (this.handler != null) {
            this.handler.complete();
        }
    }

    public synchronized void completeWithError(Throwable ex) {
        if (this.sendFailed) {
            return;
        }
        this.complete = true;
        this.failure = ex;
        if (this.handler != null) {
            this.handler.completeWithError(ex);
        }
    }

    public synchronized void onTimeout(Runnable callback) {
        this.timeoutCallback.setDelegate(callback);
    }

    public synchronized void onError(Consumer<Throwable> callback) {
        this.errorCallback.setDelegate(callback);
    }

    public synchronized void onCompletion(Runnable callback) {
        this.completionCallback.setDelegate(callback);
    }

    public String toString() {
        return "ResponseBodyEmitter@" + ObjectUtils.getIdentityHexString((Object)this);
    }

    private class ErrorCallback
    implements Consumer<Throwable> {
        @Nullable
        private Consumer<Throwable> delegate;

        private ErrorCallback() {
        }

        public void setDelegate(Consumer<Throwable> callback) {
            this.delegate = callback;
        }

        @Override
        public void accept(Throwable t) {
            ResponseBodyEmitter.this.complete = true;
            if (this.delegate != null) {
                this.delegate.accept(t);
            }
        }
    }

    private class DefaultCallback
    implements Runnable {
        @Nullable
        private Runnable delegate;

        private DefaultCallback() {
        }

        public void setDelegate(Runnable delegate) {
            this.delegate = delegate;
        }

        @Override
        public void run() {
            ResponseBodyEmitter.this.complete = true;
            if (this.delegate != null) {
                this.delegate.run();
            }
        }
    }

    public static class DataWithMediaType {
        private final Object data;
        @Nullable
        private final MediaType mediaType;

        public DataWithMediaType(Object data, @Nullable MediaType mediaType) {
            this.data = data;
            this.mediaType = mediaType;
        }

        public Object getData() {
            return this.data;
        }

        @Nullable
        public MediaType getMediaType() {
            return this.mediaType;
        }
    }

    static interface Handler {
        public void send(Object var1, @Nullable MediaType var2) throws IOException;

        public void complete();

        public void completeWithError(Throwable var1);

        public void onTimeout(Runnable var1);

        public void onError(Consumer<Throwable> var1);

        public void onCompletion(Runnable var1);
    }
}


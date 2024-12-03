/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.http.SdkHttpResponse
 */
package software.amazon.awssdk.core.internal.http;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import org.reactivestreams.Publisher;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.core.internal.http.TransformingAsyncResponseHandler;
import software.amazon.awssdk.http.SdkHttpResponse;

@SdkInternalApi
@ThreadSafe
public class IdempotentAsyncResponseHandler<T, R>
implements TransformingAsyncResponseHandler<T> {
    private final Supplier<R> newScopeSupplier;
    private final BiPredicate<R, R> newScopeInRangePredicate;
    private final TransformingAsyncResponseHandler<T> wrappedHandler;
    private final AtomicReference<R> cachedScope = new AtomicReference();
    private final AtomicReference<CompletableFuture<T>> cachedPreparedFuture = new AtomicReference();

    private IdempotentAsyncResponseHandler(TransformingAsyncResponseHandler<T> wrappedHandler, Supplier<R> newScopeSupplier, BiPredicate<R, R> newScopeInRangePredicate) {
        this.newScopeSupplier = newScopeSupplier;
        this.newScopeInRangePredicate = newScopeInRangePredicate;
        this.wrappedHandler = wrappedHandler;
    }

    public static <T, R> IdempotentAsyncResponseHandler<T, R> create(TransformingAsyncResponseHandler<T> wrappedHandler, Supplier<R> preparedScopeSupplier, BiPredicate<R, R> scopeInRangePredicate) {
        return new IdempotentAsyncResponseHandler<T, R>(wrappedHandler, preparedScopeSupplier, scopeInRangePredicate);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CompletableFuture<T> prepare() {
        if (this.cachedPreparedFuture.get() == null || !this.newScopeInRangePredicate.test(this.newScopeSupplier.get(), this.cachedScope.get())) {
            IdempotentAsyncResponseHandler idempotentAsyncResponseHandler = this;
            synchronized (idempotentAsyncResponseHandler) {
                R newScope = this.newScopeSupplier.get();
                if (this.cachedPreparedFuture.get() == null || !this.newScopeInRangePredicate.test(newScope, this.cachedScope.get())) {
                    this.cachedPreparedFuture.set(this.wrappedHandler.prepare());
                    this.cachedScope.set(newScope);
                }
            }
        }
        return this.cachedPreparedFuture.get();
    }

    public void onHeaders(SdkHttpResponse headers) {
        this.wrappedHandler.onHeaders(headers);
    }

    public void onStream(Publisher<ByteBuffer> stream) {
        this.wrappedHandler.onStream(stream);
    }

    public void onError(Throwable error) {
        this.wrappedHandler.onError(error);
    }
}


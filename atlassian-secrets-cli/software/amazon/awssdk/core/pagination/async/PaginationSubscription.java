/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.pagination.async;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.pagination.async.AsyncPageFetcher;

@SdkProtectedApi
public abstract class PaginationSubscription<ResponseT>
implements Subscription {
    protected AtomicLong outstandingRequests = new AtomicLong(0L);
    protected final Subscriber subscriber;
    protected final AsyncPageFetcher<ResponseT> nextPageFetcher;
    protected volatile ResponseT currentPage;
    private AtomicBoolean isTerminated = new AtomicBoolean(false);
    private AtomicBoolean isTaskRunning = new AtomicBoolean(false);

    protected PaginationSubscription(BuilderImpl builder) {
        this.subscriber = builder.subscriber;
        this.nextPageFetcher = builder.nextPageFetcher;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void request(long n) {
        if (this.isTerminated()) {
            return;
        }
        if (n <= 0L) {
            this.subscriber.onError(new IllegalArgumentException("Non-positive request signals are illegal"));
        }
        AtomicBoolean startTask = new AtomicBoolean(false);
        PaginationSubscription paginationSubscription = this;
        synchronized (paginationSubscription) {
            this.outstandingRequests.addAndGet(n);
            startTask.set(this.startTask());
        }
        if (startTask.get()) {
            this.handleRequests();
        }
    }

    protected abstract void handleRequests();

    @Override
    public void cancel() {
        this.cleanup();
    }

    protected boolean hasNextPage() {
        return this.currentPage == null || this.nextPageFetcher.hasNextPage(this.currentPage);
    }

    protected void completeSubscription() {
        if (!this.isTerminated()) {
            this.subscriber.onComplete();
            this.cleanup();
        }
    }

    private void terminate() {
        this.isTerminated.compareAndSet(false, true);
    }

    protected boolean isTerminated() {
        return this.isTerminated.get();
    }

    protected void stopTask() {
        this.isTaskRunning.set(false);
    }

    private synchronized boolean startTask() {
        return !this.isTerminated() && this.isTaskRunning.compareAndSet(false, true);
    }

    protected synchronized void cleanup() {
        this.terminate();
        this.stopTask();
    }

    protected static abstract class BuilderImpl<TypeToBuildT extends PaginationSubscription, BuilderT extends Builder>
    implements Builder<TypeToBuildT, BuilderT> {
        private Subscriber subscriber;
        private AsyncPageFetcher nextPageFetcher;

        protected BuilderImpl() {
        }

        @Override
        public BuilderT subscriber(Subscriber subscriber) {
            this.subscriber = subscriber;
            return (BuilderT)this;
        }

        @Override
        public BuilderT nextPageFetcher(AsyncPageFetcher nextPageFetcher) {
            this.nextPageFetcher = nextPageFetcher;
            return (BuilderT)this;
        }
    }

    public static interface Builder<TypeToBuildT extends PaginationSubscription, BuilderT extends Builder> {
        public BuilderT subscriber(Subscriber var1);

        public BuilderT nextPageFetcher(AsyncPageFetcher var1);

        public TypeToBuildT build();
    }
}


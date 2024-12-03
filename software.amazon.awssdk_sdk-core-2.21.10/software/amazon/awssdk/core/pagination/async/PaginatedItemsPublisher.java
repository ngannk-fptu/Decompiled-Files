/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.core.pagination.async;

import java.util.Iterator;
import java.util.function.Function;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.core.internal.pagination.async.ItemsSubscription;
import software.amazon.awssdk.core.pagination.async.AsyncPageFetcher;
import software.amazon.awssdk.core.pagination.async.EmptySubscription;

@SdkProtectedApi
public final class PaginatedItemsPublisher<ResponseT, ItemT>
implements SdkPublisher<ItemT> {
    private final AsyncPageFetcher<ResponseT> nextPageFetcher;
    private final Function<ResponseT, Iterator<ItemT>> getIteratorFunction;
    private final boolean isLastPage;

    private PaginatedItemsPublisher(BuilderImpl builder) {
        this.nextPageFetcher = builder.nextPageFetcher;
        this.getIteratorFunction = builder.iteratorFunction;
        this.isLastPage = builder.isLastPage;
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public void subscribe(Subscriber<? super ItemT> subscriber) {
        subscriber.onSubscribe((Subscription)(this.isLastPage ? new EmptySubscription(subscriber) : ((ItemsSubscription.Builder)((ItemsSubscription.Builder)ItemsSubscription.builder().subscriber(subscriber)).nextPageFetcher(this.nextPageFetcher)).iteratorFunction(this.getIteratorFunction).build()));
    }

    private static final class BuilderImpl
    implements Builder {
        private AsyncPageFetcher nextPageFetcher;
        private Function iteratorFunction;
        private boolean isLastPage;

        private BuilderImpl() {
        }

        @Override
        public Builder nextPageFetcher(AsyncPageFetcher nextPageFetcher) {
            this.nextPageFetcher = nextPageFetcher;
            return this;
        }

        @Override
        public Builder iteratorFunction(Function iteratorFunction) {
            this.iteratorFunction = iteratorFunction;
            return this;
        }

        @Override
        public Builder isLastPage(boolean isLastPage) {
            this.isLastPage = isLastPage;
            return this;
        }

        @Override
        public PaginatedItemsPublisher build() {
            return new PaginatedItemsPublisher(this);
        }
    }

    public static interface Builder {
        public Builder nextPageFetcher(AsyncPageFetcher var1);

        public Builder iteratorFunction(Function var1);

        public Builder isLastPage(boolean var1);

        public PaginatedItemsPublisher build();
    }
}


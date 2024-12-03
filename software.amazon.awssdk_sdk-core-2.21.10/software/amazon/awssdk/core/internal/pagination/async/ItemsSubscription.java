/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.core.internal.pagination.async;

import java.util.Iterator;
import java.util.function.Function;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.pagination.async.PaginationSubscription;

@SdkInternalApi
public final class ItemsSubscription<ResponseT, ItemT>
extends PaginationSubscription<ResponseT> {
    private final Function<ResponseT, Iterator<ItemT>> getIteratorFunction;
    private volatile Iterator<ItemT> singlePageItemsIterator;

    private ItemsSubscription(BuilderImpl builder) {
        super(builder);
        this.getIteratorFunction = builder.iteratorFunction;
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void handleRequests() {
        if (!this.hasMoreItems() && !this.hasNextPage()) {
            this.completeSubscription();
            return;
        }
        ItemsSubscription itemsSubscription = this;
        synchronized (itemsSubscription) {
            if (this.outstandingRequests.get() <= 0L) {
                this.stopTask();
                return;
            }
        }
        if (!this.isTerminated()) {
            if (this.currentPage == null || !this.hasMoreItems() && this.hasNextPage()) {
                this.fetchNextPage();
            } else if (this.hasMoreItems()) {
                this.sendNextElement();
            } else {
                throw new IllegalStateException("Execution should have not reached here");
            }
        }
    }

    private void fetchNextPage() {
        this.nextPageFetcher.nextPage(this.currentPage).whenComplete((response, error) -> {
            if (response != null) {
                this.currentPage = response;
                this.singlePageItemsIterator = this.getIteratorFunction.apply(response);
                this.sendNextElement();
            }
            if (error != null) {
                this.subscriber.onError(error);
                this.cleanup();
            }
        });
    }

    private void sendNextElement() {
        if (this.singlePageItemsIterator.hasNext()) {
            this.subscriber.onNext(this.singlePageItemsIterator.next());
            this.outstandingRequests.getAndDecrement();
        }
        this.handleRequests();
    }

    private boolean hasMoreItems() {
        return this.singlePageItemsIterator != null && this.singlePageItemsIterator.hasNext();
    }

    private static final class BuilderImpl
    extends PaginationSubscription.BuilderImpl<ItemsSubscription, Builder>
    implements Builder {
        private Function iteratorFunction;

        private BuilderImpl() {
        }

        @Override
        public Builder iteratorFunction(Function iteratorFunction) {
            this.iteratorFunction = iteratorFunction;
            return this;
        }

        @Override
        public ItemsSubscription build() {
            return new ItemsSubscription(this);
        }
    }

    public static interface Builder
    extends PaginationSubscription.Builder<ItemsSubscription, Builder> {
        public Builder iteratorFunction(Function var1);

        @Override
        public ItemsSubscription build();
    }
}


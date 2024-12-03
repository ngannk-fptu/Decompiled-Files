/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.pagination.async;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.pagination.async.PaginationSubscription;

@SdkProtectedApi
public final class ResponsesSubscription<ResponseT>
extends PaginationSubscription<ResponseT> {
    private ResponsesSubscription(BuilderImpl builder) {
        super(builder);
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void handleRequests() {
        if (!this.hasNextPage()) {
            this.completeSubscription();
            return;
        }
        ResponsesSubscription responsesSubscription = this;
        synchronized (responsesSubscription) {
            if (this.outstandingRequests.get() <= 0L) {
                this.stopTask();
                return;
            }
        }
        if (!this.isTerminated()) {
            this.outstandingRequests.getAndDecrement();
            this.nextPageFetcher.nextPage(this.currentPage).whenComplete((response, error) -> {
                if (response != null) {
                    this.currentPage = response;
                    this.subscriber.onNext(response);
                    this.handleRequests();
                }
                if (error != null) {
                    this.subscriber.onError((Throwable)error);
                    this.cleanup();
                }
            });
        }
    }

    private static final class BuilderImpl
    extends PaginationSubscription.BuilderImpl<ResponsesSubscription, Builder>
    implements Builder {
        private BuilderImpl() {
        }

        @Override
        public ResponsesSubscription build() {
            return new ResponsesSubscription(this);
        }
    }

    public static interface Builder
    extends PaginationSubscription.Builder<ResponsesSubscription, Builder> {
        @Override
        public ResponsesSubscription build();
    }
}


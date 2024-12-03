/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.core.async.SdkPublisher
 *  software.amazon.awssdk.core.pagination.async.AsyncPageFetcher
 *  software.amazon.awssdk.core.pagination.async.PaginatedItemsPublisher
 *  software.amazon.awssdk.core.pagination.async.ResponsesSubscription
 *  software.amazon.awssdk.core.pagination.async.ResponsesSubscription$Builder
 */
package software.amazon.awssdk.services.s3.paginators;

import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.core.pagination.async.AsyncPageFetcher;
import software.amazon.awssdk.core.pagination.async.PaginatedItemsPublisher;
import software.amazon.awssdk.core.pagination.async.ResponsesSubscription;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.internal.UserAgentUtils;
import software.amazon.awssdk.services.s3.model.ListPartsRequest;
import software.amazon.awssdk.services.s3.model.ListPartsResponse;
import software.amazon.awssdk.services.s3.model.Part;

public class ListPartsPublisher
implements SdkPublisher<ListPartsResponse> {
    private final S3AsyncClient client;
    private final ListPartsRequest firstRequest;
    private final AsyncPageFetcher nextPageFetcher;
    private boolean isLastPage;

    public ListPartsPublisher(S3AsyncClient client, ListPartsRequest firstRequest) {
        this(client, firstRequest, false);
    }

    private ListPartsPublisher(S3AsyncClient client, ListPartsRequest firstRequest, boolean isLastPage) {
        this.client = client;
        this.firstRequest = UserAgentUtils.applyPaginatorUserAgent(firstRequest);
        this.isLastPage = isLastPage;
        this.nextPageFetcher = new ListPartsResponseFetcher();
    }

    public void subscribe(Subscriber<? super ListPartsResponse> subscriber) {
        subscriber.onSubscribe((Subscription)((ResponsesSubscription.Builder)((ResponsesSubscription.Builder)ResponsesSubscription.builder().subscriber(subscriber)).nextPageFetcher(this.nextPageFetcher)).build());
    }

    public final SdkPublisher<Part> parts() {
        Function<ListPartsResponse, Iterator> getIterator = response -> {
            if (response != null && response.parts() != null) {
                return response.parts().iterator();
            }
            return Collections.emptyIterator();
        };
        return PaginatedItemsPublisher.builder().nextPageFetcher((AsyncPageFetcher)new ListPartsResponseFetcher()).iteratorFunction(getIterator).isLastPage(this.isLastPage).build();
    }

    private class ListPartsResponseFetcher
    implements AsyncPageFetcher<ListPartsResponse> {
        private ListPartsResponseFetcher() {
        }

        public boolean hasNextPage(ListPartsResponse previousPage) {
            return previousPage.isTruncated();
        }

        public CompletableFuture<ListPartsResponse> nextPage(ListPartsResponse previousPage) {
            if (previousPage == null) {
                return ListPartsPublisher.this.client.listParts(ListPartsPublisher.this.firstRequest);
            }
            return ListPartsPublisher.this.client.listParts((ListPartsRequest)((Object)ListPartsPublisher.this.firstRequest.toBuilder().partNumberMarker(previousPage.nextPartNumberMarker()).build()));
        }
    }
}


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
import software.amazon.awssdk.services.s3.model.CommonPrefix;
import software.amazon.awssdk.services.s3.model.ListMultipartUploadsRequest;
import software.amazon.awssdk.services.s3.model.ListMultipartUploadsResponse;
import software.amazon.awssdk.services.s3.model.MultipartUpload;

public class ListMultipartUploadsPublisher
implements SdkPublisher<ListMultipartUploadsResponse> {
    private final S3AsyncClient client;
    private final ListMultipartUploadsRequest firstRequest;
    private final AsyncPageFetcher nextPageFetcher;
    private boolean isLastPage;

    public ListMultipartUploadsPublisher(S3AsyncClient client, ListMultipartUploadsRequest firstRequest) {
        this(client, firstRequest, false);
    }

    private ListMultipartUploadsPublisher(S3AsyncClient client, ListMultipartUploadsRequest firstRequest, boolean isLastPage) {
        this.client = client;
        this.firstRequest = UserAgentUtils.applyPaginatorUserAgent(firstRequest);
        this.isLastPage = isLastPage;
        this.nextPageFetcher = new ListMultipartUploadsResponseFetcher();
    }

    public void subscribe(Subscriber<? super ListMultipartUploadsResponse> subscriber) {
        subscriber.onSubscribe((Subscription)((ResponsesSubscription.Builder)((ResponsesSubscription.Builder)ResponsesSubscription.builder().subscriber(subscriber)).nextPageFetcher(this.nextPageFetcher)).build());
    }

    public final SdkPublisher<MultipartUpload> uploads() {
        Function<ListMultipartUploadsResponse, Iterator> getIterator = response -> {
            if (response != null && response.uploads() != null) {
                return response.uploads().iterator();
            }
            return Collections.emptyIterator();
        };
        return PaginatedItemsPublisher.builder().nextPageFetcher((AsyncPageFetcher)new ListMultipartUploadsResponseFetcher()).iteratorFunction(getIterator).isLastPage(this.isLastPage).build();
    }

    public final SdkPublisher<CommonPrefix> commonPrefixes() {
        Function<ListMultipartUploadsResponse, Iterator> getIterator = response -> {
            if (response != null && response.commonPrefixes() != null) {
                return response.commonPrefixes().iterator();
            }
            return Collections.emptyIterator();
        };
        return PaginatedItemsPublisher.builder().nextPageFetcher((AsyncPageFetcher)new ListMultipartUploadsResponseFetcher()).iteratorFunction(getIterator).isLastPage(this.isLastPage).build();
    }

    private class ListMultipartUploadsResponseFetcher
    implements AsyncPageFetcher<ListMultipartUploadsResponse> {
        private ListMultipartUploadsResponseFetcher() {
        }

        public boolean hasNextPage(ListMultipartUploadsResponse previousPage) {
            return previousPage.isTruncated();
        }

        public CompletableFuture<ListMultipartUploadsResponse> nextPage(ListMultipartUploadsResponse previousPage) {
            if (previousPage == null) {
                return ListMultipartUploadsPublisher.this.client.listMultipartUploads(ListMultipartUploadsPublisher.this.firstRequest);
            }
            return ListMultipartUploadsPublisher.this.client.listMultipartUploads((ListMultipartUploadsRequest)((Object)ListMultipartUploadsPublisher.this.firstRequest.toBuilder().keyMarker(previousPage.nextKeyMarker()).uploadIdMarker(previousPage.nextUploadIdMarker()).build()));
        }
    }
}


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
import software.amazon.awssdk.services.s3.model.DeleteMarkerEntry;
import software.amazon.awssdk.services.s3.model.ListObjectVersionsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectVersionsResponse;
import software.amazon.awssdk.services.s3.model.ObjectVersion;

public class ListObjectVersionsPublisher
implements SdkPublisher<ListObjectVersionsResponse> {
    private final S3AsyncClient client;
    private final ListObjectVersionsRequest firstRequest;
    private final AsyncPageFetcher nextPageFetcher;
    private boolean isLastPage;

    public ListObjectVersionsPublisher(S3AsyncClient client, ListObjectVersionsRequest firstRequest) {
        this(client, firstRequest, false);
    }

    private ListObjectVersionsPublisher(S3AsyncClient client, ListObjectVersionsRequest firstRequest, boolean isLastPage) {
        this.client = client;
        this.firstRequest = UserAgentUtils.applyPaginatorUserAgent(firstRequest);
        this.isLastPage = isLastPage;
        this.nextPageFetcher = new ListObjectVersionsResponseFetcher();
    }

    public void subscribe(Subscriber<? super ListObjectVersionsResponse> subscriber) {
        subscriber.onSubscribe((Subscription)((ResponsesSubscription.Builder)((ResponsesSubscription.Builder)ResponsesSubscription.builder().subscriber(subscriber)).nextPageFetcher(this.nextPageFetcher)).build());
    }

    public final SdkPublisher<ObjectVersion> versions() {
        Function<ListObjectVersionsResponse, Iterator> getIterator = response -> {
            if (response != null && response.versions() != null) {
                return response.versions().iterator();
            }
            return Collections.emptyIterator();
        };
        return PaginatedItemsPublisher.builder().nextPageFetcher((AsyncPageFetcher)new ListObjectVersionsResponseFetcher()).iteratorFunction(getIterator).isLastPage(this.isLastPage).build();
    }

    public final SdkPublisher<DeleteMarkerEntry> deleteMarkers() {
        Function<ListObjectVersionsResponse, Iterator> getIterator = response -> {
            if (response != null && response.deleteMarkers() != null) {
                return response.deleteMarkers().iterator();
            }
            return Collections.emptyIterator();
        };
        return PaginatedItemsPublisher.builder().nextPageFetcher((AsyncPageFetcher)new ListObjectVersionsResponseFetcher()).iteratorFunction(getIterator).isLastPage(this.isLastPage).build();
    }

    public final SdkPublisher<CommonPrefix> commonPrefixes() {
        Function<ListObjectVersionsResponse, Iterator> getIterator = response -> {
            if (response != null && response.commonPrefixes() != null) {
                return response.commonPrefixes().iterator();
            }
            return Collections.emptyIterator();
        };
        return PaginatedItemsPublisher.builder().nextPageFetcher((AsyncPageFetcher)new ListObjectVersionsResponseFetcher()).iteratorFunction(getIterator).isLastPage(this.isLastPage).build();
    }

    private class ListObjectVersionsResponseFetcher
    implements AsyncPageFetcher<ListObjectVersionsResponse> {
        private ListObjectVersionsResponseFetcher() {
        }

        public boolean hasNextPage(ListObjectVersionsResponse previousPage) {
            return previousPage.isTruncated();
        }

        public CompletableFuture<ListObjectVersionsResponse> nextPage(ListObjectVersionsResponse previousPage) {
            if (previousPage == null) {
                return ListObjectVersionsPublisher.this.client.listObjectVersions(ListObjectVersionsPublisher.this.firstRequest);
            }
            return ListObjectVersionsPublisher.this.client.listObjectVersions((ListObjectVersionsRequest)((Object)ListObjectVersionsPublisher.this.firstRequest.toBuilder().keyMarker(previousPage.nextKeyMarker()).versionIdMarker(previousPage.nextVersionIdMarker()).build()));
        }
    }
}


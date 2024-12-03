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
 *  software.amazon.awssdk.core.util.PaginatorUtils
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
import software.amazon.awssdk.core.util.PaginatorUtils;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.internal.UserAgentUtils;
import software.amazon.awssdk.services.s3.model.CommonPrefix;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

public class ListObjectsV2Publisher
implements SdkPublisher<ListObjectsV2Response> {
    private final S3AsyncClient client;
    private final ListObjectsV2Request firstRequest;
    private final AsyncPageFetcher nextPageFetcher;
    private boolean isLastPage;

    public ListObjectsV2Publisher(S3AsyncClient client, ListObjectsV2Request firstRequest) {
        this(client, firstRequest, false);
    }

    private ListObjectsV2Publisher(S3AsyncClient client, ListObjectsV2Request firstRequest, boolean isLastPage) {
        this.client = client;
        this.firstRequest = UserAgentUtils.applyPaginatorUserAgent(firstRequest);
        this.isLastPage = isLastPage;
        this.nextPageFetcher = new ListObjectsV2ResponseFetcher();
    }

    public void subscribe(Subscriber<? super ListObjectsV2Response> subscriber) {
        subscriber.onSubscribe((Subscription)((ResponsesSubscription.Builder)((ResponsesSubscription.Builder)ResponsesSubscription.builder().subscriber(subscriber)).nextPageFetcher(this.nextPageFetcher)).build());
    }

    public final SdkPublisher<S3Object> contents() {
        Function<ListObjectsV2Response, Iterator> getIterator = response -> {
            if (response != null && response.contents() != null) {
                return response.contents().iterator();
            }
            return Collections.emptyIterator();
        };
        return PaginatedItemsPublisher.builder().nextPageFetcher((AsyncPageFetcher)new ListObjectsV2ResponseFetcher()).iteratorFunction(getIterator).isLastPage(this.isLastPage).build();
    }

    public final SdkPublisher<CommonPrefix> commonPrefixes() {
        Function<ListObjectsV2Response, Iterator> getIterator = response -> {
            if (response != null && response.commonPrefixes() != null) {
                return response.commonPrefixes().iterator();
            }
            return Collections.emptyIterator();
        };
        return PaginatedItemsPublisher.builder().nextPageFetcher((AsyncPageFetcher)new ListObjectsV2ResponseFetcher()).iteratorFunction(getIterator).isLastPage(this.isLastPage).build();
    }

    private class ListObjectsV2ResponseFetcher
    implements AsyncPageFetcher<ListObjectsV2Response> {
        private ListObjectsV2ResponseFetcher() {
        }

        public boolean hasNextPage(ListObjectsV2Response previousPage) {
            return PaginatorUtils.isOutputTokenAvailable((Object)previousPage.nextContinuationToken());
        }

        public CompletableFuture<ListObjectsV2Response> nextPage(ListObjectsV2Response previousPage) {
            if (previousPage == null) {
                return ListObjectsV2Publisher.this.client.listObjectsV2(ListObjectsV2Publisher.this.firstRequest);
            }
            return ListObjectsV2Publisher.this.client.listObjectsV2((ListObjectsV2Request)((Object)ListObjectsV2Publisher.this.firstRequest.toBuilder().continuationToken(previousPage.nextContinuationToken()).build()));
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.pagination.sync.PaginatedItemsIterable
 *  software.amazon.awssdk.core.pagination.sync.PaginatedResponsesIterator
 *  software.amazon.awssdk.core.pagination.sync.SdkIterable
 *  software.amazon.awssdk.core.pagination.sync.SyncPageFetcher
 *  software.amazon.awssdk.core.util.PaginatorUtils
 */
package software.amazon.awssdk.services.s3.paginators;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.Function;
import software.amazon.awssdk.core.pagination.sync.PaginatedItemsIterable;
import software.amazon.awssdk.core.pagination.sync.PaginatedResponsesIterator;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.core.pagination.sync.SyncPageFetcher;
import software.amazon.awssdk.core.util.PaginatorUtils;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.internal.UserAgentUtils;
import software.amazon.awssdk.services.s3.model.CommonPrefix;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

public class ListObjectsV2Iterable
implements SdkIterable<ListObjectsV2Response> {
    private final S3Client client;
    private final ListObjectsV2Request firstRequest;
    private final SyncPageFetcher nextPageFetcher;

    public ListObjectsV2Iterable(S3Client client, ListObjectsV2Request firstRequest) {
        this.client = client;
        this.firstRequest = UserAgentUtils.applyPaginatorUserAgent(firstRequest);
        this.nextPageFetcher = new ListObjectsV2ResponseFetcher();
    }

    public Iterator<ListObjectsV2Response> iterator() {
        return PaginatedResponsesIterator.builder().nextPageFetcher(this.nextPageFetcher).build();
    }

    public final SdkIterable<S3Object> contents() {
        Function<ListObjectsV2Response, Iterator> getIterator = response -> {
            if (response != null && response.contents() != null) {
                return response.contents().iterator();
            }
            return Collections.emptyIterator();
        };
        return PaginatedItemsIterable.builder().pagesIterable((SdkIterable)this).itemIteratorFunction(getIterator).build();
    }

    public final SdkIterable<CommonPrefix> commonPrefixes() {
        Function<ListObjectsV2Response, Iterator> getIterator = response -> {
            if (response != null && response.commonPrefixes() != null) {
                return response.commonPrefixes().iterator();
            }
            return Collections.emptyIterator();
        };
        return PaginatedItemsIterable.builder().pagesIterable((SdkIterable)this).itemIteratorFunction(getIterator).build();
    }

    private class ListObjectsV2ResponseFetcher
    implements SyncPageFetcher<ListObjectsV2Response> {
        private ListObjectsV2ResponseFetcher() {
        }

        public boolean hasNextPage(ListObjectsV2Response previousPage) {
            return PaginatorUtils.isOutputTokenAvailable((Object)previousPage.nextContinuationToken());
        }

        public ListObjectsV2Response nextPage(ListObjectsV2Response previousPage) {
            if (previousPage == null) {
                return ListObjectsV2Iterable.this.client.listObjectsV2(ListObjectsV2Iterable.this.firstRequest);
            }
            return ListObjectsV2Iterable.this.client.listObjectsV2((ListObjectsV2Request)((Object)ListObjectsV2Iterable.this.firstRequest.toBuilder().continuationToken(previousPage.nextContinuationToken()).build()));
        }
    }
}


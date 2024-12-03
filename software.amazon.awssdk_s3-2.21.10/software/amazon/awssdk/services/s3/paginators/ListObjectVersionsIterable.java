/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.pagination.sync.PaginatedItemsIterable
 *  software.amazon.awssdk.core.pagination.sync.PaginatedResponsesIterator
 *  software.amazon.awssdk.core.pagination.sync.SdkIterable
 *  software.amazon.awssdk.core.pagination.sync.SyncPageFetcher
 */
package software.amazon.awssdk.services.s3.paginators;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.Function;
import software.amazon.awssdk.core.pagination.sync.PaginatedItemsIterable;
import software.amazon.awssdk.core.pagination.sync.PaginatedResponsesIterator;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.core.pagination.sync.SyncPageFetcher;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.internal.UserAgentUtils;
import software.amazon.awssdk.services.s3.model.CommonPrefix;
import software.amazon.awssdk.services.s3.model.DeleteMarkerEntry;
import software.amazon.awssdk.services.s3.model.ListObjectVersionsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectVersionsResponse;
import software.amazon.awssdk.services.s3.model.ObjectVersion;

public class ListObjectVersionsIterable
implements SdkIterable<ListObjectVersionsResponse> {
    private final S3Client client;
    private final ListObjectVersionsRequest firstRequest;
    private final SyncPageFetcher nextPageFetcher;

    public ListObjectVersionsIterable(S3Client client, ListObjectVersionsRequest firstRequest) {
        this.client = client;
        this.firstRequest = UserAgentUtils.applyPaginatorUserAgent(firstRequest);
        this.nextPageFetcher = new ListObjectVersionsResponseFetcher();
    }

    public Iterator<ListObjectVersionsResponse> iterator() {
        return PaginatedResponsesIterator.builder().nextPageFetcher(this.nextPageFetcher).build();
    }

    public final SdkIterable<ObjectVersion> versions() {
        Function<ListObjectVersionsResponse, Iterator> getIterator = response -> {
            if (response != null && response.versions() != null) {
                return response.versions().iterator();
            }
            return Collections.emptyIterator();
        };
        return PaginatedItemsIterable.builder().pagesIterable((SdkIterable)this).itemIteratorFunction(getIterator).build();
    }

    public final SdkIterable<DeleteMarkerEntry> deleteMarkers() {
        Function<ListObjectVersionsResponse, Iterator> getIterator = response -> {
            if (response != null && response.deleteMarkers() != null) {
                return response.deleteMarkers().iterator();
            }
            return Collections.emptyIterator();
        };
        return PaginatedItemsIterable.builder().pagesIterable((SdkIterable)this).itemIteratorFunction(getIterator).build();
    }

    public final SdkIterable<CommonPrefix> commonPrefixes() {
        Function<ListObjectVersionsResponse, Iterator> getIterator = response -> {
            if (response != null && response.commonPrefixes() != null) {
                return response.commonPrefixes().iterator();
            }
            return Collections.emptyIterator();
        };
        return PaginatedItemsIterable.builder().pagesIterable((SdkIterable)this).itemIteratorFunction(getIterator).build();
    }

    private class ListObjectVersionsResponseFetcher
    implements SyncPageFetcher<ListObjectVersionsResponse> {
        private ListObjectVersionsResponseFetcher() {
        }

        public boolean hasNextPage(ListObjectVersionsResponse previousPage) {
            return previousPage.isTruncated();
        }

        public ListObjectVersionsResponse nextPage(ListObjectVersionsResponse previousPage) {
            if (previousPage == null) {
                return ListObjectVersionsIterable.this.client.listObjectVersions(ListObjectVersionsIterable.this.firstRequest);
            }
            return ListObjectVersionsIterable.this.client.listObjectVersions((ListObjectVersionsRequest)((Object)ListObjectVersionsIterable.this.firstRequest.toBuilder().keyMarker(previousPage.nextKeyMarker()).versionIdMarker(previousPage.nextVersionIdMarker()).build()));
        }
    }
}


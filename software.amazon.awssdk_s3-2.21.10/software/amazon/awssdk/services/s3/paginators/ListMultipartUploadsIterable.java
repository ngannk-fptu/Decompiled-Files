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
import software.amazon.awssdk.services.s3.model.ListMultipartUploadsRequest;
import software.amazon.awssdk.services.s3.model.ListMultipartUploadsResponse;
import software.amazon.awssdk.services.s3.model.MultipartUpload;

public class ListMultipartUploadsIterable
implements SdkIterable<ListMultipartUploadsResponse> {
    private final S3Client client;
    private final ListMultipartUploadsRequest firstRequest;
    private final SyncPageFetcher nextPageFetcher;

    public ListMultipartUploadsIterable(S3Client client, ListMultipartUploadsRequest firstRequest) {
        this.client = client;
        this.firstRequest = UserAgentUtils.applyPaginatorUserAgent(firstRequest);
        this.nextPageFetcher = new ListMultipartUploadsResponseFetcher();
    }

    public Iterator<ListMultipartUploadsResponse> iterator() {
        return PaginatedResponsesIterator.builder().nextPageFetcher(this.nextPageFetcher).build();
    }

    public final SdkIterable<MultipartUpload> uploads() {
        Function<ListMultipartUploadsResponse, Iterator> getIterator = response -> {
            if (response != null && response.uploads() != null) {
                return response.uploads().iterator();
            }
            return Collections.emptyIterator();
        };
        return PaginatedItemsIterable.builder().pagesIterable((SdkIterable)this).itemIteratorFunction(getIterator).build();
    }

    public final SdkIterable<CommonPrefix> commonPrefixes() {
        Function<ListMultipartUploadsResponse, Iterator> getIterator = response -> {
            if (response != null && response.commonPrefixes() != null) {
                return response.commonPrefixes().iterator();
            }
            return Collections.emptyIterator();
        };
        return PaginatedItemsIterable.builder().pagesIterable((SdkIterable)this).itemIteratorFunction(getIterator).build();
    }

    private class ListMultipartUploadsResponseFetcher
    implements SyncPageFetcher<ListMultipartUploadsResponse> {
        private ListMultipartUploadsResponseFetcher() {
        }

        public boolean hasNextPage(ListMultipartUploadsResponse previousPage) {
            return previousPage.isTruncated();
        }

        public ListMultipartUploadsResponse nextPage(ListMultipartUploadsResponse previousPage) {
            if (previousPage == null) {
                return ListMultipartUploadsIterable.this.client.listMultipartUploads(ListMultipartUploadsIterable.this.firstRequest);
            }
            return ListMultipartUploadsIterable.this.client.listMultipartUploads((ListMultipartUploadsRequest)((Object)ListMultipartUploadsIterable.this.firstRequest.toBuilder().keyMarker(previousPage.nextKeyMarker()).uploadIdMarker(previousPage.nextUploadIdMarker()).build()));
        }
    }
}


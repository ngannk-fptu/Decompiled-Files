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
import software.amazon.awssdk.services.s3.model.ListPartsRequest;
import software.amazon.awssdk.services.s3.model.ListPartsResponse;
import software.amazon.awssdk.services.s3.model.Part;

public class ListPartsIterable
implements SdkIterable<ListPartsResponse> {
    private final S3Client client;
    private final ListPartsRequest firstRequest;
    private final SyncPageFetcher nextPageFetcher;

    public ListPartsIterable(S3Client client, ListPartsRequest firstRequest) {
        this.client = client;
        this.firstRequest = UserAgentUtils.applyPaginatorUserAgent(firstRequest);
        this.nextPageFetcher = new ListPartsResponseFetcher();
    }

    public Iterator<ListPartsResponse> iterator() {
        return PaginatedResponsesIterator.builder().nextPageFetcher(this.nextPageFetcher).build();
    }

    public final SdkIterable<Part> parts() {
        Function<ListPartsResponse, Iterator> getIterator = response -> {
            if (response != null && response.parts() != null) {
                return response.parts().iterator();
            }
            return Collections.emptyIterator();
        };
        return PaginatedItemsIterable.builder().pagesIterable((SdkIterable)this).itemIteratorFunction(getIterator).build();
    }

    private class ListPartsResponseFetcher
    implements SyncPageFetcher<ListPartsResponse> {
        private ListPartsResponseFetcher() {
        }

        public boolean hasNextPage(ListPartsResponse previousPage) {
            return previousPage.isTruncated();
        }

        public ListPartsResponse nextPage(ListPartsResponse previousPage) {
            if (previousPage == null) {
                return ListPartsIterable.this.client.listParts(ListPartsIterable.this.firstRequest);
            }
            return ListPartsIterable.this.client.listParts((ListPartsRequest)((Object)ListPartsIterable.this.firstRequest.toBuilder().partNumberMarker(previousPage.nextPartNumberMarker()).build()));
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.core.pagination.sync;

import java.util.Iterator;
import java.util.NoSuchElementException;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.pagination.sync.SyncPageFetcher;

@SdkProtectedApi
public final class PaginatedResponsesIterator<ResponseT>
implements Iterator<ResponseT> {
    private final SyncPageFetcher<ResponseT> nextPageFetcher;
    private ResponseT oldResponse;

    private PaginatedResponsesIterator(BuilderImpl builder) {
        this.nextPageFetcher = builder.nextPageFetcher;
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    @Override
    public boolean hasNext() {
        return this.oldResponse == null || this.nextPageFetcher.hasNextPage(this.oldResponse);
    }

    @Override
    public ResponseT next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException("No more pages left");
        }
        this.oldResponse = this.nextPageFetcher.nextPage(this.oldResponse);
        return this.oldResponse;
    }

    private static final class BuilderImpl
    implements Builder {
        private SyncPageFetcher nextPageFetcher;

        protected BuilderImpl() {
        }

        @Override
        public Builder nextPageFetcher(SyncPageFetcher nextPageFetcher) {
            this.nextPageFetcher = nextPageFetcher;
            return this;
        }

        @Override
        public PaginatedResponsesIterator build() {
            return new PaginatedResponsesIterator(this);
        }
    }

    public static interface Builder {
        public Builder nextPageFetcher(SyncPageFetcher var1);

        public PaginatedResponsesIterator build();
    }
}


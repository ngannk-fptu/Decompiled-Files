/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.core.pagination.sync;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;

@SdkProtectedApi
public final class PaginatedItemsIterable<ResponseT, ItemT>
implements SdkIterable<ItemT> {
    private final SdkIterable<ResponseT> pagesIterable;
    private final Function<ResponseT, Iterator<ItemT>> getItemIterator;

    private PaginatedItemsIterable(BuilderImpl<ResponseT, ItemT> builder) {
        this.pagesIterable = ((BuilderImpl)builder).pagesIterable;
        this.getItemIterator = ((BuilderImpl)builder).itemIteratorFunction;
    }

    public static <R, T> Builder<R, T> builder() {
        return new BuilderImpl();
    }

    @Override
    public Iterator<ItemT> iterator() {
        return new ItemsIterator(this.pagesIterable.iterator());
    }

    private static final class BuilderImpl<ResponseT, ItemT>
    implements Builder<ResponseT, ItemT> {
        private SdkIterable<ResponseT> pagesIterable;
        private Function<ResponseT, Iterator<ItemT>> itemIteratorFunction;

        private BuilderImpl() {
        }

        @Override
        public Builder<ResponseT, ItemT> pagesIterable(SdkIterable<ResponseT> pagesIterable) {
            this.pagesIterable = pagesIterable;
            return this;
        }

        @Override
        public Builder<ResponseT, ItemT> itemIteratorFunction(Function<ResponseT, Iterator<ItemT>> itemIteratorFunction) {
            this.itemIteratorFunction = itemIteratorFunction;
            return this;
        }

        @Override
        public PaginatedItemsIterable<ResponseT, ItemT> build() {
            return new PaginatedItemsIterable(this);
        }
    }

    public static interface Builder<ResponseT, ItemT> {
        public Builder<ResponseT, ItemT> pagesIterable(SdkIterable<ResponseT> var1);

        public Builder<ResponseT, ItemT> itemIteratorFunction(Function<ResponseT, Iterator<ItemT>> var1);

        public PaginatedItemsIterable<ResponseT, ItemT> build();
    }

    private class ItemsIterator
    implements Iterator<ItemT> {
        private final Iterator<ResponseT> pagesIterator;
        private Iterator<ItemT> singlePageItemsIterator;

        ItemsIterator(Iterator<ResponseT> pagesIterator) {
            this.pagesIterator = pagesIterator;
            this.singlePageItemsIterator = pagesIterator.hasNext() ? (Iterator)PaginatedItemsIterable.this.getItemIterator.apply(pagesIterator.next()) : Collections.emptyIterator();
        }

        @Override
        public boolean hasNext() {
            while (!this.hasMoreItems() && this.pagesIterator.hasNext()) {
                this.singlePageItemsIterator = (Iterator)PaginatedItemsIterable.this.getItemIterator.apply(this.pagesIterator.next());
            }
            return this.hasMoreItems();
        }

        @Override
        public ItemT next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException("No more elements left");
            }
            return this.singlePageItemsIterator.next();
        }

        private boolean hasMoreItems() {
            return this.singlePageItemsIterator.hasNext();
        }
    }
}


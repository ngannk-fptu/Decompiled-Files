/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.api.model.pagination.Cursor
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.model.pagination.PaginationBatch
 *  com.atlassian.confluence.api.model.pagination.SkipDiscardLimitedRequest
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.AbstractIterator
 *  com.google.common.collect.Iterables
 *  javax.persistence.PersistenceException
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.event.spi.EventSource
 *  org.hibernate.proxy.HibernateProxy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.PlatformTransactionManager
 */
package com.atlassian.confluence.api.impl.pagination;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.api.impl.ReadOnlyAndReadWriteTransactionConversionTemplate;
import com.atlassian.confluence.api.impl.pagination.Paginated;
import com.atlassian.confluence.api.impl.pagination.PaginationServiceInternal;
import com.atlassian.confluence.api.impl.pagination.PagingIterator;
import com.atlassian.confluence.api.model.pagination.Cursor;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.model.pagination.PaginationBatch;
import com.atlassian.confluence.api.model.pagination.SkipDiscardLimitedRequest;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.persistence.PersistenceException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.event.spi.EventSource;
import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;

public class PaginationServiceImpl
implements PaginationServiceInternal {
    private static final Logger log = LoggerFactory.getLogger(PaginationServiceImpl.class);
    private final PlatformTransactionManager transactionManager;
    private final SessionFactory sessionFactory;

    public PaginationServiceImpl(PlatformTransactionManager transactionManager, SessionFactory sessionFactory) {
        this.transactionManager = transactionManager;
        this.sessionFactory = sessionFactory;
    }

    public <H, M> PageResponse<M> performPaginationRequest(LimitedRequest initialRequest, PaginationBatch<H> fetchPage, Function<? super H, M> modelConverter) {
        return this.performPaginationListRequest(initialRequest, fetchPage, items -> StreamSupport.stream(items.spliterator(), false).map(modelConverter).collect(Collectors.toList()));
    }

    public <H, M> PageResponse<M> performPaginationListRequest(LimitedRequest initialRequest, PaginationBatch<H> fetchPage, Function<Iterable<H>, Iterable<M>> modelConverter) {
        PageResponse<M> latestResponse;
        SkipDiscardLimitedRequest skipDiscardLimitedRequest;
        this.validateRequest(initialRequest);
        Preconditions.checkNotNull(fetchPage);
        List resultList = new ArrayList();
        int dbPageSize = initialRequest.getLimit() + initialRequest.getStart();
        int requiredResults = initialRequest.getLimit();
        int numberOfItemsToDiscard = initialRequest.getStart();
        if (initialRequest instanceof SkipDiscardLimitedRequest && (skipDiscardLimitedRequest = (SkipDiscardLimitedRequest)initialRequest).shouldSkipDiscardingThreshold()) {
            numberOfItemsToDiscard = 0;
        }
        DiscardingThreshold threshold = new DiscardingThreshold(numberOfItemsToDiscard);
        LimitedRequest currRequest = LimitedRequestImpl.create((int)0, (int)dbPageSize, (int)initialRequest.getMaxLimit());
        do {
            latestResponse = this.doRequestInTransaction(fetchPage, currRequest, modelConverter, threshold);
            Iterables.addAll(resultList, latestResponse);
            currRequest = this.calcNextRequest(resultList, currRequest, requiredResults);
        } while (latestResponse.hasMore() && resultList.size() < requiredResults);
        boolean moreElementsAvailable = latestResponse.hasMore();
        if (resultList.size() > requiredResults) {
            moreElementsAvailable = true;
            resultList = resultList.subList(0, requiredResults);
        }
        return PageResponseImpl.from(resultList, (boolean)moreElementsAvailable).pageRequest(initialRequest).build();
    }

    public <H, M> PageResponse<M> performPaginationListRequestWithCursor(LimitedRequest initialRequest, Function<LimitedRequest, PageResponse<H>> fetchBatch, Function<Iterable<H>, Iterable<M>> modelConverter, BiFunction<H, Boolean, Cursor> cursorCalculator) {
        PageResponse<M> latestResponse;
        this.validateRequest(initialRequest);
        Preconditions.checkNotNull(fetchBatch);
        Preconditions.checkNotNull((Object)initialRequest.getCursor());
        int requiredResults = initialRequest.getLimit();
        LinkedList resultList = new LinkedList();
        Cursor currentCursor = initialRequest.getCursor();
        Cursor firstCursor = null;
        boolean processingFirstBatch = true;
        int limit = initialRequest.getLimit();
        int requiredRecordCount = initialRequest.getLimit();
        do {
            LimitedRequest currRequest = LimitedRequestImpl.create((Cursor)currentCursor, (int)limit, (int)initialRequest.getMaxLimit());
            latestResponse = this.doRequestInTransactionWithCursor(fetchBatch, currRequest, modelConverter, cursorCalculator, requiredRecordCount);
            if (currentCursor.isReverse()) {
                resultList.addAll(0, latestResponse.getResults());
            } else {
                resultList.addAll(latestResponse.getResults());
            }
            requiredRecordCount = requiredResults - resultList.size();
            limit = this.getNextLimit(resultList, currRequest, requiredResults);
            if (processingFirstBatch && !resultList.isEmpty()) {
                firstCursor = currentCursor.isReverse() ? latestResponse.getNextCursor() : latestResponse.getPrevCursor();
                processingFirstBatch = false;
            }
            Cursor cursor = currentCursor = currentCursor.isReverse() ? latestResponse.getPrevCursor() : latestResponse.getNextCursor();
        } while (latestResponse.hasMore() && currentCursor != null && resultList.size() < requiredResults);
        return this.buildResponseWithCursor(resultList, latestResponse.hasMore(), initialRequest, firstCursor, currentCursor);
    }

    private <M> PageResponse<M> buildResponseWithCursor(List<M> resultList, boolean hasMore, LimitedRequest initialRequest, Cursor firstCursor, Cursor lastCursor) {
        Cursor nextCursor;
        Cursor prevCursor;
        if (initialRequest.getCursor().isReverse()) {
            prevCursor = hasMore ? lastCursor : null;
            nextCursor = initialRequest.getCursor().isEmpty() ? null : firstCursor;
        } else {
            nextCursor = hasMore ? lastCursor : null;
            prevCursor = initialRequest.getCursor().isEmpty() ? null : firstCursor;
        }
        return PageResponseImpl.from(resultList, (boolean)hasMore).pageRequest(initialRequest).nextCursor(nextCursor).prevCursor(prevCursor).build();
    }

    private void validateRequest(LimitedRequest limitedRequest) throws IllegalArgumentException {
        Preconditions.checkNotNull((Object)limitedRequest);
        Preconditions.checkArgument((limitedRequest.getLimit() >= 0 ? 1 : 0) != 0, (String)"limit cannot be less than zero! [%s]", (Object)limitedRequest);
        Preconditions.checkArgument((limitedRequest.getMaxLimit() >= 0 ? 1 : 0) != 0, (String)"maxLimit cannot be less than zero! [%s]", (Object)limitedRequest);
        Preconditions.checkArgument((limitedRequest.getMaxLimit() != Integer.MAX_VALUE ? 1 : 0) != 0, (String)"maxLimit %s is not a sensible maxLimit! [%s]", (int)limitedRequest.getMaxLimit(), (Object)limitedRequest);
        Preconditions.checkArgument((limitedRequest.getStart() >= 0 ? 1 : 0) != 0, (String)"start cannot be less than zero! [%s]", (Object)limitedRequest);
        Preconditions.checkArgument((limitedRequest.getCursor() != null && limitedRequest.getStart() == 0 || limitedRequest.getCursor() == null ? 1 : 0) != 0, (String)"start shouldn't be used together with cursor! [%s]", (Object)limitedRequest);
    }

    @Override
    public <F, T> PagingIterator<T> newPagingIterator(PaginationBatch<F> fetchBatch, int resultsPerPage, Function<Iterable<F>, Iterable<T>> modelConverter) {
        Preconditions.checkNotNull(fetchBatch);
        Preconditions.checkNotNull(modelConverter);
        return new PagingIteratorImpl<F, T>(fetchBatch, resultsPerPage, modelConverter);
    }

    @Override
    public <F, T> Paginated<T> newPaginated(PaginationBatch<F> fetchBatch, Function<Iterable<F>, Iterable<T>> modelConverter, int maxLimit) {
        return new PaginatedImpl<F, T>(modelConverter, fetchBatch, maxLimit);
    }

    private <M> LimitedRequest calcNextRequest(List<M> resultList, LimitedRequest currRequest, int requiredResults) {
        int nextOffset = currRequest.getStart() + currRequest.getLimit();
        int nextLimit = this.getNextLimit(resultList, currRequest, requiredResults);
        return LimitedRequestImpl.create((int)nextOffset, (int)nextLimit, (int)currRequest.getMaxLimit());
    }

    private <M> int getNextLimit(List<M> resultList, LimitedRequest currRequest, int requiredResults) {
        int nextLimit = currRequest.getLimit();
        if (resultList.size() == 0 || resultList.size() < requiredResults / 2) {
            nextLimit = currRequest.getLimit() * 2;
        }
        return nextLimit;
    }

    private <H, M> PageResponse<M> doRequestInTransaction(PaginationBatch<H> toExecute, LimitedRequest request, Function<Iterable<H>, Iterable<M>> converter, Predicate<H> filter) {
        ReadOnlyAndReadWriteTransactionConversionTemplate<PageResponse> template = new ReadOnlyAndReadWriteTransactionConversionTemplate<PageResponse>(this.transactionManager);
        return template.executeInReadOnly(() -> {
            log.debug("Detected existing read-only transaction, running with session clearing");
            Session existingSession = this.sessionFactory.getCurrentSession();
            ArrayList list = new ArrayList();
            try {
                PageResponse pageResponse = this.executeBatch(toExecute, request, converter, list, filter);
                return pageResponse;
            }
            finally {
                this.clearSession(existingSession, list);
            }
        }, () -> this.executeBatch(toExecute, request, converter, null, filter));
    }

    private <H, M> PageResponse<M> doRequestInTransactionWithCursor(Function<LimitedRequest, PageResponse<H>> toExecute, LimitedRequest request, Function<Iterable<H>, Iterable<M>> converter, BiFunction<H, Boolean, Cursor> cursorCalculator, int requiredRecordCount) {
        ReadOnlyAndReadWriteTransactionConversionTemplate<PageResponse> template = new ReadOnlyAndReadWriteTransactionConversionTemplate<PageResponse>(this.transactionManager);
        return template.executeInReadOnly(() -> {
            log.debug("Detected existing read-only transaction, running with session clearing");
            Session existingSession = this.sessionFactory.getCurrentSession();
            ArrayList list = new ArrayList();
            try {
                PageResponse pageResponse = this.executeBatchWithCursor(toExecute, request, converter, list, cursorCalculator, requiredRecordCount);
                return pageResponse;
            }
            finally {
                this.clearSession(existingSession, list);
            }
        }, () -> this.executeBatchWithCursor(toExecute, request, converter, null, cursorCalculator, requiredRecordCount));
    }

    private <H> void clearSession(Session session, List<H> list) {
        try {
            for (H obj : list) {
                if (!(obj instanceof HibernateProxy) && session instanceof EventSource && !((EventSource)session).getPersistenceContext().isEntryFor((Object)session)) continue;
                session.evict(obj);
            }
        }
        catch (PersistenceException ex) {
            log.error("Could not evict hibernate object during pagination, executing without session clearing, this may use more memory.", (Throwable)ex);
        }
    }

    private <H, M> PageResponse<M> executeBatch(PaginationBatch<H> toExecute, LimitedRequest request, Function<Iterable<H>, Iterable<M>> converter, List<H> hibernateObjects, Predicate<H> filter) {
        PageResponse dataResponse = Objects.requireNonNull((PageResponse)toExecute.apply((Object)request));
        List list = StreamSupport.stream(dataResponse.spliterator(), false).filter(filter).collect(Collectors.toList());
        if (hibernateObjects != null) {
            hibernateObjects.addAll(list);
        }
        Iterable<M> modelList = converter.apply(list);
        log.debug("Completed pagination for partial request : {}, response had {} entries ", (Object)request, (Object)dataResponse.size());
        return PageResponseImpl.from(modelList, (boolean)dataResponse.hasMore()).pageRequest(request).build();
    }

    @VisibleForTesting
    <H, M> PageResponse<M> executeBatchWithCursor(Function<LimitedRequest, PageResponse<H>> toExecute, LimitedRequest request, Function<Iterable<H>, Iterable<M>> converter, List<H> hibernateObjects, BiFunction<H, Boolean, Cursor> cursorCalculator, int requiredRecordCount) {
        PageResponse<H> dataResponse = Objects.requireNonNull(toExecute.apply(request));
        boolean hasMore = dataResponse.hasMore();
        List results = dataResponse.getResults();
        Cursor nextCursor = dataResponse.getNextCursor();
        Cursor prevCursor = dataResponse.getPrevCursor();
        if (hibernateObjects != null) {
            hibernateObjects.addAll(results);
        }
        if (results.size() > requiredRecordCount) {
            hasMore = true;
            if (request.getCursor().isReverse()) {
                int startIndex = results.size() - requiredRecordCount;
                results = results.subList(startIndex, results.size());
                prevCursor = cursorCalculator.apply(results.get(0), true);
            } else {
                results = results.subList(0, requiredRecordCount);
                nextCursor = cursorCalculator.apply(results.get(results.size() - 1), false);
            }
        }
        Iterable<M> modelList = converter.apply(results);
        log.debug("Completed pagination for partial request : {}, response had {} entries ", (Object)request, (Object)dataResponse.size());
        return PageResponseImpl.from(modelList, (boolean)hasMore).pageRequest(request).nextCursor(nextCursor).prevCursor(prevCursor).build();
    }

    private class PagingIteratorImpl<F, T>
    extends AbstractIterator<T>
    implements PagingIterator<T> {
        private final PaginationBatch<F> batch;
        private final int maxResults;
        private final Function<Iterable<F>, Iterable<T>> modelConverter;
        private PageResponse<T> currentPage;
        private int currentPageSize;
        private int currentPageIndex;
        private int offset;

        private PagingIteratorImpl(PaginationBatch<F> batch, int maxResults, Function<Iterable<F>, Iterable<T>> modelConverter) {
            this.batch = batch;
            this.maxResults = maxResults;
            this.modelConverter = modelConverter;
            this.currentPageSize = -1;
            this.offset = 0;
        }

        protected T computeNext() {
            if (this.currentPageIndex >= this.currentPageSize) {
                if (this.currentPage != null && !this.currentPage.hasMore()) {
                    return (T)this.endOfData();
                }
                this.currentPageIndex = 0;
                LimitedRequest newReq = LimitedRequestImpl.create((int)this.offset, (int)this.maxResults, (int)this.maxResults);
                this.currentPage = PaginationServiceImpl.this.doRequestInTransaction(this.batch, newReq, this.modelConverter, t -> true);
                this.offset += this.maxResults;
                this.currentPageSize = this.currentPage.size();
                if (this.currentPageSize == 0) {
                    if (this.currentPage.hasMore()) {
                        return this.computeNext();
                    }
                    return (T)this.endOfData();
                }
            }
            return (T)this.currentPage.getResults().get(this.currentPageIndex++);
        }
    }

    private class PaginatedImpl<F, T>
    implements Paginated<T> {
        private final Function<Iterable<F>, Iterable<T>> modelConverter;
        private final PaginationBatch<F> fetchBatch;
        private final int maxLimit;

        private PaginatedImpl(Function<Iterable<F>, Iterable<T>> modelConverter, PaginationBatch<F> fetchBatch, int maxLimit) {
            this.modelConverter = modelConverter;
            this.fetchBatch = fetchBatch;
            this.maxLimit = maxLimit;
        }

        @Override
        public PageResponse<T> page(PageRequest request) {
            return PaginationServiceImpl.this.performPaginationListRequest(LimitedRequestImpl.create((PageRequest)request, (int)this.maxLimit), this.fetchBatch, this.modelConverter);
        }

        @Override
        public PagingIterator<T> pagingIterator() {
            return PaginationServiceImpl.this.newPagingIterator(this.fetchBatch, this.maxLimit, this.modelConverter);
        }
    }

    private static class DiscardingThreshold<T>
    implements Predicate<T> {
        private int discarded;
        private final int threshold;

        DiscardingThreshold(int threshold) {
            this.threshold = threshold;
        }

        public boolean reached() {
            return this.discarded == this.threshold;
        }

        @Override
        public boolean test(T arg0) {
            if (this.reached()) {
                return true;
            }
            ++this.discarded;
            return false;
        }
    }
}


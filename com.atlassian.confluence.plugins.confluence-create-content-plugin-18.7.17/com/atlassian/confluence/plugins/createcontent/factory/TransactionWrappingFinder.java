/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.finder.ManyFetcher
 *  com.atlassian.confluence.api.service.finder.SingleFetcher
 *  com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor
 *  com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor$Permission
 *  com.atlassian.fugue.Option
 */
package com.atlassian.confluence.plugins.createcontent.factory;

import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.finder.ManyFetcher;
import com.atlassian.confluence.api.service.finder.SingleFetcher;
import com.atlassian.confluence.plugins.createcontent.transaction.ThrowingTransactionCallback;
import com.atlassian.confluence.plugins.createcontent.transaction.ThrowingTransactionTemplate;
import com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor;
import com.atlassian.fugue.Option;
import java.util.Optional;

public abstract class TransactionWrappingFinder<T>
implements SingleFetcher<T>,
ManyFetcher<T> {
    private final ThrowingTransactionTemplate transactionTemplate;
    private final SingleFetcher<T> singleFetcherDelegate;
    private final ManyFetcher<T> manyFetcherDelegate;

    public TransactionWrappingFinder(SingleFetcher<T> singleFetcherDelegate, ManyFetcher<T> manyFetcherDelegate, TransactionalHostContextAccessor hostContextAccessor) {
        this.singleFetcherDelegate = singleFetcherDelegate;
        this.manyFetcherDelegate = manyFetcherDelegate;
        this.transactionTemplate = new ThrowingTransactionTemplate(hostContextAccessor);
    }

    public PageResponse<T> fetchMany(PageRequest request) throws NotFoundException {
        ThrowingTransactionCallback callback = () -> this.manyFetcherDelegate.fetchMany(request);
        return this.executeReadOnly(callback);
    }

    private <T> T executeReadOnly(ThrowingTransactionCallback<T, NotFoundException> callback) throws NotFoundException {
        return this.transactionTemplate.execute(NotFoundException.class, TransactionalHostContextAccessor.Permission.READ_ONLY, callback);
    }

    public Option<T> fetchOne() {
        ThrowingTransactionCallback callback = () -> this.singleFetcherDelegate.fetchOne();
        return this.executeReadOnly(callback);
    }

    public T fetchOneOrNull() {
        return (T)this.fetchOne().getOrNull();
    }

    public Optional<T> fetch() {
        ThrowingTransactionCallback callback = () -> this.singleFetcherDelegate.fetch();
        return this.executeReadOnly(callback);
    }
}


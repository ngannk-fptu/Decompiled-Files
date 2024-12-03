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
 */
package com.atlassian.confluence.plugins.contentproperty;

import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.finder.ManyFetcher;
import com.atlassian.confluence.api.service.finder.SingleFetcher;
import com.atlassian.confluence.plugins.contentproperty.transaction.ThrowingTransactionCallback;
import com.atlassian.confluence.plugins.contentproperty.transaction.ThrowingTransactionTemplate;
import com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor;
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

    protected <TYPE> TYPE executeReadOnly(ThrowingTransactionCallback<TYPE, NotFoundException> callback) throws NotFoundException {
        return this.transactionTemplate.execute(NotFoundException.class, TransactionalHostContextAccessor.Permission.READ_ONLY, callback);
    }

    public Optional<T> fetch() {
        return this.executeReadOnly(() -> this.singleFetcherDelegate.fetch());
    }

    public T fetchOneOrNull() {
        return this.fetch().orElse(null);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core.rest.async;

import com.atlassian.upm.core.async.AsyncTaskInfo;
import com.atlassian.upm.core.rest.async.AsyncTaskCollectionRepresentation;
import com.atlassian.upm.core.rest.async.AsyncTaskRepresentation;
import com.atlassian.upm.core.rest.async.LegacyAsyncTaskCollectionRepresentation;
import com.atlassian.upm.core.rest.async.LegacyAsyncTaskRepresentation;

public interface AsyncTaskRepresentationFactory {
    public AsyncTaskRepresentation createAsyncTaskRepresentation(AsyncTaskInfo var1, boolean var2);

    public AsyncTaskCollectionRepresentation createAsyncTaskCollectionRepresentation(Iterable<AsyncTaskInfo> var1, boolean var2);

    public LegacyAsyncTaskRepresentation createLegacyAsyncTaskRepresentation(AsyncTaskInfo var1);

    public LegacyAsyncTaskCollectionRepresentation createLegacyAsyncTaskCollectionRepresentation(Iterable<AsyncTaskInfo> var1);
}


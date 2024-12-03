/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 */
package com.atlassian.upm.core.rest.async;

import com.atlassian.upm.core.async.AsyncTaskErrorInfo;
import com.atlassian.upm.core.async.AsyncTaskInfo;
import com.atlassian.upm.core.async.AsyncTaskStatus;
import com.atlassian.upm.core.rest.BaseUriBuilder;
import com.atlassian.upm.core.rest.async.AsyncTaskCollectionRepresentation;
import com.atlassian.upm.core.rest.async.AsyncTaskRepresentation;
import com.atlassian.upm.core.rest.async.AsyncTaskRepresentationFactory;
import com.atlassian.upm.core.rest.async.LegacyAsyncTaskCollectionRepresentation;
import com.atlassian.upm.core.rest.async.LegacyAsyncTaskRepresentation;
import com.google.common.collect.ImmutableMap;
import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AsyncTaskRepresentationFactoryImpl
implements AsyncTaskRepresentationFactory {
    private final BaseUriBuilder uriBuilder;

    public AsyncTaskRepresentationFactoryImpl(BaseUriBuilder uriBuilder) {
        this.uriBuilder = uriBuilder;
    }

    @Override
    public AsyncTaskRepresentation createAsyncTaskRepresentation(AsyncTaskInfo taskInfo, boolean isAdmin) {
        AsyncTaskStatus status = taskInfo.getStatus();
        ImmutableMap.Builder links = ImmutableMap.builder();
        links.put((Object)"self", (Object)this.uriBuilder.buildPendingTaskUri(taskInfo.getId()));
        for (URI resultUri : taskInfo.getStatus().getResultUri()) {
            links.put((Object)"result", (Object)resultUri);
        }
        for (URI nextUri : taskInfo.getStatus().getNextStepPostUri()) {
            links.put((Object)"nextStepPost", (Object)nextUri);
        }
        return new AsyncTaskRepresentation((Map<String, URI>)links.build(), status.isDone(), status.getError().getOrElse((AsyncTaskErrorInfo)null), taskInfo.getType().name(), status.getDescription().getOrElse((String)null), status.getItemsDone().getOrElse((Integer)null), status.getItemsTotal().getOrElse((Integer)null), status.getProgress().getOrElse((Float)null), status.getPollDelay(), taskInfo.getTimestamp(), isAdmin ? taskInfo.getUserKey() : null);
    }

    @Override
    public AsyncTaskCollectionRepresentation createAsyncTaskCollectionRepresentation(Iterable<AsyncTaskInfo> tasks, boolean isAdmin) {
        Iterable reps = StreamSupport.stream(tasks.spliterator(), false).map(task -> this.createAsyncTaskRepresentation((AsyncTaskInfo)task, isAdmin)).collect(Collectors.toList());
        return new AsyncTaskCollectionRepresentation(reps, this.uriBuilder);
    }

    @Override
    public LegacyAsyncTaskRepresentation createLegacyAsyncTaskRepresentation(AsyncTaskInfo taskInfo) {
        return new LegacyAsyncTaskRepresentation(taskInfo, this.uriBuilder);
    }

    @Override
    public LegacyAsyncTaskCollectionRepresentation createLegacyAsyncTaskCollectionRepresentation(Iterable<AsyncTaskInfo> tasks) {
        Iterable reps = StreamSupport.stream(tasks.spliterator(), false).map(this::createLegacyAsyncTaskRepresentation).collect(Collectors.toList());
        return new LegacyAsyncTaskCollectionRepresentation(reps, this.uriBuilder);
    }
}


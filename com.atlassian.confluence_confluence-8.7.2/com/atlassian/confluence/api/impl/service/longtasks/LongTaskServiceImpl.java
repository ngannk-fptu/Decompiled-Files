/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.longtasks.LongTaskId
 *  com.atlassian.confluence.api.model.longtasks.LongTaskStatus
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.service.longtasks.LongTaskService
 *  com.atlassian.confluence.rest.api.model.pagination.PaginationLimits
 *  com.atlassian.core.task.longrunning.LongRunningTask
 */
package com.atlassian.confluence.api.impl.service.longtasks;

import com.atlassian.confluence.api.impl.service.longtasks.LongTaskFactory;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.longtasks.LongTaskId;
import com.atlassian.confluence.api.model.longtasks.LongTaskStatus;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.service.longtasks.LongTaskService;
import com.atlassian.confluence.internal.longrunning.LongRunningTaskManagerInternal;
import com.atlassian.confluence.rest.api.model.pagination.PaginationLimits;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.longrunning.LongRunningTaskId;
import com.atlassian.core.task.longrunning.LongRunningTask;
import java.util.Optional;

public class LongTaskServiceImpl
implements LongTaskService {
    private final LongRunningTaskManagerInternal longRunningTaskManager;

    public LongTaskServiceImpl(LongRunningTaskManagerInternal longRunningTaskManager) {
        this.longRunningTaskManager = longRunningTaskManager;
    }

    public Optional<LongTaskStatus> getStatus(LongTaskId id, Expansion ... expansions) {
        LongRunningTaskId internalId;
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        LongRunningTask longRunningTask = this.longRunningTaskManager.getLongRunningTask(user, internalId = LongRunningTaskId.from(id));
        if (longRunningTask == null) {
            return Optional.empty();
        }
        LongTaskStatus task = LongTaskFactory.buildStatus(id, longRunningTask);
        return Optional.of(task);
    }

    public PageResponse<LongTaskStatus> getAll(PageRequest request, Expansion ... expansions) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        LimitedRequest limitedRequest = LimitedRequestImpl.create((PageRequest)request, (int)PaginationLimits.longTasks());
        return this.longRunningTaskManager.getAllTasks(user, limitedRequest);
    }
}


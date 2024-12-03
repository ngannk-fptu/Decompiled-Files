/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.model.pagination.PaginationBatch
 *  com.atlassian.confluence.api.service.pagination.PaginationService
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.core.bean.EntityObject
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.tasklist.service;

import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.model.pagination.PaginationBatch;
import com.atlassian.confluence.api.service.pagination.PaginationService;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.tasklist.Task;
import com.atlassian.confluence.plugins.tasklist.ao.AOInlineTask;
import com.atlassian.confluence.plugins.tasklist.service.util.TaskContentUtils;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.core.bean.EntityObject;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class TaskPaginationService {
    private final PermissionManager permissionManager;
    private final PageManager pageManager;
    private final PaginationService paginationService;
    private final UserAccessor userAccessor;
    private static final int MAX_LIMIT = 5000;

    public TaskPaginationService(PermissionManager permissionManager, PageManager pageManager, PaginationService paginationService, UserAccessor userAccessor) {
        this.permissionManager = permissionManager;
        this.pageManager = pageManager;
        this.paginationService = paginationService;
        this.userAccessor = userAccessor;
    }

    public PageResponse<Task> filter(List<AOInlineTask> inlineTasks, PageRequest pageRequest, ConfluenceUser confluenceUser) {
        LimitedRequest limitedRequest = LimitedRequestImpl.create((PageRequest)pageRequest, (int)5000);
        if (inlineTasks == null || inlineTasks.isEmpty()) {
            return PageResponseImpl.from(Collections.emptyList(), (boolean)false).pageRequest(limitedRequest).build();
        }
        PaginationBatch paginationBatch = request -> {
            boolean hasMore = true;
            int start = request.getStart();
            int limit = request.getLimit();
            int endIndex = Math.min(start + limit + 1, inlineTasks.size());
            List input = inlineTasks.subList(request.getStart(), endIndex);
            Set pageIds = input.stream().map(AOInlineTask::getContentId).collect(Collectors.toSet());
            List pages = this.pageManager.getAbstractPages(pageIds);
            List<AbstractPage> canView = this.getPermittedPages(confluenceUser, pages);
            Set viewablePageIds = canView.stream().map(EntityObject::getId).collect(Collectors.toSet());
            Iterable results = input.stream().filter(input1 -> viewablePageIds.contains(input1.getContentId()) && !TaskContentUtils.isBlankContent(input1.getBody())).collect(Collectors.toList());
            if (endIndex >= inlineTasks.size()) {
                hasMore = false;
            }
            return PageResponseImpl.from((Iterable)results, (boolean)hasMore).pageRequest(request).build();
        };
        PageResponse tasks = this.paginationService.performPaginationRequest(limitedRequest, paginationBatch, ao -> new Task.Builder().withGlobalId(ao.getGlobalId()).withId(ao.getId()).withContentId(ao.getContentId()).withStatus(ao.getTaskStatus()).withBody(ao.getBody()).withCreator(this.getUsername(ao.getCreatorUserKey())).withAssignee(this.getUsername(ao.getAssigneeUserKey())).withCreateDate(ao.getCreateDate()).withDueDate(ao.getDueDate()).withUpdateDate(ao.getUpdateDate()).withCompleteDate(ao.getCompleteDate()).withCompleteUser(this.getUsername(ao.getCompleteUserKey())).build());
        return tasks;
    }

    protected List<AbstractPage> getPermittedPages(ConfluenceUser confluenceUser, List<AbstractPage> pages) {
        return this.permissionManager.getPermittedEntitiesNoExemptions((User)confluenceUser, Permission.VIEW, pages);
    }

    private String getUsername(String userKey) {
        if (userKey == null) {
            return null;
        }
        ConfluenceUser user = this.userAccessor.getUserByKey(new UserKey(userKey));
        return user == null ? null : user.getName();
    }
}


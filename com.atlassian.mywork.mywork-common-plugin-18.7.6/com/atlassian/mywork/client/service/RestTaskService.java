/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.google.common.base.Function
 *  org.codehaus.jackson.type.TypeReference
 */
package com.atlassian.mywork.client.service;

import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.mywork.client.ClientUtil;
import com.atlassian.mywork.client.service.ReliableRestService;
import com.atlassian.mywork.client.service.RemoteTaskService;
import com.atlassian.mywork.client.util.FutureUtil;
import com.atlassian.mywork.model.Status;
import com.atlassian.mywork.model.Task;
import com.atlassian.mywork.model.TaskBuilder;
import com.google.common.base.Function;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import org.codehaus.jackson.type.TypeReference;

public class RestTaskService
implements RemoteTaskService {
    private final ReliableRestService restService;

    public RestTaskService(ReliableRestService restService) {
        this.restService = restService;
    }

    @Override
    public Task find(String username, String globalId) {
        try {
            return this.restService.get(username, "/rest/mywork/1/task/globalId/" + ClientUtil.urlEncode(globalId), Task.class);
        }
        catch (CredentialsRequiredException e) {
            return null;
        }
    }

    @Override
    public Future<Task> markComplete(String username, String globalId) {
        return this.updateByGlobalId(username, globalId, new Function<Task, Task>(){

            public Task apply(Task from) {
                return new TaskBuilder(from).status(Status.DONE).createTask();
            }
        });
    }

    @Override
    public Future<Task> markIncomplete(String username, String globalId) {
        return this.updateByGlobalId(username, globalId, new Function<Task, Task>(){

            public Task apply(Task from) {
                return new TaskBuilder(from).status(Status.TODO).createTask();
            }
        });
    }

    @Override
    public Future<Task> setTitle(String username, String globalId, final String title) {
        return this.updateByGlobalId(username, globalId, new Function<Task, Task>(){

            public Task apply(Task from) {
                return new TaskBuilder(from).title(title).createTask();
            }
        });
    }

    private Future<Task> updateByGlobalId(String username, String globalId, Function<Task, Task> updateFunction) {
        return this.createOrUpdate(username, (Task)updateFunction.apply((Object)this.find(username, globalId)));
    }

    @Override
    public Future<Task> createOrUpdate(String username, Task task) {
        Function<List<Task>, Task> getFirstItem = new Function<List<Task>, Task>(){

            public Task apply(List<Task> input) {
                return input.get(0);
            }
        };
        return FutureUtil.map(this.createTasksInternal(Collections.singletonList(task), username), getFirstItem);
    }

    @Override
    public Future<List<Task>> createOrUpdate(String username, List<Task> tasks) {
        return this.createTasksInternal(tasks, username);
    }

    private Future<List<Task>> createTasksInternal(List<Task> tasks, String username) {
        ArrayList<Task> jsonTasks = new ArrayList<Task>(tasks.size());
        for (Task task : tasks) {
            jsonTasks.add(new TaskBuilder(task).user(username).createTask());
        }
        return this.restService.post(username, "/rest/mywork/1/task", jsonTasks, new TypeReference<List<Task>>(){});
    }

    @Override
    public void delete(String username, String globalId) {
        this.restService.delete(username, "/rest/mywork/1/task?user=" + ClientUtil.urlEncode(username) + "&globalId=" + ClientUtil.urlEncode(globalId));
    }
}


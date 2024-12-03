/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.core.user.preferences.UserPreferences
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.mywork.model.Status
 *  com.atlassian.mywork.model.Task
 *  com.atlassian.mywork.model.TaskBuilder
 *  com.atlassian.mywork.rest.CacheControl
 *  com.atlassian.mywork.rest.Position
 *  com.atlassian.mywork.service.LocalTaskService
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.codehaus.jackson.node.TextNode
 */
package com.atlassian.mywork.host.rest;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.core.user.preferences.UserPreferences;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.mywork.host.event.MyTaskDeprecatedEvent;
import com.atlassian.mywork.host.rest.TaskType;
import com.atlassian.mywork.host.service.UserService;
import com.atlassian.mywork.model.Status;
import com.atlassian.mywork.model.Task;
import com.atlassian.mywork.model.TaskBuilder;
import com.atlassian.mywork.rest.CacheControl;
import com.atlassian.mywork.rest.Position;
import com.atlassian.mywork.service.LocalTaskService;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.TextNode;

@Path(value="task")
@Produces(value={"application/json"})
public class TaskResource {
    private final LocalTaskService taskService;
    private final UserService userManager;
    private final UserAccessor userAccessor;
    private final EventPublisher eventPublisher;
    private final ObjectMapper mapper = new ObjectMapper();

    public TaskResource(LocalTaskService taskService, UserService userManager, UserAccessor userAccessor, EventPublisher eventPublisher) {
        this.taskService = taskService;
        this.userManager = userManager;
        this.eventPublisher = eventPublisher;
        this.userAccessor = userAccessor;
    }

    @GET
    public Response findByUser(@Context HttpServletRequest request, @QueryParam(value="type") TaskType type, @QueryParam(value="bypass") String bypass) {
        String username = this.userManager.getBypassUsername(request, bypass);
        Iterable tasks = type != null ? this.taskService.findAllTasksByType(username, type.entityType) : this.taskService.findAll(username);
        return Response.ok((Object)Lists.newArrayList((Iterable)tasks)).cacheControl(CacheControl.never()).build();
    }

    @GET
    @Path(value="count")
    public Response getCount(@Context HttpServletRequest request, @QueryParam(value="completed") Boolean completed) {
        String username = this.userManager.getRemoteUsername(request);
        Iterable tasks = this.taskService.findAll(username);
        if (completed != null) {
            int count = 0;
            for (Task task : tasks) {
                if (completed != (task.getStatus() == Status.DONE)) continue;
                ++count;
            }
            return Response.ok((Object)count).cacheControl(CacheControl.never()).build();
        }
        return Response.ok((Object)Iterables.size((Iterable)tasks)).cacheControl(CacheControl.never()).build();
    }

    @POST
    @Consumes(value={"application/json"})
    @XsrfProtectionExcluded
    public Response createOrUpdate(@Context HttpServletRequest request, InputStream requestBody, @QueryParam(value="bypass") String bypass) throws Exception {
        String username = this.userManager.getBypassUsername(request, bypass);
        try {
            JsonNode jsonNode = this.mapper.readTree(requestBody);
            if (jsonNode.isArray()) {
                ArrayList<Task> result = new ArrayList<Task>(jsonNode.size());
                for (JsonNode taskNode : jsonNode) {
                    result.add(this.createOrUpdate(taskNode, username));
                }
                return Response.ok(result).cacheControl(CacheControl.never()).build();
            }
            return Response.ok((Object)this.createOrUpdate(jsonNode, username)).cacheControl(CacheControl.never()).build();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Task createOrUpdate(JsonNode taskNode, String username) throws Exception {
        Task task = (Task)this.mapper.treeToValue(taskNode, Task.class);
        return (Task)this.taskService.createOrUpdate(username, task).get();
    }

    @PUT
    @Path(value="{id}")
    @Consumes(value={"application/json"})
    @XsrfProtectionExcluded
    public Response update(@Context HttpServletRequest request, @PathParam(value="id") long id, Task task) {
        String username = this.userManager.getRemoteUsername(request);
        return Response.ok((Object)this.taskService.update(username, new TaskBuilder(task).id(id).createTask())).cacheControl(CacheControl.never()).build();
    }

    @PUT
    @Path(value="{id}/notes")
    @Consumes(value={"application/json"})
    @XsrfProtectionExcluded
    public Response updateNotes(@Context HttpServletRequest request, @PathParam(value="id") long id, TextNode jsonNotes) {
        String username = this.userManager.getRemoteUsername(request);
        String notes = jsonNotes.getTextValue();
        return Response.ok((Object)this.taskService.updateNotes(username, id, notes).getNotes()).build();
    }

    @DELETE
    @Path(value="{id}")
    @XsrfProtectionExcluded
    public Response delete(@Context HttpServletRequest request, @PathParam(value="id") long id, @QueryParam(value="bypass") String bypass) {
        String username = this.userManager.getBypassUsername(request, bypass);
        this.taskService.delete(username, id);
        return Response.ok().cacheControl(CacheControl.never()).build();
    }

    @DELETE
    @XsrfProtectionExcluded
    public Response deleteByGlobalId(@Context HttpServletRequest request, @QueryParam(value="bypass") String bypass, @QueryParam(value="globalId") String globalId) {
        String username = this.userManager.getBypassUsername(request, bypass);
        this.taskService.delete(username, globalId);
        return Response.ok().cacheControl(CacheControl.never()).build();
    }

    @PUT
    @Path(value="{id}/position")
    @Consumes(value={"application/json"})
    @XsrfProtectionExcluded
    public Response insertBefore(@Context HttpServletRequest request, @PathParam(value="id") long id, Position position) {
        String username = this.userManager.getRemoteUsername(request);
        this.taskService.moveBefore(username, id, position.getBefore());
        return Response.ok().cacheControl(CacheControl.never()).build();
    }

    @GET
    @Path(value="globalId/{globalId}")
    public Response getByGlobalId(@Context HttpServletRequest request, @PathParam(value="globalId") String globalId) {
        Task task = this.taskService.find(this.userManager.getRemoteUsername(request), globalId);
        if (task == null) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).cacheControl(CacheControl.never()).build();
        }
        return Response.ok((Object)task).cacheControl(CacheControl.never()).build();
    }

    @POST
    @Path(value="migrateTasks")
    @Consumes(value={"application/json"})
    @XsrfProtectionExcluded
    public Response migrateTasks(@Context HttpServletRequest request) throws Exception {
        UserKey currentUserKey = this.userManager.getRemoteUserKey(request);
        ConfluenceUser user = this.userAccessor.getExistingUserByKey(currentUserKey);
        UserPreferences userPreferences = this.userAccessor.getUserPreferences((User)user);
        userPreferences.setBoolean("confluence.plugins.myworkday.personaltasks.hasinteracted", true);
        if (userPreferences.getBoolean("confluence.plugins.myworkday.personaltasks.migrated")) {
            return Response.ok().build();
        }
        userPreferences.setBoolean("confluence.plugins.myworkday.personaltasks.migrated", true);
        this.eventPublisher.publish((Object)new MyTaskDeprecatedEvent(currentUserKey));
        return Response.ok().build();
    }

    @POST
    @Path(value="hasInteracted")
    @Consumes(value={"application/json"})
    @XsrfProtectionExcluded
    public Response hasInteracted(@Context HttpServletRequest request) throws Exception {
        UserKey currentUserKey = this.userManager.getRemoteUserKey(request);
        ConfluenceUser user = this.userAccessor.getExistingUserByKey(currentUserKey);
        UserPreferences userPreferences = this.userAccessor.getUserPreferences((User)user);
        userPreferences.setBoolean("confluence.plugins.myworkday.personaltasks.hasinteracted", true);
        return Response.ok().build();
    }
}


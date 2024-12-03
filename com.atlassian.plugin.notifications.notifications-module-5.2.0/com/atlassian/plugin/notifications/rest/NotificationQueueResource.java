/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.google.common.base.Function
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Multimap
 *  javax.annotation.Nullable
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.plugin.notifications.rest;

import com.atlassian.plugin.notifications.api.HandleErrorFunction;
import com.atlassian.plugin.notifications.api.event.NotificationEvent;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.queue.NotificationQueueManager;
import com.atlassian.plugin.notifications.api.queue.NotificationTask;
import com.atlassian.plugin.notifications.config.ServerConfigurationManager;
import com.atlassian.plugin.notifications.dispatcher.NotificationError;
import com.atlassian.plugin.notifications.dispatcher.NotificationErrorRegistry;
import com.atlassian.plugin.notifications.dispatcher.TaskErrors;
import com.atlassian.plugin.notifications.rest.entity.NotificationQueue;
import com.atlassian.plugin.notifications.rest.entity.QueueItem;
import com.atlassian.plugin.notifications.rest.entity.ServerModel;
import com.atlassian.plugin.notifications.rest.entity.TaskState;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Qualifier;

@Path(value="queue")
@Produces(value={"application/json"})
@Consumes(value={"application/json"})
public class NotificationQueueResource {
    private final UserManager userManager;
    private final NotificationQueueManager queueManager;
    private final ServerConfigurationManager serverConfigurationManager;
    private final NotificationErrorRegistry errorRegistry;
    private final I18nResolver i18n;

    public NotificationQueueResource(UserManager userManager, NotificationQueueManager queueManager, ServerConfigurationManager serverConfigurationManager, NotificationErrorRegistry errorRegistry, @Qualifier(value="i18nResolver") I18nResolver i18n) {
        this.userManager = userManager;
        this.queueManager = queueManager;
        this.serverConfigurationManager = serverConfigurationManager;
        this.errorRegistry = errorRegistry;
        this.i18n = i18n;
    }

    @Path(value="server/{id}")
    @DELETE
    @WebSudoRequired
    public Response clearErrors(@Context HttpServletRequest request, @PathParam(value="id") int serverId) {
        String remoteUsername = this.userManager.getRemoteUsername(request);
        if (!this.userManager.isSystemAdmin(remoteUsername)) {
            return Response.status((Response.Status)Response.Status.UNAUTHORIZED).cacheControl(HandleErrorFunction.NO_CACHE).build();
        }
        this.errorRegistry.removeErrors(serverId);
        return Response.ok().cacheControl(HandleErrorFunction.NO_CACHE).build();
    }

    @GET
    public Response getQueue(@Context HttpServletRequest request) {
        String remoteUsername = this.userManager.getRemoteUsername(request);
        if (!this.userManager.isSystemAdmin(remoteUsername)) {
            return Response.status((Response.Status)Response.Status.UNAUTHORIZED).cacheControl(HandleErrorFunction.NO_CACHE).build();
        }
        Map<Integer, List<TaskErrors>> serverErrors = this.errorRegistry.getServerErrors();
        ArrayList serverModels = Lists.newArrayList();
        for (Map.Entry<Integer, List<TaskErrors>> serverStatus : serverErrors.entrySet()) {
            ServerConfiguration server = this.serverConfigurationManager.getServer(serverStatus.getKey());
            if (server == null) continue;
            Date lastEventDate = this.errorRegistry.getLastEventDate(server.getId());
            long lastChecked = 0L;
            String lastCheckedDuration = null;
            if (lastEventDate != null) {
                lastChecked = lastEventDate.getTime();
                lastCheckedDuration = this.formatDuration((System.currentTimeMillis() - lastChecked) / 1000L);
            }
            serverModels.add(new ServerModel(server, this.i18n, serverStatus.getValue(), lastChecked, lastCheckedDuration));
        }
        NotificationQueue ret = new NotificationQueue(this.transformItems(this.queueManager.getQueuedTasks(), serverErrors), serverModels);
        return Response.ok((Object)ret).cacheControl(HandleErrorFunction.NO_CACHE).build();
    }

    private List<QueueItem> transformItems(List<NotificationTask> items, Map<Integer, List<TaskErrors>> serverErrors) {
        final long now = System.currentTimeMillis();
        ArrayListMultimap errorMap = ArrayListMultimap.create();
        for (List<TaskErrors> taskErrors : serverErrors.values()) {
            for (TaskErrors taskError : taskErrors) {
                for (NotificationError error : taskError.getErrors()) {
                    errorMap.put((Object)taskError.getTaskId(), (Object)error);
                }
            }
        }
        ArrayList ret = Lists.newArrayList((Iterable)Iterables.transform(items, (Function)new Function<NotificationTask, QueueItem>((Multimap)errorMap){
            final /* synthetic */ Multimap val$errorMap;
            {
                this.val$errorMap = multimap;
            }

            public QueueItem apply(@Nullable NotificationTask input) {
                if (input != null) {
                    NotificationEvent event = input.getEvent();
                    Date eventTime = event.getTime();
                    String itemDuration = NotificationQueueResource.this.i18n.getText("notifications.plugin.queue.item.unknown.duration");
                    long duration = 0L;
                    if (eventTime != null) {
                        duration = (now - eventTime.getTime()) / 1000L;
                        itemDuration = NotificationQueueResource.this.formatDuration(duration);
                    }
                    long stateChangeDuration = (now - input.getStatus().getLastStateChange()) / 1000L;
                    String stateChangeDurationPretty = NotificationQueueResource.this.formatDuration(stateChangeDuration);
                    int secondsUntilNextRun = (int)Math.max(0L, (input.getNextAttemptTime() - System.currentTimeMillis()) / 1000L);
                    String prettyDurationNextRun = NotificationQueueResource.this.formatDuration(secondsUntilNextRun);
                    TaskState taskStatusEntity = new TaskState(input.getStatus().getState().toString(), NotificationQueueResource.this.i18n.getText(input.getStatus().getState().getI18nKey()));
                    String id = input.getId();
                    ArrayList errors = Lists.newArrayList();
                    errors.addAll(this.val$errorMap.get((Object)id));
                    return new QueueItem(id, duration, itemDuration, taskStatusEntity, input.getRecipientDescriptions(NotificationQueueResource.this.i18n), stateChangeDuration, stateChangeDurationPretty, input.getRecipientType(), event.getSubject(), input.getSendCount(), secondsUntilNextRun, prettyDurationNextRun, errors);
                }
                return null;
            }
        }));
        Collections.sort(ret);
        return ret;
    }

    private String formatDuration(long durationSeconds) {
        if (durationSeconds == 0L) {
            return this.i18n.getText("notifications.plugin.duration.now");
        }
        long minutes = durationSeconds / 60L;
        long hours = minutes / 60L;
        long minutesRemaining = minutes - hours * 60L;
        long secondsRemaining = durationSeconds - (minutes * 60L + hours * 3600L);
        StringBuilder ret = new StringBuilder();
        if (hours > 0L) {
            ret.append(hours).append(" ").append(this.i18n.getText("notifications.plugin.duration.hours"));
        }
        if (minutesRemaining > 0L) {
            ret.append(" ").append(minutesRemaining).append(" ").append(this.i18n.getText("notifications.plugin.duration.mins"));
        }
        if (secondsRemaining > 0L) {
            ret.append(" ").append(secondsRemaining).append(" ").append(this.i18n.getText("notifications.plugin.duration.secs"));
        }
        ret.append(" ").append(this.i18n.getText("notifications.plugin.duration.ago"));
        return ret.toString().trim();
    }
}


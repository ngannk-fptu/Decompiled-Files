/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  org.apache.log4j.Logger
 */
package com.atlassian.plugin.notifications.dispatcher;

import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.queue.NotificationTask;
import com.atlassian.plugin.notifications.config.ServerConfigurationManager;
import com.atlassian.plugin.notifications.dispatcher.NotificationError;
import com.atlassian.plugin.notifications.dispatcher.NotificationErrorRegistry;
import com.atlassian.plugin.notifications.dispatcher.TaskErrors;
import com.atlassian.plugin.notifications.dispatcher.util.DiscardingMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.log4j.Logger;

public class NotificationErrorRegistryImpl
implements NotificationErrorRegistry {
    private static final Logger log = Logger.getLogger(NotificationErrorRegistryImpl.class);
    private final ConcurrentMap<Integer, Map<String, TaskErrors>> serverErrors = new ConcurrentHashMap<Integer, Map<String, TaskErrors>>();
    private final ServerConfigurationManager serverConfigurationManager;
    private final ConcurrentMap<Integer, Date> statusCheckPerServer = new ConcurrentHashMap<Integer, Date>();
    private final AtomicInteger unknownErrorCount = new AtomicInteger(0);

    public NotificationErrorRegistryImpl(ServerConfigurationManager serverConfigurationManager) {
        this.serverConfigurationManager = serverConfigurationManager;
    }

    @Override
    public Logger getLogger() {
        return log;
    }

    @Override
    public void addUnknownError(int serverId, NotificationTask task, NotificationError error) {
        this.unknownErrorCount.incrementAndGet();
        this.addError(serverId, task, error);
    }

    @Override
    public void addError(int serverId, NotificationTask task, NotificationError error) {
        String taskId;
        Map taskErrors;
        this.statusCheckPerServer.put(serverId, new Date());
        if (!this.serverErrors.containsKey(serverId)) {
            this.serverErrors.putIfAbsent(serverId, new DiscardingMap(10));
        }
        if (!(taskErrors = (Map)this.serverErrors.get(serverId)).containsKey(taskId = task.getId())) {
            taskErrors.put(taskId, new TaskErrors(taskId, task.getStatus()));
        }
        ((TaskErrors)taskErrors.get(taskId)).addError(error);
        ServerConfiguration server = this.serverConfigurationManager.getServer(serverId);
        StringBuilder serverNameAndId = new StringBuilder();
        if (server != null) {
            serverNameAndId.append("'").append(server.getServerName()).append("'");
        } else {
            serverNameAndId.append("'<Unknown>'");
        }
        serverNameAndId.append("(").append(serverId).append(")");
        StringBuilder msg = new StringBuilder();
        msg.append("Error sending notification to server ").append((CharSequence)serverNameAndId).append(" for ").append((Object)task.getRecipientType()).append(" task (resent ").append(task.getSendCount() - 1).append(" times): ").append(error.getMessage());
        if (serverId == -1 || log.isDebugEnabled()) {
            msg.append("\n").append(error.getStackTrace());
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)msg.toString());
        } else {
            log.error((Object)msg.toString());
        }
    }

    @Override
    public void removeErrors(int serverId) {
        this.serverErrors.remove(serverId);
    }

    @Override
    public void removeTaskErrors(String taskId) {
        for (Map taskErrorsMap : this.serverErrors.values()) {
            taskErrorsMap.remove(taskId);
        }
    }

    @Override
    public int getUnknownErrorCount() {
        return this.unknownErrorCount.get();
    }

    @Override
    public Map<Integer, List<TaskErrors>> getServerErrors() {
        LinkedHashMap ret = Maps.newLinkedHashMap();
        for (ServerConfiguration config : this.serverConfigurationManager.getServers()) {
            int serverId = config.getId();
            Map taskErrors = (Map)this.serverErrors.get(serverId);
            ArrayList errors = Lists.newArrayList();
            if (taskErrors != null) {
                errors.addAll(taskErrors.values());
            }
            ret.put(serverId, errors);
        }
        Map unknownErrors = (Map)this.serverErrors.get(-1);
        if (unknownErrors != null) {
            ret.put(-1, Lists.newArrayList(unknownErrors.values()));
        }
        return ret;
    }

    @Override
    public void logSuccess(int serverId) {
        this.statusCheckPerServer.put(serverId, new Date());
    }

    @Override
    public Date getLastEventDate(int serverId) {
        return (Date)this.statusCheckPerServer.get(serverId);
    }
}


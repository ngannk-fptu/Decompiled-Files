/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  org.codehaus.jackson.JsonFactory
 *  org.codehaus.jackson.map.DeserializationConfig$Feature
 *  org.codehaus.jackson.map.MappingJsonFactory
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.core.async;

import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.async.AsyncTaskInfo;
import com.atlassian.upm.core.async.AsyncTaskStatus;
import com.atlassian.upm.core.async.AsynchronousTaskStatusStore;
import com.atlassian.upm.core.impl.NamespacedPluginSettings;
import com.atlassian.upm.impl.Locks;
import com.atlassian.upm.lifecycle.UpmProductDataStartupComponent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AsynchronousTaskStatusStoreImpl
implements AsynchronousTaskStatusStore,
UpmProductDataStartupComponent {
    private static final Logger log = LoggerFactory.getLogger((String)AsynchronousTaskStatusStoreImpl.class.getName());
    private final PluginSettingsFactory pluginSettingsFactory;
    private final ObjectMapper mapper;
    private final ClusterLock lock;

    public AsynchronousTaskStatusStoreImpl(PluginSettingsFactory pluginSettingsFactory, ClusterLockService lockService) {
        this.pluginSettingsFactory = Objects.requireNonNull(pluginSettingsFactory, "pluginSettingsFactory");
        this.lock = Locks.getLock(Objects.requireNonNull(lockService, "lockService"), this.getClass());
        this.mapper = new ObjectMapper((JsonFactory)new MappingJsonFactory());
        this.mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public void addTask(AsyncTaskInfo taskInfo) {
        Locks.writeWithLock(this.lock, () -> this.storeOngoingTask(taskInfo));
    }

    @Override
    public Option<AsyncTaskInfo> updateTaskStatus(String taskId, AsyncTaskStatus newStatus) {
        return Locks.writeWithLock(this.lock, () -> {
            Iterator<AsyncTaskInfo> iterator = this.getTaskInternal(taskId).iterator();
            if (iterator.hasNext()) {
                AsyncTaskInfo taskInfo = iterator.next();
                if (log.isDebugEnabled()) {
                    log.debug("updating status: " + newStatus);
                }
                if (!taskInfo.getStatus().isDone()) {
                    AsyncTaskInfo newInfo = taskInfo.withStatus(newStatus);
                    this.storeOngoingTask(newInfo);
                    if (newStatus.isDone()) {
                        this.removeOngoingTaskId(taskId);
                        this.addCompletedTaskId(taskId);
                    }
                    return Option.some(newInfo);
                }
                return Option.some(taskInfo);
            }
            log.warn("Attempted to update the status of an asynchronous task which is no longer running.");
            return Option.none();
        });
    }

    private void storeOngoingTask(AsyncTaskInfo taskInfo) {
        String taskId = taskInfo.getId();
        String map = this.map(taskInfo);
        this.getPluginSettings().put(this.getTaskKey(taskId), (Object)map);
        this.addOngoingTaskId(taskId);
    }

    @Override
    public void removeTask(String taskId) {
        Locks.writeWithLock(this.lock, () -> {
            this.getPluginSettings().remove(this.getTaskKey(taskId));
            this.removeOngoingTaskId(taskId);
        });
    }

    @Override
    public Option<AsyncTaskInfo> getTask(String taskId) {
        return Locks.readWithLock(this.lock, () -> this.getTaskInternal(taskId));
    }

    public Collection<AsyncTaskInfo> getOngoingTasks() {
        return Locks.readWithLock(this.lock, () -> Collections.unmodifiableList(this.getAllOngoingTaskIds().stream().map(this::getTaskInternal).filter(Option::isDefined).map(Option::get).collect(Collectors.toList())));
    }

    private Option<AsyncTaskInfo> getTaskInternal(String taskId) {
        AsynchronousTaskStatusStoreImpl self = this;
        return Option.option(this.getPluginSettings().get(this.getTaskKey(taskId))).flatMap(o -> {
            if (o instanceof String) {
                AsyncTaskInfo task = self.map(o.toString());
                return Option.some(task);
            }
            return Option.none();
        });
    }

    private String map(AsyncTaskInfo taskInfo) {
        try {
            return this.mapper.writeValueAsString((Object)taskInfo);
        }
        catch (Exception e) {
            log.warn("Cannot serialize asynchronous task with id: " + taskInfo.getId(), (Throwable)e);
            throw new RuntimeException(e);
        }
    }

    private AsyncTaskInfo map(String taskBody) {
        try {
            return (AsyncTaskInfo)this.mapper.readValue(taskBody, AsyncTaskInfo.class);
        }
        catch (Exception e) {
            log.warn("Cannot deserialize asynchronous task with json: " + taskBody, (Throwable)e);
            throw new RuntimeException(e);
        }
    }

    private void addOngoingTaskId(String taskId) {
        this.addTaskId(this.getAllOngoingTasksKey(), taskId);
    }

    private void addCompletedTaskId(String taskId) {
        this.addTaskId(this.getAllCompletedTasksKey(), taskId);
    }

    private void addTaskId(String prefixKey, String taskId) {
        HashSet<String> taskIds = new HashSet<String>(this.getAllTaskIds(prefixKey, true));
        taskIds.add(taskId);
        this.getPluginSettings().put(prefixKey, new ArrayList<String>(taskIds));
    }

    private void removeOngoingTaskId(String taskId) {
        this.removeTaskId(this.getAllOngoingTasksKey(), taskId);
    }

    private void removeTaskId(String prefixKey, String taskId) {
        List taskIds = this.getAllTaskIds(prefixKey, true).stream().filter(id -> !id.equals(taskId)).collect(Collectors.toList());
        this.getPluginSettings().put(prefixKey, taskIds);
    }

    @Override
    public void clearOngoingTasks() {
        log.warn("Resetting UPM's list of long-running tasks.... Any existing long-running tasks will continue to run until completed, however, these tasks will not prevent UPM's front-end from being used.");
        this.clearTasksOfType(this.getAllOngoingTasksKey());
    }

    private void clearTasksOfType(String typeKey) {
        for (String taskId : this.getAllTaskIds(typeKey, false)) {
            this.getPluginSettings().remove(this.getTaskKey(taskId));
        }
        this.getPluginSettings().remove(typeKey);
    }

    private void clearAllTasks() {
        this.clearTasksOfType(this.getAllOngoingTasksKey());
        this.clearTasksOfType(this.getAllCompletedTasksKey());
    }

    private List<String> getAllOngoingTaskIds() {
        return this.getAllTaskIds(this.getAllOngoingTasksKey(), true);
    }

    private List<String> getAllTaskIds(String prefixKey, boolean clearIfInvalid) {
        Object ids = this.getPluginSettings().get(prefixKey);
        if (ids == null) {
            return Collections.emptyList();
        }
        if (!(ids instanceof List)) {
            if (clearIfInvalid) {
                log.error("Invalid asynchronous task storage has been detected: " + ids);
                this.clearAllTasks();
            }
            return Collections.emptyList();
        }
        return Collections.unmodifiableList((List)ids);
    }

    private PluginSettings getPluginSettings() {
        return new NamespacedPluginSettings(this.pluginSettingsFactory.createGlobalSettings(), this.getPluginSettingsKeyPrefix());
    }

    protected abstract String getPluginSettingsKeyPrefix();

    private String getTaskKey(String taskId) {
        return ":" + taskId + ":";
    }

    private String getAllOngoingTasksKey() {
        return this.getTaskKey("ongoing-tasks");
    }

    private String getAllCompletedTasksKey() {
        return this.getTaskKey("completed-tasks");
    }

    @Override
    public void onStartupWithProductData() {
        Locks.writeWithLock(this.lock, this::clearAllTasks);
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Maps
 *  com.google.common.util.concurrent.ListenableFutureTask
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.java.ao.ActiveObjectsException
 *  net.java.ao.DBParam
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.stp.persistence;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.troubleshooting.api.PluginInfo;
import com.atlassian.troubleshooting.stp.action.Message;
import com.atlassian.troubleshooting.stp.persistence.TaskMonitorRepository;
import com.atlassian.troubleshooting.stp.persistence.TaskMonitorSchema;
import com.atlassian.troubleshooting.stp.persistence.util.SqlCondition;
import com.atlassian.troubleshooting.stp.task.MutableTaskMonitor;
import com.atlassian.troubleshooting.stp.task.TaskMonitor;
import com.atlassian.troubleshooting.stp.task.TaskMonitorFactory;
import com.atlassian.troubleshooting.stp.task.TaskType;
import com.atlassian.troubleshooting.stp.util.Base64ObjectSerializerUtil;
import com.atlassian.troubleshooting.stp.util.StreamUtil;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFutureTask;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.java.ao.ActiveObjectsException;
import net.java.ao.DBParam;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActiveObjectsTaskMonitorRepository<M extends TaskMonitor<?>>
implements TaskMonitorRepository<M> {
    private static final Logger LOG = LoggerFactory.getLogger(ActiveObjectsTaskMonitorRepository.class);
    private final TaskMonitorRepository<M> delegate;
    private final ActiveObjects activeObjects;
    private final long recentTasksTimeoutMs;
    private final TaskType taskType;
    private final boolean useTransactions;
    private final PluginInfo pluginInfo;
    private final TaskMonitorFactory taskMonitorFactory;

    public ActiveObjectsTaskMonitorRepository(ActiveObjects activeObjects, TaskMonitorRepository<M> delegate, TaskType taskType, long recentTasksTimeoutMs, boolean useTransactions, PluginInfo pluginInfo, TaskMonitorFactory taskMonitorFactory) {
        this.activeObjects = Objects.requireNonNull(activeObjects);
        this.taskType = Objects.requireNonNull(taskType);
        this.recentTasksTimeoutMs = recentTasksTimeoutMs;
        this.delegate = Objects.requireNonNull(delegate);
        this.useTransactions = useTransactions;
        this.pluginInfo = Objects.requireNonNull(pluginInfo);
        this.taskMonitorFactory = Objects.requireNonNull(taskMonitorFactory);
    }

    private static <M extends TaskMonitor<?>> TaskMonitorSchema.TaskMonitorAO updateRow(M taskMonitor, TaskMonitorSchema.TaskMonitorAO row) {
        taskMonitor.getClusteredTaskId().ifPresent(row::setClusteredTaskId);
        row.setCreatedTimestamp(taskMonitor.getCreatedTimestamp());
        taskMonitor.getNodeId().ifPresent(row::setNodeId);
        row.setProgressMessage(taskMonitor.getProgressMessage());
        row.setProgressPercentage(taskMonitor.getProgressPercentage());
        row.setSerializedErrors(Base64ObjectSerializerUtil.serialize(new ArrayList<Message>(taskMonitor.getErrors())));
        row.setSerializedWarnings(Base64ObjectSerializerUtil.serialize(new ArrayList<Message>(taskMonitor.getWarnings())));
        row.setTaskId(taskMonitor.getTaskId());
        row.setAttributes(ActiveObjectsTaskMonitorRepository.serializeAttributes(taskMonitor));
        return row;
    }

    private static String serializeAttributes(TaskMonitor<?> taskMonitor) {
        return Base64ObjectSerializerUtil.serialize(Maps.newHashMap(taskMonitor.getAttributes()));
    }

    @VisibleForTesting
    static Optional<Map<String, Serializable>> deserializeAttributes(@Nullable String storedAttributes) {
        return Optional.ofNullable(storedAttributes).flatMap(Base64ObjectSerializerUtil::deserialize).filter(Map.class::isInstance).map(Map.class::cast);
    }

    private static Optional<List<Message>> deserializeListOfMessages(String messages) {
        return Base64ObjectSerializerUtil.deserialize(messages).filter(ArrayList.class::isInstance).map(ArrayList.class::cast).map(list -> list);
    }

    @Override
    public boolean storeTaskMonitor(M taskMonitor) {
        try {
            return (Boolean)this.doInTransaction(() -> {
                this.activeObjects.create(TaskMonitorSchema.TaskMonitorAO.class, this.toDBParams((TaskMonitor<?>)taskMonitor));
                return this.delegate.storeTaskMonitor((TaskMonitor)taskMonitor);
            });
        }
        catch (ActiveObjectsException e) {
            LOG.error("Failed to persist task monitor with taskId={} into the database", (Object)taskMonitor.getTaskId(), (Object)e);
            return false;
        }
    }

    private DBParam[] toDBParams(TaskMonitor<?> taskMonitor) {
        return new DBParam[]{new DBParam("TASK_ID", (Object)taskMonitor.getTaskId()), new DBParam("TASK_MONITOR_KIND", (Object)this.getTaskMonitorKind()), new DBParam("CLUSTERED_TASK_ID", taskMonitor.getClusteredTaskId().orElse(null)), new DBParam("NODE_ID", taskMonitor.getNodeId().orElse(null)), new DBParam("SERIALIZED_ERRORS", (Object)Base64ObjectSerializerUtil.serialize(new ArrayList<Message>(taskMonitor.getErrors()))), new DBParam("SERIALIZED_WARNINGS", (Object)Base64ObjectSerializerUtil.serialize(new ArrayList<Message>(taskMonitor.getWarnings()))), new DBParam("PROGRESS_MESSAGE", (Object)taskMonitor.getProgressMessage()), new DBParam("PROGRESS_PERCENTAGE", (Object)taskMonitor.getProgressPercentage()), new DBParam("CREATED_TIMESTAMP", (Object)taskMonitor.getCreatedTimestamp()), new DBParam("TASK_STATUS", (Object)ActiveObjectsTaskMonitorRepository.serializeAttributes(taskMonitor))};
    }

    private String getTaskMonitorKind() {
        return String.format("%s.%s", this.pluginInfo.getPluginKey(), this.taskType.getKey());
    }

    @Override
    public Optional<M> getTaskMonitor(String taskId) {
        try {
            return (Optional)this.doInTransaction(() -> {
                Optional<M> foundInDelegate = this.delegate.getTaskMonitor(taskId);
                return foundInDelegate.isPresent() ? foundInDelegate : this.findByTaskId(taskId).map(this::readRow);
            });
        }
        catch (ActiveObjectsException e) {
            LOG.error("Failed to query task monitor from database by taskId={}", (Object)taskId, (Object)e);
            return Optional.empty();
        }
    }

    @Override
    public boolean updateTaskMonitor(M taskMonitor) {
        try {
            return (Boolean)this.doInTransaction(() -> {
                this.findByTaskId(taskMonitor.getTaskId()).map(taskMonitorAO -> ActiveObjectsTaskMonitorRepository.updateRow(taskMonitor, taskMonitorAO)).ifPresent(RawEntity::save);
                return this.delegate.updateTaskMonitor((TaskMonitor)taskMonitor);
            });
        }
        catch (ActiveObjectsException e) {
            LOG.error("Failed to update task monitor with taskId={} in the database", (Object)taskMonitor.getTaskId(), (Object)e);
            return false;
        }
    }

    @Override
    public void deleteTaskMonitor(M taskMonitor) {
        this.delegate.deleteTaskMonitor(taskMonitor);
        this.doInTransaction(() -> {
            this.findByTaskId(taskMonitor.getTaskId()).ifPresent(xva$0 -> this.activeObjects.delete(new RawEntity[]{xva$0}));
            return null;
        });
    }

    @Override
    public Collection<M> getRecentTaskMonitors() {
        Collection<M> monitorsInDelegate = this.delegate.getRecentTaskMonitors();
        Collection<M> monitorsInAo = this.listRecentTaskMonitors(null);
        return this.joinMonitors(monitorsInDelegate, monitorsInAo);
    }

    @Override
    public Collection<M> getRecentTaskMonitorsByNodeId(@Nonnull String nodeId) {
        return this.joinMonitors(this.delegate.getRecentTaskMonitorsByNodeId(nodeId), this.listRecentTaskMonitors(SqlCondition.isEqual("NODE_ID", nodeId)));
    }

    @Override
    public Collection<M> getRecentTaskMonitorsByClusteredTaskId(@Nonnull String clusteredTaskId) {
        Collection<M> monitorsInAo = this.listRecentTaskMonitors(SqlCondition.isEqual("CLUSTERED_TASK_ID", clusteredTaskId));
        Collection<M> monitorsInDelegate = this.delegate.getRecentTaskMonitorsByClusteredTaskId(clusteredTaskId);
        return this.joinMonitors(monitorsInDelegate, monitorsInAo);
    }

    private Collection<M> listRecentTaskMonitors(@Nullable SqlCondition condition) {
        ArrayList<Serializable> values = new ArrayList<Serializable>(Arrays.asList(this.getTaskMonitorKind(), this.recent()));
        ArrayList<String> condStrings = new ArrayList<String>(Arrays.asList(String.format("%s = ?", "TASK_MONITOR_KIND"), String.format("%s > ?", "CREATED_TIMESTAMP")));
        Optional.ofNullable(condition).ifPresent(c -> {
            condStrings.add(condition.getSql());
            values.add((Serializable)condition.getBindValue());
        });
        String queryString = StringUtils.join(condStrings, (String)" AND ");
        Query query = Query.select().where(queryString, values.toArray()).order("CREATED_TIMESTAMP DESC");
        try {
            return (Collection)this.doInTransaction(() -> Arrays.stream(this.activeObjects.find(TaskMonitorSchema.TaskMonitorAO.class, query)).map(this::readRow).collect(Collectors.toList()));
        }
        catch (ActiveObjectsException e) {
            LOG.error("Failed to retrieve list of task monitors from database", (Throwable)e);
            return Collections.emptyList();
        }
    }

    private M readRow(TaskMonitorSchema.TaskMonitorAO row) {
        TaskType taskType = this.getTaskType(row);
        MutableTaskMonitor taskMonitor = this.taskMonitorFactory.newInstance(taskType);
        taskMonitor.init(row.getTaskId(), ListenableFutureTask.create(() -> null));
        Optional.ofNullable(row.getClusteredTaskId()).ifPresent(taskMonitor::setClusteredTaskId);
        Optional.ofNullable(row.getNodeId()).ifPresent(taskMonitor::setNodeId);
        taskMonitor.updateProgress(row.getProgressPercentage(), StringUtils.trimToEmpty((String)row.getProgressMessage()));
        taskMonitor.setCreatedTimestamp(row.getCreatedTimestamp());
        ActiveObjectsTaskMonitorRepository.deserializeListOfMessages(row.getSerializedErrors()).ifPresent(messages -> messages.forEach(taskMonitor::addError));
        ActiveObjectsTaskMonitorRepository.deserializeListOfMessages(row.getSerializedWarnings()).ifPresent(messages -> messages.forEach(taskMonitor::addWarning));
        ActiveObjectsTaskMonitorRepository.deserializeAttributes(row.getAttributes()).ifPresent(taskMonitor::setCustomAttributes);
        return (M)taskMonitor;
    }

    private TaskType getTaskType(TaskMonitorSchema.TaskMonitorAO taskMonitorAO) {
        String storedPrefix = this.pluginInfo.getPluginKey() + ".";
        String taskTypeKey = StringUtils.substringAfter((String)taskMonitorAO.getTaskMonitorKind(), (String)storedPrefix);
        return TaskType.valueOfKey(taskTypeKey).orElseThrow(() -> new IllegalStateException(String.format("Unexpected task monitor kind '%s'", taskMonitorAO.getTaskMonitorKind())));
    }

    private long recent() {
        return System.currentTimeMillis() - this.recentTasksTimeoutMs;
    }

    private Optional<TaskMonitorSchema.TaskMonitorAO> findByTaskId(String taskId) {
        String query = String.format("%s = ? AND %s = ?", "TASK_MONITOR_KIND", "TASK_ID");
        return Arrays.stream(this.activeObjects.find(TaskMonitorSchema.TaskMonitorAO.class, query, new Object[]{this.getTaskMonitorKind(), taskId})).findFirst();
    }

    private <V> V doInTransaction(TransactionCallback<V> call) {
        if (this.useTransactions) {
            return (V)this.activeObjects.executeInTransaction(call);
        }
        return (V)call.doInTransaction();
    }

    private Collection<M> joinMonitors(Collection<M> first, Collection<M> second) {
        return Stream.concat(first.stream(), second.stream()).filter(StreamUtil.distinctByField(TaskMonitor::getTaskId)).collect(Collectors.toList());
    }
}


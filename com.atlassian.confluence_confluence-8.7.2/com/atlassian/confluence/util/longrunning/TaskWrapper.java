/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.FieldsAreNonnullByDefault
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 *  com.atlassian.core.task.longrunning.LongRunningTask
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.Validate
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.util.longrunning;

import com.atlassian.annotations.nullability.FieldsAreNonnullByDefault;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import com.atlassian.confluence.util.longrunning.LongRunningTaskId;
import com.atlassian.core.task.longrunning.LongRunningTask;
import com.atlassian.user.User;
import java.util.Objects;
import org.apache.commons.lang3.Validate;
import org.checkerframework.checker.nullness.qual.Nullable;

@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
final class TaskWrapper {
    private final @Nullable User user;
    private final LongRunningTaskId id;
    private final long started;
    private final long completed;
    private final LongRunningTask task;

    TaskWrapper(@Nullable User user, LongRunningTaskId id, LongRunningTask task, long started) {
        this(user, id, task, started, -1L);
    }

    private TaskWrapper(@Nullable User user, LongRunningTaskId id, LongRunningTask task, long started, long completed) {
        this.user = user;
        this.id = id;
        this.started = started;
        this.completed = completed;
        this.task = task;
    }

    public TaskWrapper(TaskWrapper oldTaskWrapper, long completed) {
        this(oldTaskWrapper.user, oldTaskWrapper.id, oldTaskWrapper.task, oldTaskWrapper.started, completed);
        Validate.isTrue((oldTaskWrapper.getCompleted() == -1L ? 1 : 0) != 0);
    }

    public long getStarted() {
        return this.started;
    }

    public long getCompleted() {
        return this.completed;
    }

    public LongRunningTask getTask() {
        return this.task;
    }

    public @Nullable User getUser() {
        return this.user;
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TaskWrapper that = (TaskWrapper)o;
        return Objects.equals(this.completed, that.completed) && Objects.equals(this.started, that.started) && Objects.equals(this.id, that.id);
    }

    public int hashCode() {
        return Objects.hash(this.id, this.started, this.completed);
    }

    public boolean isSameUser(@Nullable User user) {
        if (user == null) {
            return this.user == null;
        }
        return user.equals(this.user);
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.confluence.extra.dynamictasklist2.model;

import com.atlassian.confluence.extra.dynamictasklist2.model.TaskList;
import java.util.Iterator;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Task {
    private String id;
    private String name;
    private String assignee = "";
    private long createdDate;
    private long completedDate;
    private Priority priority;
    private TaskList owner;
    private boolean completed;
    private boolean locked;

    public Task() {
        this.priority = Priority.MEDIUM;
    }

    public Task(Task task) {
        this.id = task.id;
        this.name = task.name;
        this.assignee = task.assignee;
        this.createdDate = task.createdDate;
        this.completedDate = task.completedDate;
        this.priority = task.priority;
        this.owner = task.owner;
        this.completed = task.completed;
        this.locked = task.locked;
    }

    public Task(String name, TaskList owner) {
        this();
        this.setName(name, owner);
    }

    public void updateId(TaskList owner) {
        this.id = this.createId(this.name, owner);
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name, TaskList owner) {
        this.id = this.createId(name, owner);
        this.name = name;
        this.owner = owner;
    }

    public String getAssignee() {
        return this.assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public long getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public long getCompletedDate() {
        return this.completedDate;
    }

    public void setCompletedDate(long completedDate) {
        this.completedDate = completedDate;
    }

    public Priority getPriority() {
        return this.priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority != null ? priority : Priority.MEDIUM;
    }

    public boolean isCompleted() {
        return this.completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isLocked() {
        return this.locked && this.owner != null && this.owner.getConfig().getEnableLocking();
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    private String createId(String name, TaskList owner) {
        String taskId;
        String id;
        String listId = owner.getId();
        int count = this.countTasksWithId(id = listId + "_" + (taskId = TaskList.createId(name)), owner);
        return id + (String)(count > 0 ? "_" + count : "");
    }

    private int countTasksWithId(String id, TaskList list) {
        int count = 0;
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Task task = (Task)it.next();
            if (!task.id.startsWith(id)) continue;
            ++count;
        }
        return count;
    }

    public String toString() {
        return "name = " + this.name + "\nid = " + this.id + "\nassignee = " + this.assignee + "\ncreated on = " + this.createdDate + "\ncompleted on= " + this.completedDate;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Task)) {
            return false;
        }
        Task that = (Task)obj;
        return new EqualsBuilder().append((Object)this.id, (Object)that.id).append((Object)this.name, (Object)that.name).append((Object)this.assignee, (Object)that.assignee).append(this.createdDate, that.createdDate).append(this.completedDate, that.completedDate).append((Object)this.priority, (Object)that.priority).append(this.completed, that.completed).append(this.locked, that.locked).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder().append((Object)this.id).append((Object)this.name).append((Object)this.assignee).append(this.createdDate).append(this.completedDate).append((Object)this.priority).append(this.completed).append(this.locked).toHashCode();
    }

    public static final class Priority
    implements Comparable {
        public static final Priority HIGH = new Priority("HIGH");
        public static final Priority MEDIUM = new Priority("MEDIUM");
        public static final Priority LOW = new Priority("LOW");
        private String name;

        private Priority(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public boolean isHigh() {
            return this == HIGH;
        }

        public boolean isMedium() {
            return this == MEDIUM;
        }

        public boolean isLow() {
            return this == LOW;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Priority priority = (Priority)o;
            return this.name.equals(priority.name);
        }

        public int hashCode() {
            return this.name.hashCode();
        }

        public static Priority from(String value) {
            if (Priority.HIGH.name.startsWith(value)) {
                return HIGH;
            }
            if (Priority.LOW.name.startsWith(value)) {
                return LOW;
            }
            if (Priority.MEDIUM.name.startsWith(value)) {
                return MEDIUM;
            }
            throw new IllegalArgumentException("Unrecognized priority '" + value + "'.  Valid values are HIGH, MEDIUM and LOW.");
        }

        public static Priority[] values() {
            return new Priority[]{HIGH, MEDIUM, LOW};
        }

        public int compareTo(Object o) {
            Priority rhs = (Priority)o;
            if (this.equals(rhs)) {
                return 0;
            }
            if (this.equals(HIGH)) {
                return 1;
            }
            if (this.equals(MEDIUM) && rhs.equals(LOW)) {
                return 1;
            }
            return -1;
        }
    }
}


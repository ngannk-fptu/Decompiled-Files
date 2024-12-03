/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.confluence.extra.dynamictasklist2.model;

import com.atlassian.confluence.extra.dynamictasklist2.model.Sort;
import com.atlassian.confluence.extra.dynamictasklist2.model.Task;
import com.atlassian.confluence.extra.dynamictasklist2.model.TaskListConfig;
import com.atlassian.confluence.extra.dynamictasklist2.model.TaskListId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class TaskList {
    public static final String DEFAULT_LIST_NAME = " ";
    public static final String OCCURANCE_SEP = ":";
    private TaskListId id;
    private int occurance = 1;
    private String name;
    private List<Task> tasks;
    private TaskListConfig config = new TaskListConfig();
    private String sourceMacro;

    public TaskList(String name, int occurance) {
        this.occurance = occurance;
        this.setName(name);
        this.tasks = Collections.synchronizedList(new ArrayList());
    }

    public String getId() {
        return this.id.toString();
    }

    public String getName() {
        return this.name;
    }

    public boolean hasNonDefaultName() {
        return !DEFAULT_LIST_NAME.equals(this.name);
    }

    public void setName(String name) {
        this.id = new TaskListId(name, this.occurance);
        this.name = name;
    }

    public int getOccurance() {
        return this.occurance;
    }

    public String getSourceMacro() {
        return this.sourceMacro;
    }

    public void setSourceMacro(String sourceMacro) {
        this.sourceMacro = sourceMacro;
    }

    public List<Task> getTasks() {
        if (this.tasks == null) {
            this.setTasks();
        }
        return Collections.unmodifiableList(this.tasks);
    }

    public void setTasks(List tasks) {
        this.tasks = new ArrayList<Task>(tasks);
    }

    private void setTasks() {
        this.tasks = new ArrayList<Task>();
        this.sort();
    }

    public int getTotalTasks() {
        if (this.tasks == null) {
            this.setTasks();
        }
        return this.tasks.size();
    }

    public int getCompleteTasks() {
        if (this.tasks == null) {
            this.setTasks();
        }
        int complete = 0;
        for (int i = 0; i < this.tasks.size(); ++i) {
            Task o = this.tasks.get(i);
            Task task = o;
            if (!task.isCompleted()) continue;
            ++complete;
        }
        return complete;
    }

    public int getPercentComplete() {
        int complete = this.getCompleteTasks();
        return (int)(100.0f * ((float)complete / (float)this.tasks.size()));
    }

    public Task getTask(String taskId) {
        if (this.tasks == null) {
            this.setTasks();
        }
        for (int i = 0; i < this.tasks.size(); ++i) {
            Task task = this.tasks.get(i);
            if (!task.getId().equals(taskId)) continue;
            return task;
        }
        return null;
    }

    public void addTask(Task task) {
        if (this.tasks == null) {
            this.setTasks();
        }
        this.tasks.add(task);
        this.sort();
    }

    public void removeTask(String taskId) {
        if (this.tasks == null) {
            this.setTasks();
        }
        Task task = this.getTask(taskId);
        this.tasks.remove(task);
    }

    public void setTaskIndex(String taskId, int idx) {
        Task task = this.getTask(taskId);
        if (task != null) {
            this.getConfig().setSort(Sort.NONE);
            try {
                this.tasks.remove(task);
                this.tasks.add(idx, task);
            }
            catch (IndexOutOfBoundsException e) {
                this.tasks.remove(task);
                this.tasks.add(task);
            }
        }
    }

    public Iterator iterator() {
        return this.tasks.iterator();
    }

    public String getSessionTip() {
        String[] tips = new String[]{"Click task names to edit them.", "Use the + icon to view task details.", "Change task priority in the detail view.", "Tasks may be locked in the detail view.", "The return key saves task name edits.", "The escape key exits task name edits."};
        return tips[(int)Math.floor(Math.random() * (double)tips.length)];
    }

    public void insertTask(int index, Task task) {
        this.getConfig().setSort(Sort.NONE);
        task.updateId(this);
        this.tasks.add(index, task);
    }

    public TaskListConfig getConfig() {
        if (this.config == null) {
            this.config = new TaskListConfig();
        }
        return this.config;
    }

    public void setConfig(TaskListConfig config) {
        this.config = config;
    }

    public void sortByName() {
        this.setSort(Sort.BY_NAME);
    }

    public void sortByDate() {
        this.setSort(Sort.BY_CREATION_DATE);
    }

    public void sortByComplete() {
        this.setSort(Sort.BY_COMPLETION_DATE);
    }

    public void sortByAssignee() {
        this.setSort(Sort.BY_ASSIGNEE);
    }

    public void sortByPriority() {
        this.setSort(Sort.BY_PRIORITY);
    }

    private void setSort(Sort sort) {
        if (this.getConfig().getSort() == sort) {
            this.getConfig().setSortAscending(!this.getConfig().isSortAscending());
        } else {
            this.getConfig().setSort(sort);
            this.getConfig().setSortAscending(sort.getAscendingDefault());
        }
        this.sort();
    }

    public void sort() {
        Collections.sort(this.tasks, this.getConfig().getComparator());
    }

    public String toString() {
        String s = "\nTasklist name = " + this.name;
        s = s + "\nTasklist id = " + this.id;
        s = s + "\nTasks:";
        s = s + "\n";
        List<Task> list = this.getTasks();
        for (Task task : list) {
            s = s + "---\n";
            s = s + task.toString() + "\n";
        }
        return s;
    }

    public static String createId(String name) {
        int hash;
        if (name == null) {
            name = DEFAULT_LIST_NAME;
        }
        return (hash = name.hashCode()) >= 0 ? "p" + hash : "n" + String.valueOf(hash).substring(1);
    }

    public static int getOccuranceFromId(String listId) {
        return Integer.valueOf(listId.substring(0, listId.indexOf(OCCURANCE_SEP)));
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof TaskList)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        TaskList that = (TaskList)obj;
        return new EqualsBuilder().append((Object)this.name, (Object)that.name).append(this.tasks, that.tasks).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder().append((Object)this.name).append(this.tasks).toHashCode();
    }
}


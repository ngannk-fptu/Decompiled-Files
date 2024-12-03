/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.dynamictasklist2.model;

import com.atlassian.confluence.extra.dynamictasklist2.model.LegacyTask;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LegacyTaskList {
    private String name;
    private List tasks;

    public LegacyTaskList() {
    }

    public LegacyTaskList(String name) {
        this.name = name;
        this.tasks = Collections.synchronizedList(new ArrayList());
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List getTasks() {
        return this.tasks;
    }

    public void setTasks(List tasks) {
        this.tasks = tasks;
    }

    public int getTotalTasks() {
        return this.tasks.size();
    }

    public int getCompleteTasks() {
        int complete = 0;
        for (LegacyTask legacyTask : this.getTasks()) {
            if (!legacyTask.isCompleted()) continue;
            ++complete;
        }
        return complete;
    }

    public int getPercentComplete() {
        int complete = this.getCompleteTasks();
        return (int)(100.0f * ((float)complete / (float)this.tasks.size()));
    }

    public void addTask(String task) {
        this.tasks.add(new LegacyTask(task));
    }

    public LegacyTask getTask(String taskName) {
        for (LegacyTask legacyTask : this.tasks) {
            if (!legacyTask.getName().equals(taskName)) continue;
            return legacyTask;
        }
        return null;
    }

    public void removeTask(String task) {
        this.tasks.remove(this.getTask(task));
    }

    public void setTaskIndex(String taskName, int idx) {
        LegacyTask legacyTask = this.getTask(taskName);
        if (legacyTask != null) {
            try {
                this.tasks.remove(legacyTask);
                this.tasks.add(idx, legacyTask);
            }
            catch (IndexOutOfBoundsException e) {
                this.tasks.remove(legacyTask);
                this.tasks.add(legacyTask);
            }
        }
    }

    public String getNiceName() {
        return this.name.replaceAll("_", " ");
    }
}


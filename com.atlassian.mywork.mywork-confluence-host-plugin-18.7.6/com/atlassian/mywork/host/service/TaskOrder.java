/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mywork.model.Task
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.mywork.host.service;

import com.atlassian.mywork.model.Task;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class TaskOrder {
    private final String taskOrdering;

    public TaskOrder(String taskOrdering) {
        this.taskOrdering = taskOrdering;
    }

    public boolean isEmpty() {
        return this.taskOrdering == null;
    }

    public String getString() {
        return this.taskOrdering;
    }

    public String toString() {
        return this.taskOrdering;
    }

    public int hashCode() {
        return this.taskOrdering.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof TaskOrder)) {
            return false;
        }
        return super.equals(obj) || ((TaskOrder)obj).taskOrdering.equals(this.taskOrdering);
    }

    public TaskOrder moveBefore(Long sourceId, Long targetId) {
        return this.move(sourceId, targetId, true);
    }

    public TaskOrder moveAfter(Long sourceId, Long targetId) {
        return this.move(sourceId, targetId, false);
    }

    public TaskOrder move(Long sourceId, Long targetId, boolean before) {
        TaskOrderBuffer b = new TaskOrderBuffer();
        boolean found = false;
        if (targetId == null && !before) {
            b.add(sourceId);
            found = true;
        }
        for (Long id : this.getOrderedIds()) {
            if (before && id.equals(targetId)) {
                b.add(sourceId);
                found = true;
            }
            if (!id.equals(sourceId)) {
                b.add(id);
            }
            if (before || !id.equals(targetId)) continue;
            b.add(sourceId);
            found = true;
        }
        if (!found) {
            b.add(sourceId);
        }
        return b.build();
    }

    private Iterable<Long> getOrderedIds() {
        if (StringUtils.isBlank((CharSequence)this.taskOrdering)) {
            return Lists.newArrayList();
        }
        return Lists.transform(Arrays.asList(this.taskOrdering.split(",")), id -> Long.parseLong(id));
    }

    public Iterable<Task> order(Iterable<Task> tasks) {
        Map<Long, Task> itemMap = this.createMap(tasks);
        return Iterables.concat((Iterable)Iterables.filter((Iterable)Iterables.transform(this.getOrderedIds(), from -> (Task)itemMap.remove(from)), (Predicate)Predicates.notNull()), itemMap.values());
    }

    private Map<Long, Task> createMap(Iterable<Task> tasks) {
        HashMap<Long, Task> items = new HashMap<Long, Task>();
        for (Task item : tasks) {
            items.put(item.getId(), item);
        }
        return items;
    }

    public TaskOrder update(Iterable<Task> tasks) {
        TaskOrderBuffer buffer = new TaskOrderBuffer();
        for (Task task : this.order(tasks)) {
            buffer.add(task.getId());
        }
        return buffer.build();
    }

    public static class TaskOrderBuffer {
        public final StringBuilder buffer = new StringBuilder();

        public void add(long id) {
            if (this.buffer.length() > 0) {
                this.buffer.append(",");
            }
            this.buffer.append(id);
        }

        public TaskOrder build() {
            return new TaskOrder(this.buffer.toString());
        }
    }
}


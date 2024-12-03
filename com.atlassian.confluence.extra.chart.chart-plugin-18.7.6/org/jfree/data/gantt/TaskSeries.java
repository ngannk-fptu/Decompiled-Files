/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.gantt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jfree.data.gantt.Task;
import org.jfree.data.general.Series;
import org.jfree.util.ObjectUtilities;

public class TaskSeries
extends Series {
    private List tasks = new ArrayList();

    public TaskSeries(String name) {
        super((Comparable)((Object)name));
    }

    public void add(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Null 'task' argument.");
        }
        this.tasks.add(task);
        this.fireSeriesChanged();
    }

    public void remove(Task task) {
        this.tasks.remove(task);
        this.fireSeriesChanged();
    }

    public void removeAll() {
        this.tasks.clear();
        this.fireSeriesChanged();
    }

    public int getItemCount() {
        return this.tasks.size();
    }

    public Task get(int index) {
        return (Task)this.tasks.get(index);
    }

    public Task get(String description) {
        Task result = null;
        int count = this.tasks.size();
        for (int i = 0; i < count; ++i) {
            Task t = (Task)this.tasks.get(i);
            if (!t.getDescription().equals(description)) continue;
            result = t;
            break;
        }
        return result;
    }

    public List getTasks() {
        return Collections.unmodifiableList(this.tasks);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TaskSeries)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        TaskSeries that = (TaskSeries)obj;
        return ((Object)this.tasks).equals(that.tasks);
    }

    public Object clone() throws CloneNotSupportedException {
        TaskSeries clone = (TaskSeries)super.clone();
        clone.tasks = (List)ObjectUtilities.deepClone(this.tasks);
        return clone;
    }
}


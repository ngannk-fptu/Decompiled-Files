/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Effect
 *  org.apache.http.annotation.Experimental
 */
package com.atlassian.confluence.search;

import com.atlassian.confluence.search.IndexTask;
import com.atlassian.fugue.Effect;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import org.apache.http.annotation.Experimental;

public interface IndexTaskQueue<T extends IndexTask> {
    public int getSize();

    public List<T> getQueuedEntries();

    public List<T> getQueuedEntries(int var1);

    public void enqueue(T var1);

    public void enqueueAll(Collection<T> var1);

    @Deprecated
    public List<T> flushQueue(int var1);

    @Deprecated
    public List<T> flushQueue();

    @Deprecated
    default public int flushQueue(Effect<T> actionOnTask) {
        return this.flushAndExecute(arg_0 -> actionOnTask.apply(arg_0));
    }

    public int flushAndExecute(Consumer<T> var1);

    @Deprecated
    default public int flushQueue(Effect<T> actionOnTask, int numberOfEntries) {
        return this.flushAndExecute(arg_0 -> actionOnTask.apply(arg_0), numberOfEntries);
    }

    public int flushAndExecute(Consumer<T> var1, int var2);

    @Experimental
    public int flushQueueWithActionOnIterableOfTasks(Consumer<Iterable<T>> var1, int var2);

    public void reset();
}


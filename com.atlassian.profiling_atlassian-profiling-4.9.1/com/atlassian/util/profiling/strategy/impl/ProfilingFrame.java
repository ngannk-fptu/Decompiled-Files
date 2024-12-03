/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  javax.annotation.Nonnull
 */
package com.atlassian.util.profiling.strategy.impl;

import com.atlassian.annotations.Internal;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.strategy.impl.ProfilingTrace;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

@Internal
class ProfilingFrame
implements Ticker {
    private final List<ProfilingFrame> children;
    private final long startMemory;
    private final long startTimeNanos;
    private final ProfilingTrace trace;
    private int count;
    private long durationNanos;
    private long memoryDelta;
    private String name;
    private ProfilingFrame parent;

    ProfilingFrame(@Nonnull ProfilingTrace trace, @Nonnull String name, boolean captureMemory) {
        this.name = Objects.requireNonNull(name, "name");
        this.trace = Objects.requireNonNull(trace, "trace");
        this.children = new ArrayList<ProfilingFrame>();
        this.count = 1;
        this.durationNanos = -1L;
        this.startMemory = captureMemory ? ProfilingFrame.getUsedMemory() : -1L;
        this.startTimeNanos = System.nanoTime();
    }

    @Override
    public void close() {
        int index;
        List<ProfilingFrame> siblings;
        this.durationNanos = System.nanoTime() - this.startTimeNanos;
        if (this.startMemory != -1L) {
            this.memoryDelta = ProfilingFrame.getUsedMemory() - this.startMemory;
        }
        if (this.parent != null && (siblings = this.parent.children).size() > 1 && (index = siblings.lastIndexOf(this)) > 0 && siblings.get(index - 1).maybeMerge(this)) {
            siblings.remove(index);
        }
        if (this.count > 0 && this.durationNanos < this.trace.getConfiguration().getMinFrameTime(TimeUnit.NANOSECONDS) && (this.parent == null || this.parent.removeChild(this))) {
            this.count = 0;
        }
        this.trace.onClose(this);
    }

    void addChild(@Nonnull ProfilingFrame child) {
        this.children.add(child);
        child.parent = this;
    }

    void append(@Nonnull String indent, @Nonnull StringBuilder builder) {
        double durationMillis = (double)this.durationNanos / 1000000.0;
        builder.append(indent);
        builder.append("[").append(String.format("%.1f", durationMillis)).append("ms] ");
        if (this.count > 1) {
            builder.append("[count: ").append(this.count).append(", avg: ").append(String.format("%.1f", durationMillis / (double)this.count)).append("ms] ");
        }
        if (this.startMemory != -1L) {
            builder.append("[").append(this.memoryDelta / 1024L).append("KB used] ");
            builder.append("[").append(Runtime.getRuntime().freeMemory() / 1024L).append("KB free] ");
        }
        builder.append("- ").append(this.name);
        builder.append("\n");
        String childIndent = indent + " ";
        for (ProfilingFrame child : this.children) {
            child.append(childIndent, builder);
        }
    }

    void closeAbnormally() {
        this.name = this.name + " (not closed)";
        this.close();
    }

    long getDurationNanos() {
        return this.durationNanos;
    }

    String getName() {
        return this.name;
    }

    ProfilingFrame getParent() {
        return this.parent;
    }

    boolean isPruned() {
        return this.count == 0;
    }

    int size() {
        int size = 1;
        for (ProfilingFrame child : this.children) {
            size += child.size();
        }
        return size;
    }

    private static long getUsedMemory() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    private boolean maybeMerge(ProfilingFrame other) {
        if (other.count > 0 && this.treeEquals(other)) {
            this.treeMerge(other);
            return true;
        }
        return false;
    }

    private boolean removeChild(ProfilingFrame child) {
        int index = this.children.lastIndexOf(child);
        if (index != -1) {
            this.children.remove(index);
            return true;
        }
        return false;
    }

    private boolean treeEquals(ProfilingFrame other) {
        int numberOfChildren = this.children.size();
        if (!Objects.equals(this.name, other.name) || numberOfChildren != other.children.size()) {
            return false;
        }
        for (int i = 0; i < numberOfChildren; ++i) {
            if (this.children.get(i).treeEquals(other.children.get(i))) continue;
            return false;
        }
        return true;
    }

    private void treeMerge(ProfilingFrame other) {
        ++this.count;
        --other.count;
        this.durationNanos += other.durationNanos;
        this.memoryDelta += other.memoryDelta;
        int numberOfChildren = this.children.size();
        for (int i = 0; i < numberOfChildren; ++i) {
            this.children.get(i).treeMerge(other.children.get(i));
        }
    }
}


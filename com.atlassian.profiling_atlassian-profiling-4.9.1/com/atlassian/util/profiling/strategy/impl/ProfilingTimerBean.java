/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.util.profiling.strategy.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

@Deprecated
public class ProfilingTimerBean
implements Serializable {
    List<ProfilingTimerBean> children = new ArrayList<ProfilingTimerBean>();
    int frameCount;
    boolean hasMem = false;
    ProfilingTimerBean parent = null;
    String resource;
    long startMem;
    long startTime;
    long totalMem;
    long totalTime;

    public ProfilingTimerBean(String resource) {
        this.resource = resource;
    }

    public void addChild(ProfilingTimerBean child) {
        this.children.add(child);
        child.addParent(this);
    }

    public ProfilingTimerBean getParent() {
        return this.parent;
    }

    public String getPrintable(long minTime) {
        return this.getPrintable("", minTime);
    }

    public String getResource() {
        return this.resource;
    }

    public long getTotalTime() {
        return this.totalTime;
    }

    public void setEndMem() {
        if (this.hasMem) {
            this.totalMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() - this.startMem;
        }
    }

    public void setEndTime() {
        this.totalTime = System.currentTimeMillis() - this.startTime;
    }

    public void setStartMem() {
        this.startMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        this.hasMem = true;
    }

    public void setStartTime() {
        this.startTime = System.currentTimeMillis();
    }

    protected void addParent(ProfilingTimerBean parent) {
        this.parent = parent;
    }

    protected String getPrintable(String indent, long minTime) {
        if (this.totalTime >= minTime) {
            StringBuilder builder = new StringBuilder();
            builder.append(indent);
            builder.append("[").append(this.totalTime).append("ms] ");
            if (this.hasMem) {
                builder.append("[").append(this.totalMem / 1024L).append("KB used] ");
                builder.append("[").append(Runtime.getRuntime().freeMemory() / 1024L).append("KB Free] ");
            }
            builder.append("- ").append(this.resource);
            builder.append("\n");
            for (ProfilingTimerBean aChildren : this.children) {
                builder.append(aChildren.getPrintable(indent + "  ", minTime));
            }
            return builder.toString();
        }
        return "";
    }

    int getFrameCount() {
        return this.frameCount;
    }

    void removeChild(ProfilingTimerBean child) {
        ListIterator<ProfilingTimerBean> childrenIt = this.children.listIterator(this.children.size());
        while (childrenIt.hasPrevious()) {
            ProfilingTimerBean time = childrenIt.previous();
            if (time != child) continue;
            childrenIt.remove();
            return;
        }
    }

    void setFrameCount(int frameCount) {
        this.frameCount = frameCount;
    }
}


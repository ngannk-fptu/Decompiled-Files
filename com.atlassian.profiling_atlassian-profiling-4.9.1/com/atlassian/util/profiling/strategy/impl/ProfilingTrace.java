/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.util.profiling.strategy.impl;

import com.atlassian.annotations.Internal;
import com.atlassian.util.profiling.ProfilerConfiguration;
import com.atlassian.util.profiling.strategy.impl.ProfilingFrame;
import com.atlassian.util.profiling.strategy.impl.StackProfilerStrategy;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Internal
class ProfilingTrace {
    private final StackProfilerStrategy strategy;
    private ProfilingFrame current;
    private int frameCount;

    ProfilingTrace(@Nonnull StackProfilerStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "strategy");
    }

    void closeAbnormally() {
        while (this.current != null) {
            this.current.closeAbnormally();
        }
    }

    @Nullable
    ProfilingFrame getCurrentFrame() {
        return this.current;
    }

    int getFrameCount() {
        return this.frameCount;
    }

    ProfilerConfiguration getConfiguration() {
        return this.strategy.getConfiguration();
    }

    boolean isClosed() {
        return this.current == null;
    }

    void onClose(@Nonnull ProfilingFrame frame) {
        if (this.current != Objects.requireNonNull(frame, "frame")) {
            ProfilingFrame f;
            for (f = this.current; f != null && f != frame; f = f.getParent()) {
            }
            if (f == null) {
                return;
            }
            for (f = this.current; f != null && f != frame; f = f.getParent()) {
                f.closeAbnormally();
            }
        }
        if (this.current == frame) {
            this.current = frame.getParent();
            if (frame.isPruned()) {
                this.frameCount -= frame.size();
            }
            if (this.current == null) {
                this.strategy.onClose(this, frame);
            }
        }
    }

    @Nonnull
    ProfilingFrame startFrame(@Nonnull String frameName, boolean profileMemory) {
        ProfilingFrame frame = new ProfilingFrame(this, frameName, profileMemory);
        if (this.current != null) {
            this.current.addChild(frame);
        }
        this.current = frame;
        ++this.frameCount;
        return frame;
    }
}


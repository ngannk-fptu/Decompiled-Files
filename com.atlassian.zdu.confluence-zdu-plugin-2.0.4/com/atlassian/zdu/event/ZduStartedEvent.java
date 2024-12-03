/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  javax.annotation.Nonnull
 */
package com.atlassian.zdu.event;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.zdu.event.BuildInfo;
import java.io.Serializable;
import java.util.Objects;
import javax.annotation.Nonnull;

@EventName(value="zdu.upgrade-state.start")
public class ZduStartedEvent
implements Serializable {
    private static final long serialVersionUID = 8298126973087182910L;
    private final long nodeCount;
    private final BuildInfo nodeBuild;

    public ZduStartedEvent(long nodeCount, @Nonnull BuildInfo nodeBuild) {
        this.nodeCount = nodeCount;
        this.nodeBuild = Objects.requireNonNull(nodeBuild);
    }

    public long getNodeCount() {
        return this.nodeCount;
    }

    @Nonnull
    public BuildInfo getNodeBuild() {
        return this.nodeBuild;
    }

    public String getNodeVersion() {
        return this.nodeBuild.getVersion();
    }
}


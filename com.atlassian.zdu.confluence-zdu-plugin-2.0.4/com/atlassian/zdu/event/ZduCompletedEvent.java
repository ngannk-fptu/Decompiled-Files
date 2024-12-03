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

@EventName(value="zdu.upgrade-state.complete")
public class ZduCompletedEvent
implements Serializable {
    private static final long serialVersionUID = 5795677745601043532L;
    private final long nodeCount;
    private final BuildInfo fromBuild;
    private final BuildInfo toBuild;

    public ZduCompletedEvent(long nodeCount, BuildInfo fromBuild, @Nonnull BuildInfo toBuild) {
        this.nodeCount = nodeCount;
        this.fromBuild = fromBuild;
        this.toBuild = Objects.requireNonNull(toBuild);
    }

    public long getNodeCount() {
        return this.nodeCount;
    }

    public BuildInfo getFromBuild() {
        return this.fromBuild;
    }

    public String getFromVersion() {
        return this.fromBuild == null ? null : this.fromBuild.getVersion();
    }

    @Nonnull
    public BuildInfo getToBuild() {
        return this.toBuild;
    }

    public String getToVersion() {
        return this.toBuild.getVersion();
    }
}


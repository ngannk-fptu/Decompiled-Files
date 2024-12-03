/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.ConfluenceEvent
 *  com.atlassian.confluence.event.events.cluster.ClusterEvent
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.troubleshooting.confluence.zip;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEvent;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CancelSupportZipEvent
extends ConfluenceEvent
implements ClusterEvent {
    private static final long serialVersionUID = 7901742237012831384L;
    private final String taskId;

    CancelSupportZipEvent(Object source, String taskId) {
        super(source);
        this.taskId = Objects.requireNonNull(taskId);
    }

    @Nonnull
    public String getTaskId() {
        return this.taskId;
    }
}


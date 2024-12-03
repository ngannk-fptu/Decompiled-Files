/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.cluster.event;

import java.util.UUID;

public interface TopicEvent {
    public UUID getId();

    public Object getPayload();
}


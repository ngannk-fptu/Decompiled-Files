/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 */
package com.atlassian.crowd.core.event;

import com.atlassian.event.api.EventPublisher;
import java.util.Collection;

public interface MultiEventPublisher
extends EventPublisher {
    public void publishAll(Collection<Object> var1);
}


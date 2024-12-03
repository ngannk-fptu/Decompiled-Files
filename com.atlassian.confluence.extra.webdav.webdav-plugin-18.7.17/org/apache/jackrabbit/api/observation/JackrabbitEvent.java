/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.api.observation;

import javax.jcr.observation.Event;

public interface JackrabbitEvent
extends Event {
    public boolean isExternal();
}


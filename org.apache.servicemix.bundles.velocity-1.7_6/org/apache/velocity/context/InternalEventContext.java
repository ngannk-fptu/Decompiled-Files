/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.context;

import org.apache.velocity.app.event.EventCartridge;

public interface InternalEventContext {
    public EventCartridge attachEventCartridge(EventCartridge var1);

    public EventCartridge getEventCartridge();
}


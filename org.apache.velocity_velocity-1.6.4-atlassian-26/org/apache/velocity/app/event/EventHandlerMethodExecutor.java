/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.app.event;

import org.apache.velocity.app.event.EventHandler;

public interface EventHandlerMethodExecutor {
    public void execute(EventHandler var1) throws Exception;

    public boolean isDone();

    public Object getReturnValue();
}


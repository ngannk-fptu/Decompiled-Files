/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.event.config;

import com.atlassian.event.spi.ListenerHandler;
import java.util.List;
import javax.annotation.Nonnull;

public interface ListenerHandlersConfiguration {
    @Nonnull
    public List<ListenerHandler> getListenerHandlers();
}


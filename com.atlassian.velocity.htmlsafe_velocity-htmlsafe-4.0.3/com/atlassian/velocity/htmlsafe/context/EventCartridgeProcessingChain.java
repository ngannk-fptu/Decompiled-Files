/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.apache.velocity.app.event.EventCartridge
 */
package com.atlassian.velocity.htmlsafe.context;

import com.atlassian.velocity.htmlsafe.context.EventCartridgeProcessor;
import com.google.common.base.Preconditions;
import org.apache.velocity.app.event.EventCartridge;

public final class EventCartridgeProcessingChain
implements EventCartridgeProcessor {
    private final EventCartridgeProcessor[] processors;

    public EventCartridgeProcessingChain(EventCartridgeProcessor ... processors) {
        Preconditions.checkNotNull((Object)processors, (Object)"processors must not be null");
        this.processors = (EventCartridgeProcessor[])processors.clone();
    }

    @Override
    public void processCartridge(EventCartridge cartridge) {
        for (EventCartridgeProcessor processor : this.processors) {
            processor.processCartridge(cartridge);
        }
    }
}


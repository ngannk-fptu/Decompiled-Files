/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.app.event.EventCartridge
 */
package com.atlassian.velocity.htmlsafe.context;

import com.atlassian.velocity.htmlsafe.context.EventCartridgeProcessor;
import org.apache.velocity.app.event.EventCartridge;

public final class NoOpEventCartridgeProcessor
implements EventCartridgeProcessor {
    @Override
    public void processCartridge(EventCartridge cartridge) {
    }
}


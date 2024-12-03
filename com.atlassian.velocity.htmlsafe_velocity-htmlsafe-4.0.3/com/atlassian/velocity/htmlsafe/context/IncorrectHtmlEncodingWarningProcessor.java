/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.app.event.EventCartridge
 *  org.apache.velocity.app.event.EventHandler
 */
package com.atlassian.velocity.htmlsafe.context;

import com.atlassian.velocity.htmlsafe.PossibleIncorrectHtmlEncodingEventHandler;
import com.atlassian.velocity.htmlsafe.context.EventCartridgeProcessor;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.app.event.EventHandler;

public final class IncorrectHtmlEncodingWarningProcessor
implements EventCartridgeProcessor {
    @Override
    public void processCartridge(EventCartridge cartridge) {
        if (PossibleIncorrectHtmlEncodingEventHandler.isLoggingEnabled()) {
            cartridge.addEventHandler((EventHandler)new PossibleIncorrectHtmlEncodingEventHandler());
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.app.event.EventCartridge
 *  org.apache.velocity.app.event.ReferenceInsertionEventHandler
 */
package com.atlassian.velocity.htmlsafe.context;

import com.atlassian.velocity.htmlsafe.PolicyBasedReferenceInsertionHandler;
import com.atlassian.velocity.htmlsafe.ReferenceInsertionPolicy;
import com.atlassian.velocity.htmlsafe.context.EventCartridgeProcessor;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;

public final class InsertionPolicyCartridgeProcessor
implements EventCartridgeProcessor {
    private final ReferenceInsertionEventHandler REFERENCE_INSERTION_EVENT_HANDLER;

    public InsertionPolicyCartridgeProcessor(ReferenceInsertionPolicy encodingPolicy) {
        this.REFERENCE_INSERTION_EVENT_HANDLER = new PolicyBasedReferenceInsertionHandler(encodingPolicy);
    }

    @Override
    public void processCartridge(EventCartridge cartridge) {
        cartridge.addReferenceInsertionEventHandler(this.REFERENCE_INSERTION_EVENT_HANDLER);
    }
}


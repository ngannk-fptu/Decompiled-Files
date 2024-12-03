/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.app.event.ReferenceInsertionEventHandler
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util.velocity.debug;

import org.apache.velocity.app.event.ReferenceInsertionEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated(forRemoval=true)
public class DebugReferenceInsertionEventHandler
implements ReferenceInsertionEventHandler {
    private static final Logger log = LoggerFactory.getLogger(DebugReferenceInsertionEventHandler.class);

    public Object referenceInsert(String reference, Object value) {
        log.debug("resolving reference [{}]", (Object)reference);
        return value;
    }
}


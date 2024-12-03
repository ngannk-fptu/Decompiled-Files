/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.event.events.user;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.event.api.AsynchronousPreferred;

@AsynchronousPreferred
@Internal
public class UserVerificationTokenCleanupEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = -6434934321611407966L;

    public UserVerificationTokenCleanupEvent(Object src) {
        super(src);
    }
}


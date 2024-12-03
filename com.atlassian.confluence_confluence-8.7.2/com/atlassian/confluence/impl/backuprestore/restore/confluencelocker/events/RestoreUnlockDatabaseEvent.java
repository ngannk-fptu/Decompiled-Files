/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.JobScope
 */
package com.atlassian.confluence.impl.backuprestore.restore.confluencelocker.events;

import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.confluence.impl.backuprestore.restore.confluencelocker.events.AbstractRestoreEvent;

public class RestoreUnlockDatabaseEvent
extends AbstractRestoreEvent {
    private static final long serialVersionUID = 3127595495883085594L;

    public RestoreUnlockDatabaseEvent(Object src, JobScope jobScope) {
        super(src, jobScope);
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.JobScope
 */
package com.atlassian.confluence.impl.backuprestore.restore.confluencelocker.events;

import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.confluence.impl.backuprestore.restore.confluencelocker.events.AbstractRestoreEvent;

public class RestoreLockDatabaseEvent
extends AbstractRestoreEvent {
    private static final long serialVersionUID = 7030747075145247686L;

    public RestoreLockDatabaseEvent(Object src, JobScope jobScope) {
        super(src, jobScope);
    }
}


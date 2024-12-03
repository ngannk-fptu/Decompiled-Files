/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.JobScope
 */
package com.atlassian.confluence.impl.backuprestore.restore.confluencelocker.events;

import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.confluence.impl.backuprestore.restore.confluencelocker.events.AbstractRestoreEvent;

public class RestoreSucceededEvent
extends AbstractRestoreEvent {
    private static final long serialVersionUID = -5941719015769694834L;

    public RestoreSucceededEvent(Object src, JobScope jobScope) {
        super(src, jobScope);
    }
}


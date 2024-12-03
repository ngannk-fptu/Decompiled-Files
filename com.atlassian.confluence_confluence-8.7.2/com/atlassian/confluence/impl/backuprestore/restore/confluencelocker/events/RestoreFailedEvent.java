/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.JobScope
 */
package com.atlassian.confluence.impl.backuprestore.restore.confluencelocker.events;

import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.confluence.impl.backuprestore.restore.confluencelocker.events.AbstractRestoreEvent;

public class RestoreFailedEvent
extends AbstractRestoreEvent {
    private static final long serialVersionUID = -1402103570927951517L;
    private final String errorMessage;
    private final boolean isDisplayJohnson;

    public RestoreFailedEvent(Object src, JobScope jobScope, String errorMessage, boolean isDisplayJohnson) {
        super(src, jobScope);
        this.errorMessage = errorMessage;
        this.isDisplayJohnson = isDisplayJohnson;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public boolean isDisplayJohnson() {
        return this.isDisplayJohnson;
    }
}


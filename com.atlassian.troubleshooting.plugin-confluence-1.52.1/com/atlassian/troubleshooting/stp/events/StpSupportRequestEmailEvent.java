/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.troubleshooting.stp.events;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.troubleshooting.stp.events.SupportZipOptionsAwareEvent;
import java.util.Collection;

public class StpSupportRequestEmailEvent
extends SupportZipOptionsAwareEvent {
    private static final String BASE_EVENT_NAME = "stp.create.support.request.send";
    private final boolean successful;
    private final Boolean limitFileSize;
    private final Integer fileConstraintSize;
    private final Integer fileConstraintLastModified;

    public StpSupportRequestEmailEvent(boolean successful, Collection<String> supportZipOptions, Boolean limitFileSize, Integer fileConstraintSize, Integer fileConstraintLastModified) {
        super(supportZipOptions);
        this.successful = successful;
        this.limitFileSize = limitFileSize;
        this.fileConstraintSize = fileConstraintSize;
        this.fileConstraintLastModified = fileConstraintLastModified;
    }

    @EventName
    public String buildEventName() {
        return String.format("%s.%s", BASE_EVENT_NAME, this.successful ? "success" : "failed");
    }

    public Boolean getLimitFileSize() {
        return this.limitFileSize;
    }

    public Integer getFileConstraintSize() {
        return this.fileConstraintSize;
    }

    public Integer getFileConstraintLastModified() {
        return this.fileConstraintLastModified;
    }
}


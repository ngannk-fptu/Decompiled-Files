/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.ConfluenceEvent
 *  com.atlassian.confluence.event.events.cluster.ClusterEvent
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.confluence.zip;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEvent;
import com.atlassian.troubleshooting.stp.zip.SupportZipRequest;
import java.util.Objects;
import javax.annotation.Nonnull;

public class CreateSupportZipEvent
extends ConfluenceEvent
implements ClusterEvent {
    private static final long serialVersionUID = 9159011471372384728L;
    private final SupportZipRequest supportZipRequest;

    CreateSupportZipEvent(@Nonnull Object source, @Nonnull SupportZipRequest supportZipRequest) {
        super(source);
        this.supportZipRequest = Objects.requireNonNull(supportZipRequest);
    }

    @Nonnull
    public SupportZipRequest getSupportZipRequest() {
        return this.supportZipRequest;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.confluence.zip;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.troubleshooting.confluence.zip.CancelSupportZipEvent;
import com.atlassian.troubleshooting.confluence.zip.CreateSupportZipEvent;
import com.atlassian.troubleshooting.stp.zip.ClusteredSupportZipService;
import com.atlassian.troubleshooting.stp.zip.SupportZipRequest;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.springframework.beans.factory.annotation.Autowired;

@ParametersAreNonnullByDefault
public class ConfluenceClusteredSupportZipService
implements ClusteredSupportZipService {
    private final EventPublisher eventPublisher;

    @Autowired
    public ConfluenceClusteredSupportZipService(EventPublisher eventPublisher) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    @Nonnull
    public Optional<String> requestSupportZipCreationOnOtherNodes(SupportZipRequest supportZipRequest) {
        this.eventPublisher.publish((Object)new CreateSupportZipEvent(this, supportZipRequest));
        return Optional.empty();
    }

    @Override
    public void requestSupportZipCancellationOnOtherNodes(String taskId) {
        this.eventPublisher.publish((Object)new CancelSupportZipEvent(this, taskId));
    }
}


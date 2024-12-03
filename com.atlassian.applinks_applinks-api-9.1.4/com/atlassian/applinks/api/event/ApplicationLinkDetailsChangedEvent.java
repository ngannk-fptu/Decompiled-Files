/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.api.event;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.event.ApplicationLinkEvent;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;

public class ApplicationLinkDetailsChangedEvent
extends ApplicationLinkEvent {
    private final ApplicationLink oldApplicationLink;

    @Deprecated
    public ApplicationLinkDetailsChangedEvent(ApplicationLink applicationLink) {
        this(applicationLink, null);
    }

    public ApplicationLinkDetailsChangedEvent(@Nonnull ApplicationLink applicationLink, ApplicationLink oldApplicationLink) {
        super(Objects.requireNonNull(applicationLink, "applicationLink"));
        this.oldApplicationLink = oldApplicationLink;
    }

    @Nonnull
    public Optional<ApplicationLink> getOldApplicationLink() {
        return Optional.ofNullable(this.oldApplicationLink);
    }
}


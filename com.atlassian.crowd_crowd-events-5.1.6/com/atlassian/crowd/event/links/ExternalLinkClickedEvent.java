/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.crowd.event.links;

import com.atlassian.crowd.event.EnumBasedEvent;
import com.google.common.collect.ImmutableMap;

public class ExternalLinkClickedEvent
extends EnumBasedEvent<TargetLink> {
    public static ImmutableMap<TargetLink, ExternalLinkClickedEvent> BY_TARGET = ExternalLinkClickedEvent.createMapByEnum((Enum[])TargetLink.values(), ExternalLinkClickedEvent::new);

    public static ExternalLinkClickedEvent from(TargetLink targetLink) {
        return (ExternalLinkClickedEvent)BY_TARGET.get((Object)targetLink);
    }

    private ExternalLinkClickedEvent(TargetLink targetLink) {
        super(targetLink);
    }

    public TargetLink getTargetLink() {
        return (TargetLink)this.data;
    }

    public static enum TargetLink {
        SETUP_PANOPTICON,
        LICENSING_GETTING_STARTED;

    }
}


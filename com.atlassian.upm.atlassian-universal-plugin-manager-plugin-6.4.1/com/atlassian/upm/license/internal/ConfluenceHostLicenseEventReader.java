/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.admin.LicenceUpdatedEvent
 */
package com.atlassian.upm.license.internal;

import com.atlassian.confluence.event.events.admin.LicenceUpdatedEvent;
import com.atlassian.upm.license.internal.HostLicenseEventReader;
import java.util.Objects;

public class ConfluenceHostLicenseEventReader
implements HostLicenseEventReader {
    @Override
    public boolean isHostLicenseUpdated(Object event) {
        Objects.requireNonNull(event, "event");
        return LicenceUpdatedEvent.class.isAssignableFrom(event.getClass());
    }
}


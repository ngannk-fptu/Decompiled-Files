/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bitbucket.event.license.LicenseChangedEvent
 */
package com.atlassian.upm.license.internal;

import com.atlassian.bitbucket.event.license.LicenseChangedEvent;
import com.atlassian.upm.license.internal.HostLicenseEventReader;
import java.util.Objects;

public class BitbucketHostLicenseEventReader
implements HostLicenseEventReader {
    @Override
    public boolean isHostLicenseUpdated(Object event) {
        Objects.requireNonNull(event, "event");
        return LicenseChangedEvent.class.isAssignableFrom(event.getClass());
    }
}


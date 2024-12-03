/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  org.joda.time.DateTimeZone
 */
package com.atlassian.upm.license.internal;

import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.atlassian.upm.license.internal.impl.DefaultLicenseDateFormatter;
import java.util.TimeZone;
import org.joda.time.DateTimeZone;

public class BitbucketLicenseDateFormatter
extends DefaultLicenseDateFormatter {
    private final TimeZoneManager timeZoneManager;

    public BitbucketLicenseDateFormatter(TimeZoneManager timeZoneManager) {
        this.timeZoneManager = timeZoneManager;
    }

    @Override
    protected DateTimeZone getUserTimeZone() {
        return DateTimeZone.forTimeZone((TimeZone)this.timeZoneManager.getUserTimeZone());
    }
}


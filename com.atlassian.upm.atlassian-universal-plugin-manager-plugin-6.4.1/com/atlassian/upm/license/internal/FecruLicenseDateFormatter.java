/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.cenqua.fisheye.AppConfig
 *  org.joda.time.DateTimeZone
 */
package com.atlassian.upm.license.internal;

import com.atlassian.upm.license.internal.impl.DefaultLicenseDateFormatter;
import com.cenqua.fisheye.AppConfig;
import java.util.TimeZone;
import org.joda.time.DateTimeZone;

public class FecruLicenseDateFormatter
extends DefaultLicenseDateFormatter {
    @Override
    protected DateTimeZone getUserTimeZone() {
        try {
            return DateTimeZone.forTimeZone((TimeZone)AppConfig.getsConfig().getTimezone());
        }
        catch (IllegalArgumentException e) {
            return DateTimeZone.getDefault();
        }
    }
}


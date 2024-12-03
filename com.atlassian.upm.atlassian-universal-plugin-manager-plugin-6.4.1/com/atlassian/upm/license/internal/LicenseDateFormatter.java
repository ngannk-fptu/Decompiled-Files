/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTime
 */
package com.atlassian.upm.license.internal;

import org.joda.time.DateTime;

public interface LicenseDateFormatter {
    public String formatDateTime(DateTime var1);

    public String formatDate(DateTime var1);
}


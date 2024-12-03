/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTime
 */
package com.atlassian.upm.mac;

import java.util.Objects;
import org.joda.time.DateTime;

public class LicensesSummary {
    private final int count;
    private final DateTime lastModified;

    public LicensesSummary(int count, DateTime lastModified) {
        this.count = count;
        this.lastModified = Objects.requireNonNull(lastModified);
    }

    public int getCount() {
        return this.count;
    }

    public DateTime getLastModified() {
        return this.lastModified;
    }

    public boolean equals(Object other) {
        if (other instanceof LicensesSummary) {
            LicensesSummary ls = (LicensesSummary)other;
            return this.count == ls.count && this.lastModified.equals((Object)ls.lastModified);
        }
        return false;
    }

    public int hashCode() {
        return this.lastModified.hashCode() + this.count;
    }
}


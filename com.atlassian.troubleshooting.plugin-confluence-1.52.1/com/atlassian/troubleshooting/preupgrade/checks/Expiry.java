/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.troubleshooting.preupgrade.checks;

import java.util.Date;
import javax.annotation.Nullable;

public class Expiry {
    private static final Expiry PERPETUAL = new Expiry(null);
    private final Date expiryDate;

    private Expiry(@Nullable Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public static Expiry fromDate(@Nullable Date date) {
        return date == null ? PERPETUAL : new Expiry(date);
    }

    public Expiry max(Expiry other) {
        if (this == PERPETUAL || other == PERPETUAL) {
            return PERPETUAL;
        }
        return this.expiryDate.compareTo(other.expiryDate) < 0 ? other : this;
    }

    public boolean isBeforeExpiry(Date date) {
        return this.expiryDate == null || this.expiryDate.compareTo(date) > 0;
    }

    public String toString() {
        return this.expiryDate != null ? String.format("%TF", this.expiryDate) : "(perpetual license)";
    }
}


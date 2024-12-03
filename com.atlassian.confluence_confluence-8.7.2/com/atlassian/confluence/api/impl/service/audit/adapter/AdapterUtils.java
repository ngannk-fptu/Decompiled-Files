/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.audit.RetentionPeriod
 */
package com.atlassian.confluence.api.impl.service.audit.adapter;

import com.atlassian.confluence.api.model.audit.RetentionPeriod;
import java.time.Period;

@Deprecated
public class AdapterUtils {
    public static Period toPeriod(RetentionPeriod retentionPeriod) {
        switch (retentionPeriod.getUnits()) {
            case DAYS: {
                return Period.ofDays(retentionPeriod.getNumber());
            }
            case MONTHS: {
                return Period.ofMonths(retentionPeriod.getNumber());
            }
            case YEARS: {
                return Period.ofYears(retentionPeriod.getNumber());
            }
        }
        throw new IllegalArgumentException("Not supported period unit: " + retentionPeriod.getUnits());
    }
}


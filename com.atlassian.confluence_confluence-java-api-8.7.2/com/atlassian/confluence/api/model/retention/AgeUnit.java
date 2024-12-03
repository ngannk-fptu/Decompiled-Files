/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.api.model.retention;

import com.atlassian.annotations.ExperimentalApi;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@ExperimentalApi
public enum AgeUnit implements Serializable
{
    DAYS("audit.logging.summary.global.retention.rules.age.day", "audit.logging.summary.global.retention.rules.age.days", "days"),
    MONTHS("audit.logging.summary.global.retention.rules.age.month", "audit.logging.summary.global.retention.rules.age.months", "months"),
    YEARS("audit.logging.summary.global.retention.rules.age.year", "audit.logging.summary.global.retention.rules.age.years", "years");

    private static final Map<String, AgeUnit> lookupMap;
    private final String singleLabel;
    private final String pluralLabel;
    private final String analyticsLabel;

    private AgeUnit(String singleLabel, String pluralLabel, String analyticsLabel) {
        this.singleLabel = singleLabel;
        this.pluralLabel = pluralLabel;
        this.analyticsLabel = analyticsLabel;
    }

    public String getSingleLabel() {
        return this.singleLabel;
    }

    public String getPluralLabel() {
        return this.pluralLabel;
    }

    public String getAnalyticsLabel() {
        return this.analyticsLabel;
    }

    public static AgeUnit getAgeUnit(String ageUnitValue) {
        return lookupMap.getOrDefault(ageUnitValue, DAYS);
    }

    static {
        lookupMap = new HashMap<String, AgeUnit>();
        for (AgeUnit unit : AgeUnit.values()) {
            lookupMap.put(unit.toString(), unit);
        }
    }
}


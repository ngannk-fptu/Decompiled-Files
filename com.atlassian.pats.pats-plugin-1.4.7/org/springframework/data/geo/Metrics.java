/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.geo;

import org.springframework.data.geo.Metric;

public enum Metrics implements Metric
{
    KILOMETERS(6378.137, "km"),
    MILES(3963.191, "mi"),
    NEUTRAL(1.0, "");

    private final double multiplier;
    private final String abbreviation;

    private Metrics(double multiplier, String abbreviation) {
        this.multiplier = multiplier;
        this.abbreviation = abbreviation;
    }

    @Override
    public double getMultiplier() {
        return this.multiplier;
    }

    @Override
    public String getAbbreviation() {
        return this.abbreviation;
    }
}


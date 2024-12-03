/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.data.geo;

import org.springframework.data.geo.Metric;
import org.springframework.util.Assert;

public class CustomMetric
implements Metric {
    private static final long serialVersionUID = -2972074177454114228L;
    private final double multiplier;
    private final String abbreviation;

    public CustomMetric(double multiplier) {
        this(multiplier, "");
    }

    public CustomMetric(double multiplier, String abbreviation) {
        Assert.notNull((Object)abbreviation, (String)"Abbreviation must not be null!");
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


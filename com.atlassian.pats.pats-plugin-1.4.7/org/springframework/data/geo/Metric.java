/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.geo;

import java.io.Serializable;

public interface Metric
extends Serializable {
    public double getMultiplier();

    public String getAbbreviation();
}


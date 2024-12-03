/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc.spatialdatatypes;

import com.microsoft.sqlserver.jdbc.spatialdatatypes.WKBPoint;

public class WKBLinearRing {
    private final int numPoints;
    private final WKBPoint[] wkbPoints;

    public WKBLinearRing(int numPoints, WKBPoint[] wkbPoints) {
        this.numPoints = numPoints;
        this.wkbPoints = wkbPoints;
    }

    public int getNumPoints() {
        return this.numPoints;
    }

    public WKBPoint[] getWkbPoints() {
        return this.wkbPoints;
    }
}


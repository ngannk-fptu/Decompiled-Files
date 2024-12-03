/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.sl.draw.geom;

import org.apache.poi.sl.draw.geom.Context;

public interface Formula {
    public static final double OOXML_DEGREE = 60000.0;

    public double evaluate(Context var1);
}


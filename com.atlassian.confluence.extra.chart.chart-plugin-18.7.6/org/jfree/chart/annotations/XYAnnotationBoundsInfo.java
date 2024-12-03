/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.annotations;

import org.jfree.data.Range;

public interface XYAnnotationBoundsInfo {
    public boolean getIncludeInDataBounds();

    public Range getXRange();

    public Range getYRange();
}


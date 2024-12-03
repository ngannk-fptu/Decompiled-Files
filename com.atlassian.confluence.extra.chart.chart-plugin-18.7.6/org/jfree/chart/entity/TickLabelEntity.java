/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.entity;

import java.awt.Shape;
import java.io.Serializable;
import org.jfree.chart.entity.ChartEntity;

public class TickLabelEntity
extends ChartEntity
implements Cloneable,
Serializable {
    private static final long serialVersionUID = 681583956588092095L;

    public TickLabelEntity(Shape area, String toolTipText, String urlText) {
        super(area, toolTipText, urlText);
    }
}


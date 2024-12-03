/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlUnionImpl;
import org.openxmlformats.schemas.drawingml.x2006.chart.STSecondPieSize;
import org.openxmlformats.schemas.drawingml.x2006.chart.STSecondPieSizePercent;
import org.openxmlformats.schemas.drawingml.x2006.chart.STSecondPieSizeUShort;

public class STSecondPieSizeImpl
extends XmlUnionImpl
implements STSecondPieSize,
STSecondPieSizePercent,
STSecondPieSizeUShort {
    private static final long serialVersionUID = 1L;

    public STSecondPieSizeImpl(SchemaType sType) {
        super(sType, false);
    }

    protected STSecondPieSizeImpl(SchemaType sType, boolean b) {
        super(sType, b);
    }
}


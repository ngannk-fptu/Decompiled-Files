/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlUnionImpl;
import org.openxmlformats.schemas.drawingml.x2006.chart.STGapAmount;
import org.openxmlformats.schemas.drawingml.x2006.chart.STGapAmountPercent;
import org.openxmlformats.schemas.drawingml.x2006.chart.STGapAmountUShort;

public class STGapAmountImpl
extends XmlUnionImpl
implements STGapAmount,
STGapAmountPercent,
STGapAmountUShort {
    private static final long serialVersionUID = 1L;

    public STGapAmountImpl(SchemaType sType) {
        super(sType, false);
    }

    protected STGapAmountImpl(SchemaType sType, boolean b) {
        super(sType, b);
    }
}


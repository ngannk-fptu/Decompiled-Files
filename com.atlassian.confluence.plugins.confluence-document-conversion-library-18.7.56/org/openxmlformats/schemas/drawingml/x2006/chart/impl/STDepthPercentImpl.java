/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlUnionImpl;
import org.openxmlformats.schemas.drawingml.x2006.chart.STDepthPercent;
import org.openxmlformats.schemas.drawingml.x2006.chart.STDepthPercentUShort;
import org.openxmlformats.schemas.drawingml.x2006.chart.STDepthPercentWithSymbol;

public class STDepthPercentImpl
extends XmlUnionImpl
implements STDepthPercent,
STDepthPercentWithSymbol,
STDepthPercentUShort {
    private static final long serialVersionUID = 1L;

    public STDepthPercentImpl(SchemaType sType) {
        super(sType, false);
    }

    protected STDepthPercentImpl(SchemaType sType, boolean b) {
        super(sType, b);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlUnionImpl;
import org.openxmlformats.schemas.drawingml.x2006.chart.STHPercent;
import org.openxmlformats.schemas.drawingml.x2006.chart.STHPercentUShort;
import org.openxmlformats.schemas.drawingml.x2006.chart.STHPercentWithSymbol;

public class STHPercentImpl
extends XmlUnionImpl
implements STHPercent,
STHPercentWithSymbol,
STHPercentUShort {
    private static final long serialVersionUID = 1L;

    public STHPercentImpl(SchemaType sType) {
        super(sType, false);
    }

    protected STHPercentImpl(SchemaType sType, boolean b) {
        super(sType, b);
    }
}


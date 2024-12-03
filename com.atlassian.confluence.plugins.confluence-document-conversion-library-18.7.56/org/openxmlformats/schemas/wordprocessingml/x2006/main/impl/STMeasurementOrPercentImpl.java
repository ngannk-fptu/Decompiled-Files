/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlUnionImpl;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STUniversalMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDecimalNumberOrPercent;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMeasurementOrPercent;

public class STMeasurementOrPercentImpl
extends XmlUnionImpl
implements STMeasurementOrPercent,
STDecimalNumberOrPercent,
STUniversalMeasure {
    private static final long serialVersionUID = 1L;

    public STMeasurementOrPercentImpl(SchemaType sType) {
        super(sType, false);
    }

    protected STMeasurementOrPercentImpl(SchemaType sType, boolean b) {
        super(sType, b);
    }
}


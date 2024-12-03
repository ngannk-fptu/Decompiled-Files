/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlUnionImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.STPositiveFixedPercentage;
import org.openxmlformats.schemas.drawingml.x2006.main.STPositiveFixedPercentageDecimal;

public class STPositiveFixedPercentageImpl
extends XmlUnionImpl
implements STPositiveFixedPercentage,
STPositiveFixedPercentageDecimal,
org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STPositiveFixedPercentage {
    private static final long serialVersionUID = 1L;

    public STPositiveFixedPercentageImpl(SchemaType sType) {
        super(sType, false);
    }

    protected STPositiveFixedPercentageImpl(SchemaType sType, boolean b) {
        super(sType, b);
    }
}


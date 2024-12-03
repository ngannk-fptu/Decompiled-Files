/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlUnionImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.STFixedPercentage;
import org.openxmlformats.schemas.drawingml.x2006.main.STFixedPercentageDecimal;

public class STFixedPercentageImpl
extends XmlUnionImpl
implements STFixedPercentage,
STFixedPercentageDecimal,
org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STFixedPercentage {
    private static final long serialVersionUID = 1L;

    public STFixedPercentageImpl(SchemaType sType) {
        super(sType, false);
    }

    protected STFixedPercentageImpl(SchemaType sType, boolean b) {
        super(sType, b);
    }
}


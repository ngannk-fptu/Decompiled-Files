/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlUnionImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.STPositivePercentage;
import org.openxmlformats.schemas.drawingml.x2006.main.STPositivePercentageDecimal;

public class STPositivePercentageImpl
extends XmlUnionImpl
implements STPositivePercentage,
STPositivePercentageDecimal,
org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STPositivePercentage {
    private static final long serialVersionUID = 1L;

    public STPositivePercentageImpl(SchemaType sType) {
        super(sType, false);
    }

    protected STPositivePercentageImpl(SchemaType sType, boolean b) {
        super(sType, b);
    }
}


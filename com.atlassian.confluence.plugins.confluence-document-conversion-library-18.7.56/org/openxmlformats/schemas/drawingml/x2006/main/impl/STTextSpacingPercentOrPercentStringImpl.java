/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlUnionImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextSpacingPercent;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextSpacingPercentOrPercentString;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STPercentage;

public class STTextSpacingPercentOrPercentStringImpl
extends XmlUnionImpl
implements STTextSpacingPercentOrPercentString,
STTextSpacingPercent,
STPercentage {
    private static final long serialVersionUID = 1L;

    public STTextSpacingPercentOrPercentStringImpl(SchemaType sType) {
        super(sType, false);
    }

    protected STTextSpacingPercentOrPercentStringImpl(SchemaType sType, boolean b) {
        super(sType, b);
    }
}


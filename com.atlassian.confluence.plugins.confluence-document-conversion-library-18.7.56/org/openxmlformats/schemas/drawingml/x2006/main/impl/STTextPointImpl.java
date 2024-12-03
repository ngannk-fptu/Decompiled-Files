/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlUnionImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextPoint;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextPointUnqualified;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STUniversalMeasure;

public class STTextPointImpl
extends XmlUnionImpl
implements STTextPoint,
STTextPointUnqualified,
STUniversalMeasure {
    private static final long serialVersionUID = 1L;

    public STTextPointImpl(SchemaType sType) {
        super(sType, false);
    }

    protected STTextPointImpl(SchemaType sType, boolean b) {
        super(sType, b);
    }
}


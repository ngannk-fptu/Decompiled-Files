/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlUnionImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.STCoordinate;
import org.openxmlformats.schemas.drawingml.x2006.main.STCoordinateUnqualified;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STUniversalMeasure;

public class STCoordinateImpl
extends XmlUnionImpl
implements STCoordinate,
STCoordinateUnqualified,
STUniversalMeasure {
    private static final long serialVersionUID = 1L;

    public STCoordinateImpl(SchemaType sType) {
        super(sType, false);
    }

    protected STCoordinateImpl(SchemaType sType, boolean b) {
        super(sType, b);
    }
}


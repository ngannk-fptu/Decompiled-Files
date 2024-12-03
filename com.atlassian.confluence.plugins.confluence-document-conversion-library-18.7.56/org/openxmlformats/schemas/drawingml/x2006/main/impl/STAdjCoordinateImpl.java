/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlUnionImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.STAdjCoordinate;
import org.openxmlformats.schemas.drawingml.x2006.main.STCoordinate;
import org.openxmlformats.schemas.drawingml.x2006.main.STGeomGuideName;

public class STAdjCoordinateImpl
extends XmlUnionImpl
implements STAdjCoordinate,
STCoordinate,
STGeomGuideName {
    private static final long serialVersionUID = 1L;

    public STAdjCoordinateImpl(SchemaType sType) {
        super(sType, false);
    }

    protected STAdjCoordinateImpl(SchemaType sType, boolean b) {
        super(sType, b);
    }
}


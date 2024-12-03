/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlUnionImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.STAdjAngle;
import org.openxmlformats.schemas.drawingml.x2006.main.STAngle;
import org.openxmlformats.schemas.drawingml.x2006.main.STGeomGuideName;

public class STAdjAngleImpl
extends XmlUnionImpl
implements STAdjAngle,
STAngle,
STGeomGuideName {
    private static final long serialVersionUID = 1L;

    public STAdjAngleImpl(SchemaType sType) {
        super(sType, false);
    }

    protected STAdjAngleImpl(SchemaType sType, boolean b) {
        super(sType, b);
    }
}


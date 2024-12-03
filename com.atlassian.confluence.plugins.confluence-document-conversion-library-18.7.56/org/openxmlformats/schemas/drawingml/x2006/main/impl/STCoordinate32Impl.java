/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlUnionImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.STCoordinate32;
import org.openxmlformats.schemas.drawingml.x2006.main.STCoordinate32Unqualified;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STUniversalMeasure;

public class STCoordinate32Impl
extends XmlUnionImpl
implements STCoordinate32,
STCoordinate32Unqualified,
STUniversalMeasure {
    private static final long serialVersionUID = 1L;

    public STCoordinate32Impl(SchemaType sType) {
        super(sType, false);
    }

    protected STCoordinate32Impl(SchemaType sType, boolean b) {
        super(sType, b);
    }
}


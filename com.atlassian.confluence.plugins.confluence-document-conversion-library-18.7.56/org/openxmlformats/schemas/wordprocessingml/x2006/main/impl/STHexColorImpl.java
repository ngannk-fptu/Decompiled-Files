/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlUnionImpl;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STHexColorRGB;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHexColor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHexColorAuto;

public class STHexColorImpl
extends XmlUnionImpl
implements STHexColor,
STHexColorAuto,
STHexColorRGB {
    private static final long serialVersionUID = 1L;

    public STHexColorImpl(SchemaType sType) {
        super(sType, false);
    }

    protected STHexColorImpl(SchemaType sType, boolean b) {
        super(sType, b);
    }
}


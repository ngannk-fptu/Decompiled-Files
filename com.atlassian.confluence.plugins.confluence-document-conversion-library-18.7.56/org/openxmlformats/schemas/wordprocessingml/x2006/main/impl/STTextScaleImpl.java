/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlUnionImpl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTextScale;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTextScaleDecimal;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTextScalePercent;

public class STTextScaleImpl
extends XmlUnionImpl
implements STTextScale,
STTextScalePercent,
STTextScaleDecimal {
    private static final long serialVersionUID = 1L;

    public STTextScaleImpl(SchemaType sType) {
        super(sType, false);
    }

    protected STTextScaleImpl(SchemaType sType, boolean b) {
        super(sType, b);
    }
}


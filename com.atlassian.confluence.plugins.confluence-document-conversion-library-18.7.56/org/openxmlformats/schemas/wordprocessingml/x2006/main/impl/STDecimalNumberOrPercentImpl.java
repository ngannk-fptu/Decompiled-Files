/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.XmlUnionImpl;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STPercentage;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDecimalNumberOrPercent;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STUnqualifiedPercentage;

public class STDecimalNumberOrPercentImpl
extends XmlUnionImpl
implements STDecimalNumberOrPercent,
STUnqualifiedPercentage,
STPercentage {
    private static final long serialVersionUID = 1L;

    public STDecimalNumberOrPercentImpl(SchemaType sType) {
        super(sType, false);
    }

    protected STDecimalNumberOrPercentImpl(SchemaType sType, boolean b) {
        super(sType, b);
    }
}


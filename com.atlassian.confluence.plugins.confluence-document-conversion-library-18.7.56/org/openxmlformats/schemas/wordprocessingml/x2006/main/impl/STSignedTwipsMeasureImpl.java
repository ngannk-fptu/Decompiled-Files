/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlInteger;
import org.apache.xmlbeans.impl.values.XmlUnionImpl;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STUniversalMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STSignedTwipsMeasure;

public class STSignedTwipsMeasureImpl
extends XmlUnionImpl
implements STSignedTwipsMeasure,
XmlInteger,
STUniversalMeasure {
    private static final long serialVersionUID = 1L;

    public STSignedTwipsMeasureImpl(SchemaType sType) {
        super(sType, false);
    }

    protected STSignedTwipsMeasureImpl(SchemaType sType, boolean b) {
        super(sType, b);
    }
}


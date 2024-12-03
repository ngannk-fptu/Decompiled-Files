/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlInteger;
import org.apache.xmlbeans.impl.values.XmlUnionImpl;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STUniversalMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STSignedHpsMeasure;

public class STSignedHpsMeasureImpl
extends XmlUnionImpl
implements STSignedHpsMeasure,
XmlInteger,
STUniversalMeasure {
    private static final long serialVersionUID = 1L;

    public STSignedHpsMeasureImpl(SchemaType sType) {
        super(sType, false);
    }

    protected STSignedHpsMeasureImpl(SchemaType sType, boolean b) {
        super(sType, b);
    }
}


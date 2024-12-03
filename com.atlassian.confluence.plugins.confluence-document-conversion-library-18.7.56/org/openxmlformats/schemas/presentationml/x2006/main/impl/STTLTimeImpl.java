/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.values.XmlUnionImpl;
import org.openxmlformats.schemas.presentationml.x2006.main.STTLTime;
import org.openxmlformats.schemas.presentationml.x2006.main.STTLTimeIndefinite;

public class STTLTimeImpl
extends XmlUnionImpl
implements STTLTime,
XmlUnsignedInt,
STTLTimeIndefinite {
    private static final long serialVersionUID = 1L;

    public STTLTimeImpl(SchemaType sType) {
        super(sType, false);
    }

    protected STTLTimeImpl(SchemaType sType, boolean b) {
        super(sType, b);
    }
}


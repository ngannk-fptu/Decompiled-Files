/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlENTITY;
import org.apache.xmlbeans.impl.values.JavaStringHolderEx;

public class XmlEntityImpl
extends JavaStringHolderEx
implements XmlENTITY {
    public XmlEntityImpl() {
        super(XmlENTITY.type, false);
    }

    public XmlEntityImpl(SchemaType type, boolean complex) {
        super(type, complex);
    }
}


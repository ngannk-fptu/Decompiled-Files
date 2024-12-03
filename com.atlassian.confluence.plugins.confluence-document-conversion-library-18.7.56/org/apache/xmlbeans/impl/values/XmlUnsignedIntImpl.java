/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.values.JavaLongHolderEx;

public class XmlUnsignedIntImpl
extends JavaLongHolderEx
implements XmlUnsignedInt {
    public XmlUnsignedIntImpl() {
        super(XmlUnsignedInt.type, false);
    }

    public XmlUnsignedIntImpl(SchemaType type, boolean complex) {
        super(type, complex);
    }
}


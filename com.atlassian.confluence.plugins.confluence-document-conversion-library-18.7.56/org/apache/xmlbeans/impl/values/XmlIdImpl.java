/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.impl.values.JavaStringHolderEx;

public class XmlIdImpl
extends JavaStringHolderEx
implements XmlID {
    public XmlIdImpl() {
        super(XmlID.type, false);
    }

    public XmlIdImpl(SchemaType type, boolean complex) {
        super(type, complex);
    }
}


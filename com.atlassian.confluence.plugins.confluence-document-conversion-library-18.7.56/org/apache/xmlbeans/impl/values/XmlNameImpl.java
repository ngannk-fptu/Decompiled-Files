/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlName;
import org.apache.xmlbeans.impl.values.JavaStringHolderEx;

public class XmlNameImpl
extends JavaStringHolderEx
implements XmlName {
    public XmlNameImpl() {
        super(XmlName.type, false);
    }

    public XmlNameImpl(SchemaType type, boolean complex) {
        super(type, complex);
    }
}


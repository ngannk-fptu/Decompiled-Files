/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlNonPositiveInteger;
import org.apache.xmlbeans.impl.values.JavaIntegerHolderEx;

public class XmlNonPositiveIntegerImpl
extends JavaIntegerHolderEx
implements XmlNonPositiveInteger {
    public XmlNonPositiveIntegerImpl() {
        super(XmlNonPositiveInteger.type, false);
    }

    public XmlNonPositiveIntegerImpl(SchemaType type, boolean complex) {
        super(type, complex);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlNonNegativeInteger;
import org.apache.xmlbeans.impl.values.JavaIntegerHolderEx;

public class XmlNonNegativeIntegerImpl
extends JavaIntegerHolderEx
implements XmlNonNegativeInteger {
    public XmlNonNegativeIntegerImpl() {
        super(XmlNonNegativeInteger.type, false);
    }

    public XmlNonNegativeIntegerImpl(SchemaType type, boolean complex) {
        super(type, complex);
    }
}


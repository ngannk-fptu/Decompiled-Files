/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlNormalizedString;
import org.apache.xmlbeans.impl.values.JavaStringHolderEx;

public class XmlNormalizedStringImpl
extends JavaStringHolderEx
implements XmlNormalizedString {
    public XmlNormalizedStringImpl() {
        super(XmlNormalizedString.type, false);
    }

    public XmlNormalizedStringImpl(SchemaType type, boolean complex) {
        super(type, complex);
    }
}


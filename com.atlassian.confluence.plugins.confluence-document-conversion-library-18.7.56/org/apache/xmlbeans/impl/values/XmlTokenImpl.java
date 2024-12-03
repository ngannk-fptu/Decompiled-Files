/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.values.JavaStringHolderEx;

public class XmlTokenImpl
extends JavaStringHolderEx
implements XmlToken {
    public XmlTokenImpl() {
        super(XmlToken.type, false);
    }

    public XmlTokenImpl(SchemaType type, boolean complex) {
        super(type, complex);
    }
}


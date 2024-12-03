/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlTime;
import org.apache.xmlbeans.impl.values.JavaGDateHolderEx;

public class XmlTimeImpl
extends JavaGDateHolderEx
implements XmlTime {
    public XmlTimeImpl() {
        super(XmlTime.type, false);
    }

    public XmlTimeImpl(SchemaType type, boolean complex) {
        super(type, complex);
    }
}


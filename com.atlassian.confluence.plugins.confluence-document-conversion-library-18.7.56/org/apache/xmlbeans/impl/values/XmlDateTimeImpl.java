/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlDateTime;
import org.apache.xmlbeans.impl.values.JavaGDateHolderEx;

public class XmlDateTimeImpl
extends JavaGDateHolderEx
implements XmlDateTime {
    public XmlDateTimeImpl() {
        super(XmlDateTime.type, false);
    }

    public XmlDateTimeImpl(SchemaType type, boolean complex) {
        super(type, complex);
    }
}


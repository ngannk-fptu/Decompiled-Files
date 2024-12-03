/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlGDay;
import org.apache.xmlbeans.impl.values.JavaGDateHolderEx;

public class XmlGDayImpl
extends JavaGDateHolderEx
implements XmlGDay {
    public XmlGDayImpl() {
        super(XmlGDay.type, false);
    }

    public XmlGDayImpl(SchemaType type, boolean complex) {
        super(type, complex);
    }
}


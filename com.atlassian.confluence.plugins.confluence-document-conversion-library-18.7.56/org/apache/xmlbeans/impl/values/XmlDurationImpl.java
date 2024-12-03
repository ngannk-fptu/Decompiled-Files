/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlDuration;
import org.apache.xmlbeans.impl.values.JavaGDurationHolderEx;

public class XmlDurationImpl
extends JavaGDurationHolderEx
implements XmlDuration {
    public XmlDurationImpl() {
        super(XmlDuration.type, false);
    }

    public XmlDurationImpl(SchemaType type, boolean complex) {
        super(type, complex);
    }
}


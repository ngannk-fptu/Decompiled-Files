/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlENTITIES;
import org.apache.xmlbeans.impl.values.XmlListImpl;

public class XmlEntitiesImpl
extends XmlListImpl
implements XmlENTITIES {
    public XmlEntitiesImpl() {
        super(XmlENTITIES.type, false);
    }

    public XmlEntitiesImpl(SchemaType type, boolean complex) {
        super(type, complex);
    }
}


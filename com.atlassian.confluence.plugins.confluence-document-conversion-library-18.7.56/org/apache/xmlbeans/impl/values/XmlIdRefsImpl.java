/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlIDREFS;
import org.apache.xmlbeans.impl.values.XmlListImpl;

public class XmlIdRefsImpl
extends XmlListImpl
implements XmlIDREFS {
    public XmlIdRefsImpl() {
        super(XmlIDREFS.type, false);
    }

    public XmlIdRefsImpl(SchemaType type, boolean complex) {
        super(type, complex);
    }
}


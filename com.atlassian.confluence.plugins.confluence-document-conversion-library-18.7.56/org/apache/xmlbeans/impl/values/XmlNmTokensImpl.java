/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlNMTOKENS;
import org.apache.xmlbeans.impl.values.XmlListImpl;

public class XmlNmTokensImpl
extends XmlListImpl
implements XmlNMTOKENS {
    public XmlNmTokensImpl() {
        super(XmlNMTOKENS.type, false);
    }

    public XmlNmTokensImpl(SchemaType type, boolean complex) {
        super(type, complex);
    }
}


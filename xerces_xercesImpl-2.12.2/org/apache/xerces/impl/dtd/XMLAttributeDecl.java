/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dtd;

import org.apache.xerces.impl.dtd.XMLSimpleType;
import org.apache.xerces.xni.QName;

public class XMLAttributeDecl {
    public final QName name = new QName();
    public final XMLSimpleType simpleType = new XMLSimpleType();
    public boolean optional;

    public void setValues(QName qName, XMLSimpleType xMLSimpleType, boolean bl) {
        this.name.setValues(qName);
        this.simpleType.setValues(xMLSimpleType);
        this.optional = bl;
    }

    public void clear() {
        this.name.clear();
        this.simpleType.clear();
        this.optional = false;
    }
}


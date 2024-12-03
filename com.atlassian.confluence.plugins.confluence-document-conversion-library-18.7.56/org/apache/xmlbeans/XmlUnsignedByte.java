/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlUnsignedShort;
import org.apache.xmlbeans.impl.schema.XmlObjectFactory;

public interface XmlUnsignedByte
extends XmlUnsignedShort {
    public static final XmlObjectFactory<XmlUnsignedByte> Factory = new XmlObjectFactory("_BI_unsignedByte");
    public static final SchemaType type = Factory.getType();

    public short getShortValue();

    public void setShortValue(short var1);
}


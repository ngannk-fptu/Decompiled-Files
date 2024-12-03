/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlShort;
import org.apache.xmlbeans.impl.schema.XmlObjectFactory;

public interface XmlByte
extends XmlShort {
    public static final XmlObjectFactory<XmlByte> Factory = new XmlObjectFactory("_BI_byte");
    public static final SchemaType type = Factory.getType();

    public byte getByteValue();

    public void setByteValue(byte var1);
}


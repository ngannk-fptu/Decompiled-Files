/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.impl.schema.XmlObjectFactory;

public interface XmlShort
extends XmlInt {
    public static final XmlObjectFactory<XmlShort> Factory = new XmlObjectFactory("_BI_short");
    public static final SchemaType type = Factory.getType();

    public short getShortValue();

    public void setShortValue(short var1);
}


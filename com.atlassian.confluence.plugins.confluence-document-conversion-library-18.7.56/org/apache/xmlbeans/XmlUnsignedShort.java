/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.XmlObjectFactory;

public interface XmlUnsignedShort
extends XmlUnsignedInt {
    public static final XmlObjectFactory<XmlUnsignedShort> Factory = new XmlObjectFactory("_BI_unsignedShort");
    public static final SchemaType type = Factory.getType();

    public int getIntValue();

    public void setIntValue(int var1);
}


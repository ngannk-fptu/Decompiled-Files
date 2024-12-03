/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlLong;
import org.apache.xmlbeans.impl.schema.XmlObjectFactory;

public interface XmlInt
extends XmlLong {
    public static final XmlObjectFactory<XmlInt> Factory = new XmlObjectFactory("_BI_int");
    public static final SchemaType type = Factory.getType();

    public int getIntValue();

    public void setIntValue(int var1);
}


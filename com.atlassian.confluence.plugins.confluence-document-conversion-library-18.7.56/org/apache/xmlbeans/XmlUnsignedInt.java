/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlUnsignedLong;
import org.apache.xmlbeans.impl.schema.XmlObjectFactory;

public interface XmlUnsignedInt
extends XmlUnsignedLong {
    public static final XmlObjectFactory<XmlUnsignedInt> Factory = new XmlObjectFactory("_BI_unsignedInt");
    public static final SchemaType type = Factory.getType();

    public long getLongValue();

    public void setLongValue(long var1);
}


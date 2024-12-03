/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlInteger;
import org.apache.xmlbeans.impl.schema.XmlObjectFactory;

public interface XmlLong
extends XmlInteger {
    public static final XmlObjectFactory<XmlLong> Factory = new XmlObjectFactory("_BI_long");
    public static final SchemaType type = Factory.getType();

    public long getLongValue();

    public void setLongValue(long var1);
}


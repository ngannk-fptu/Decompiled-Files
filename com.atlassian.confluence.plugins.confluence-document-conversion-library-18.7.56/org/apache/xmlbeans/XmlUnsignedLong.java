/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlNonNegativeInteger;
import org.apache.xmlbeans.impl.schema.XmlObjectFactory;

public interface XmlUnsignedLong
extends XmlNonNegativeInteger {
    public static final XmlObjectFactory<XmlUnsignedLong> Factory = new XmlObjectFactory("_BI_unsignedLong");
    public static final SchemaType type = Factory.getType();
}


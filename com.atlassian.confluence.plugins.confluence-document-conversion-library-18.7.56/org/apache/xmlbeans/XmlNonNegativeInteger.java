/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlInteger;
import org.apache.xmlbeans.impl.schema.XmlObjectFactory;

public interface XmlNonNegativeInteger
extends XmlInteger {
    public static final XmlObjectFactory<XmlNonNegativeInteger> Factory = new XmlObjectFactory("_BI_nonNegativeInteger");
    public static final SchemaType type = Factory.getType();
}


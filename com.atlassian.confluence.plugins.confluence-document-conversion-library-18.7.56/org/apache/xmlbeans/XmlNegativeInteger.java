/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlNonPositiveInteger;
import org.apache.xmlbeans.impl.schema.XmlObjectFactory;

public interface XmlNegativeInteger
extends XmlNonPositiveInteger {
    public static final XmlObjectFactory<XmlNegativeInteger> Factory = new XmlObjectFactory("_BI_negativeInteger");
    public static final SchemaType type = Factory.getType();
}


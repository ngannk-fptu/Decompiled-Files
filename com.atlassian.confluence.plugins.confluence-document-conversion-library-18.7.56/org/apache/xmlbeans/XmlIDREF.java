/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlNCName;
import org.apache.xmlbeans.impl.schema.XmlObjectFactory;

public interface XmlIDREF
extends XmlNCName {
    public static final XmlObjectFactory<XmlIDREF> Factory = new XmlObjectFactory("_BI_IDREF");
    public static final SchemaType type = Factory.getType();
}


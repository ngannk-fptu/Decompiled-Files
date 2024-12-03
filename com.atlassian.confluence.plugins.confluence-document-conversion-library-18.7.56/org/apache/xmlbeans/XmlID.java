/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlNCName;
import org.apache.xmlbeans.impl.schema.XmlObjectFactory;

public interface XmlID
extends XmlNCName {
    public static final XmlObjectFactory<XmlID> Factory = new XmlObjectFactory("_BI_ID");
    public static final SchemaType type = Factory.getType();
}


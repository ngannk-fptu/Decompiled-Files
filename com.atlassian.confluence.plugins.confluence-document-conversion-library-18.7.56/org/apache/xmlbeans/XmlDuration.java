/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import org.apache.xmlbeans.GDuration;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.impl.schema.XmlObjectFactory;

public interface XmlDuration
extends XmlAnySimpleType {
    public static final XmlObjectFactory<XmlDuration> Factory = new XmlObjectFactory("_BI_duration");
    public static final SchemaType type = Factory.getType();

    public GDuration getGDurationValue();

    public void setGDurationValue(GDuration var1);
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.impl.schema.XmlObjectFactory;

public interface XmlIDREFS
extends XmlAnySimpleType {
    public static final XmlObjectFactory<XmlIDREFS> Factory = new XmlObjectFactory("_BI_IDREFS");
    public static final SchemaType type = Factory.getType();

    public List<?> getListValue();

    public List<? extends XmlAnySimpleType> xgetListValue();

    public void setListValue(List<?> var1);
}

